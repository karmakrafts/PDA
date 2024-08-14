/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.api.util.ToBooleanBiFunction;
import io.karma.pda.mod.PDAMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexander Hinze
 * @since 14/06/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultShaderPreProcessor implements ShaderPreProcessor {
    private static final ThreadLocal<DefaultShaderPreProcessor> INSTANCE = ThreadLocal.withInitial(
        DefaultShaderPreProcessor::new);
    private static final Pattern SPECIAL_CONST_PATTERN = Pattern.compile(
        "\\b(special)\\s+(const)\\s+(\\w+)\\s+(\\w+)(\\s*?=\\s*?([\\w.\"'+\\-*/%]+))?\\s*?;");
    private static final Pattern INCLUDE_PATTERN = Pattern.compile(
        "(#include)\\s*?((\\s*?<((\\w+(:))?[\\w/._\\-]+)\\s*?>)|(\"\\s*?([\\w/._\\-]+)\\s*?\"))");

    // @formatter:off
    private DefaultShaderPreProcessor() {}
    // @formatter:on

    private static void processGreedy(final StringBuffer buffer, final Pattern pattern,
                                      final ToBooleanBiFunction<Matcher, StringBuffer> callback) {
        var matcher = pattern.matcher(buffer);
        while (matcher.find()) {
            if (!callback.apply(matcher, buffer)) {
                break;
            }
            matcher = pattern.matcher(buffer);
        }
    }

    public static DefaultShaderPreProcessor getInstance() {
        return INSTANCE.get();
    }

    private static String resolveRelativePath(final String path) {
        final var stack = new Stack<String>();
        final var parts = path.split("/");
        for (final var part : parts) {
            if (part.equals("..")) {
                stack.pop();
                continue;
            }
            stack.push(part);
        }
        return String.join("/", stack);
    }

    private static String getParentPath(final String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    private void minify(final StringBuffer buffer) {
        final var unstripped = buffer.toString();
        buffer.delete(0, buffer.length());
        var isBlockComment = false;
        var isLineComment = false;
        var skipNext = false;
        for (var i = 0; i < unstripped.length(); i++) {
            final var c = unstripped.charAt(i);
            // Pop/skip logic
            if (skipNext) {
                skipNext = false;
                continue;
            }
            final var hasNext = i < unstripped.length() - 1;
            final var next = hasNext ? unstripped.charAt(i + 1) : ' ';
            if (isBlockComment) {
                if (c == '*' && hasNext && next == '/') {
                    isBlockComment = false;
                    skipNext = true;
                }
                continue;
            }
            else if (isLineComment) {
                if (c == '\n') {
                    isLineComment = false;
                }
                continue;
            }
            // Push logic
            if (c == '/' && hasNext && next == '*') {
                isBlockComment = true;
                continue;
            }
            if (c == '/' && hasNext && next == '/') {
                isLineComment = true;
                continue;
            }
            buffer.append(c);
        }
        final var lines = buffer.toString().split("\n");
        buffer.delete(0, buffer.length());
        for (final var line : lines) {
            if (line.isBlank()) { // Filter out any empty lines
                continue;
            }
            buffer.append(line).append("\n");
        }
    }

    private String expandIncludesRecursively(final ResourceLocation location, final String source,
                                             final Function<ResourceLocation, String> loader,
                                             final HashSet<ResourceLocation> includedLocations) {
        final var buffer = new StringBuffer(source);
        processGreedy(buffer, INCLUDE_PATTERN, (matcher, currentBuffer) -> {
            ResourceLocation targetLocation;
            final var relativePath = matcher.group(8);
            if (relativePath != null) { // We have a relative include path
                PDAMod.LOGGER.debug(LogMarkers.RENDERER,
                    "Processing relative include '{}' in {}",
                    relativePath,
                    location);
                final var parentPath = getParentPath(location.getPath());
                final var joinedPath = String.format("%s/%s", parentPath, relativePath);
                targetLocation = new ResourceLocation(location.getNamespace(), resolveRelativePath(joinedPath));
            }
            else {
                final var path = matcher.group(4); // Otherwise grab the absolute one
                PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Processing absolute include '{}' in {}", path, location);
                targetLocation = ResourceLocation.tryParse(path);
                if (targetLocation == null) {
                    throw new IllegalStateException(String.format("Malformed include location '%s'", path));
                }
            }
            if (includedLocations.contains(targetLocation)) {
                PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Include from {} already expanded, skipping", targetLocation);
                return true;
            }
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Loading include from {}", targetLocation);
            currentBuffer.replace(matcher.start(),
                matcher.end(),
                expandIncludesRecursively(targetLocation, loader.apply(targetLocation), loader, includedLocations));
            includedLocations.add(targetLocation);
            return true;
        });
        return buffer.toString();
    }

    private void processIncludes(final ResourceLocation location, final StringBuffer buffer,
                                 final Function<ResourceLocation, String> loader) {
        final var source = buffer.toString();
        buffer.delete(0, buffer.length());
        buffer.append(expandIncludesRecursively(location, source, loader, new HashSet<>()));
    }

    private void processSpecializationConstants(final ResourceLocation location, final Map<String, Object> constants,
                                                final StringBuffer buffer) {
        processGreedy(buffer, SPECIAL_CONST_PATTERN, (matcher, currentBuffer) -> {
            final var replacement = new StringBuilder();
            final var type = matcher.group(3);
            final var name = matcher.group(4);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Processing constant '{}' in {}", name, location);
            replacement.append(String.format("const %s %s", type, name));
            final var defaultValue = matcher.group(6);
            final var value = constants.get(name);
            if (defaultValue != null) { // We have a default value
                replacement.append(String.format(" = %s", Objects.requireNonNullElse(value, defaultValue)));
            }
            else {
                if (value == null) {
                    throw new IllegalStateException(String.format("Value for constant '%s' in object %s not defined",
                        name,
                        location));
                }
                replacement.append(String.format(" = %s", value));
            }
            replacement.append(';');
            currentBuffer.replace(matcher.start(), matcher.end(), replacement.toString());
            return true;
        });
    }

    private void processDefines(final Object2IntMap<String> defines, final StringBuffer buffer) {
        final var defineBlock = new StringBuilder();
        for (final var define : defines.object2IntEntrySet()) {
            defineBlock.append(String.format("#define %s %d\n", define.getKey(), define.getIntValue()));
        }
        buffer.insert(buffer.indexOf("\n", buffer.indexOf("#version")) + 1,
            defineBlock); // Always skip the first line for the version
    }

    @Override
    public String process(final String source, final ShaderProgram program, final ShaderObject object,
                          final Function<ResourceLocation, String> loader) {
        final var buffer = new StringBuffer(source);
        final var location = object.getLocation();
        processIncludes(location, buffer, loader);
        processSpecializationConstants(location, program.getConstants(), buffer);
        processDefines(program.getDefines(), buffer);
        minify(buffer);
        return buffer.toString();
    }
}
