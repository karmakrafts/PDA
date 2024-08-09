/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.ToBooleanBiFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadLocal<DefaultShaderPreProcessor> INSTANCE = ThreadLocal.withInitial(
        DefaultShaderPreProcessor::new);
    private static final Pattern SPECIAL_CONST_PATTERN = Pattern.compile(
        "\\b(special)\\s+(const)\\s+(\\w+)\\s+(\\w+)(\\s*?=\\s*?([\\w.\"'+]+))?\\s*?;");
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
                continue;
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
        final var lines = buffer.toString().split("\n");
        buffer.delete(0, buffer.length());
        for (final var line : lines) {
            if (line.isBlank()) { // Filter out any empty lines
                continue;
            }
            buffer.append(line).append("\n");
        }
    }

    private String insertIncludesRecursively(final ResourceLocation location, final String source,
                                             final Function<ResourceLocation, String> loader) {
        final var buffer = new StringBuffer(source);
        processGreedy(buffer, INCLUDE_PATTERN, (matcher, currentBuffer) -> {
            ResourceLocation targetLocation;
            final var relativePath = matcher.group(8);
            if (relativePath != null) { // We have a relative include path
                LOGGER.debug("Processing relative include '{}' in {}", relativePath, location);
                final var parentPath = getParentPath(location.getPath());
                final var joinedPath = String.format("%s/%s", parentPath, relativePath);
                targetLocation = new ResourceLocation(location.getNamespace(), resolveRelativePath(joinedPath));
            }
            else {
                final var path = matcher.group(4); // Otherwise grab the absolute one
                LOGGER.debug("Processing absolute include '{}' in {}", path, location);
                targetLocation = ResourceLocation.tryParse(path);
                if (targetLocation == null) {
                    throw new IllegalStateException(String.format("Malformed include location '%s'", path));
                }
            }
            LOGGER.debug("Loading include from {}", targetLocation);
            currentBuffer.replace(matcher.start(),
                matcher.end(),
                insertIncludesRecursively(targetLocation, loader.apply(targetLocation), loader));
            return true;
        });
        return buffer.toString();
    }

    private void processIncludes(final ResourceLocation location, final StringBuffer buffer,
                                 final Function<ResourceLocation, String> loader) {
        final var source = buffer.toString();
        buffer.delete(0, buffer.length());
        buffer.append(insertIncludesRecursively(location, source, loader));
    }

    private void processSpecializationConstants(final ResourceLocation location, final Map<String, Object> constants,
                                                final StringBuffer buffer) {
        processGreedy(buffer, SPECIAL_CONST_PATTERN, (matcher, currentBuffer) -> {
            final var replacement = new StringBuilder();
            final var type = matcher.group(3);
            final var name = matcher.group(4);
            LOGGER.debug("Processing constant '{}' in {}", name, location);
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

    @Override
    public String process(final String source, final ShaderProgram program, final ShaderObject object,
                          final Function<ResourceLocation, String> loader) {
        final var buffer = new StringBuffer(source);
        final var location = object.getLocation();
        processIncludes(location, buffer, loader);
        processSpecializationConstants(location, program.getConstants(), buffer);
        minify(buffer);
        return buffer.toString();
    }
}
