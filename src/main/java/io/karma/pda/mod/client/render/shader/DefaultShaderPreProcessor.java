/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderObject;
import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.ShaderProgram;
import io.karma.pda.api.util.HashUtils;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.api.util.ToBooleanBiFunction;
import io.karma.pda.mod.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final Pattern GL_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final char[] ESCAPABLE_CHARS = "\\\"nt".toCharArray();
    private static final int PRINT_BUFFER_SIZE = 4096;

    // @formatter:off
    private DefaultShaderPreProcessor() {}
    // @formatter:on

    private static void processGreedy(final StringBuffer buffer,
                                      final Pattern pattern,
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

    private static CharSequence generateStringData(final CharSequence sequence) {
        final var builder = new StringBuilder();
        builder.append("uint[]{");
        final var literalLength = sequence.length();
        for (var j = 0; j < literalLength; j++) {
            builder.append("0x").append(Integer.toHexString(sequence.charAt(j)));
            if (j < literalLength - 1) {
                builder.append(',');
            }
        }
        builder.append('}');
        return builder;
    }

    private static void stripCommentsAndWhitespace(final StringBuffer buffer) {
        final var source = buffer.toString();
        buffer.delete(0, buffer.length());
        var isBlockComment = false;
        var isLineComment = false;
        var skipNext = false;
        for (var i = 0; i < source.length(); i++) {
            final var c = source.charAt(i);
            // Pop/skip logic
            if (skipNext) {
                skipNext = false;
                continue;
            }
            final var hasNext = i < source.length() - 1;
            final var next = hasNext ? source.charAt(i + 1) : ' ';
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

    private static String expandIncludesRecursively(final ResourceLocation location,
                                                    final String source,
                                                    final Function<ResourceLocation, String> loader,
                                                    final HashSet<ResourceLocation> includedLocations) {
        final var buffer = new StringBuffer(source);
        processGreedy(buffer, INCLUDE_PATTERN, (matcher, currentBuffer) -> {
            ResourceLocation targetLocation;
            final var relativePath = matcher.group(8);
            if (relativePath != null) { // We have a relative include path
                final var parentPath = getParentPath(location.getPath());
                final var joinedPath = String.format("%s/%s", parentPath, relativePath);
                targetLocation = new ResourceLocation(location.getNamespace(), resolveRelativePath(joinedPath));
            }
            else {
                final var path = matcher.group(4); // Otherwise grab the absolute one
                targetLocation = ResourceLocation.tryParse(path);
                if (targetLocation == null) {
                    throw new IllegalStateException(String.format("Malformed include location '%s'", path));
                }
            }
            if (includedLocations.contains(targetLocation)) {
                return true;
            }
            currentBuffer.replace(matcher.start(),
                matcher.end(),
                expandIncludesRecursively(targetLocation, loader.apply(targetLocation), loader, includedLocations));
            includedLocations.add(targetLocation);
            return true;
        });
        return buffer.toString();
    }

    private static void processIncludes(final ResourceLocation location,
                                        final StringBuffer buffer,
                                        final Function<ResourceLocation, String> loader) {
        final var source = buffer.toString();
        buffer.delete(0, buffer.length());
        buffer.append(expandIncludesRecursively(location, source, loader, new HashSet<>()));
    }

    private static void processSpecializationConstants(final ResourceLocation location,
                                                       final Map<String, Object> constants,
                                                       final StringBuffer buffer) {
        processGreedy(buffer, SPECIAL_CONST_PATTERN, (matcher, currentBuffer) -> {
            final var replacement = new StringBuilder();
            final var type = matcher.group(3);
            final var name = matcher.group(4);
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

    private static void processDefines(final Map<String, Object> defines, final StringBuffer buffer) {
        final var defineBlock = new StringBuilder();
        for (final var define : defines.entrySet()) {
            defineBlock.append(String.format("#define %s %s\n", define.getKey(), define.getValue()));
        }
        buffer.insert(buffer.indexOf("\n", buffer.indexOf("#version")) + 1,
            defineBlock); // Always skip the first line for the version
    }

    private static Map<String, Object> insertBuiltinDefines(final Map<String, Object> defines) {
        final var allDefines = new LinkedHashMap<>(defines);
        allDefines.put("__debug", PDAMod.IS_DEV_ENV ? 1 : 0);
        allDefines.put("__print_buffer_size", PRINT_BUFFER_SIZE);

        final var caps = GL.getCapabilities();
        allDefines.put("__bindless_texture_support", caps.GL_ARB_bindless_texture ? 1 : 0);
        allDefines.put("__ssbo_support", caps.GL_ARB_shader_storage_buffer_object ? 1 : 0);
        allDefines.put("__long_support", caps.GL_ARB_gpu_shader_int64 ? 1 : 0);
        allDefines.put("__double_support", caps.GL_ARB_gpu_shader_fp64 ? 1 : 0);

        final var glVersion = Objects.requireNonNull(GL11.glGetString(GL11.GL_VERSION));
        final var glVersionMatcher = GL_VERSION_PATTERN.matcher(glVersion);
        if (!glVersionMatcher.find()) {
            throw new IllegalStateException("Could not parse OpenGL version");
        }
        allDefines.put("__gl_major", glVersionMatcher.group(1));
        allDefines.put("__gl_minor", glVersionMatcher.group(2));
        allDefines.put("__gl_patch", glVersionMatcher.group(3));

        final var forgeVersion = ForgeVersion.getVersion().split("\\.");
        allDefines.put("__forge_major", forgeVersion[0]);
        allDefines.put("__forge_minor", forgeVersion[1]);
        allDefines.put("__forge_patch", forgeVersion[2]);

        return allDefines;
    }

    private static Map<String, Object> insertBuiltinConstants(final Map<String, Object> constants) {
        final var allConstants = new LinkedHashMap<>(constants);
        allConstants.put("PRINT_BUFFER_SIZE", PRINT_BUFFER_SIZE);
        return allConstants;
    }

    private static void save(final Path directory, final String fingerprint, final StringBuffer buffer) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            final var path = directory.resolve(String.format("%s.glsl", fingerprint));
            Files.deleteIfExists(path);
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Saving processed shader to cache at {}", path);
            try (final var writer = Files.newBufferedWriter(path)) {
                writer.append(buffer);
            }
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not save processed shader source", error);
        }
    }

    private static boolean load(final Path directory, final String fingerprint, final StringBuffer buffer) {
        try {
            if (!Files.exists(directory)) {
                return false;
            }
            final var path = directory.resolve(String.format("%s.glsl", fingerprint));
            if (!Files.exists(path)) {
                return false;
            }
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Loading processed shader source from cache at {}", path);
            buffer.delete(0, buffer.length());
            try (final var reader = Files.newBufferedReader(path)) {
                buffer.append(reader.lines().collect(Collectors.joining("\n")));
            }
            return true;
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not load processed shader source", error);
            return false;
        }
    }

    private static @Nullable String loadSourceFingerprint(final Path directory, final String fingerprint) {
        final var path = directory.resolve(String.format("%s.md5", fingerprint));
        if (!Files.exists(path)) {
            return null;
        }
        try (final var reader = Files.newBufferedReader(path)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not load shader source fingerprint", error);
            return null;
        }
    }

    private static void saveSourceFingerprint(final Path directory,
                                              final String fingerprint,
                                              final String sourceFingerprint) {
        try {
            final var path = directory.resolve(String.format("%s.md5", fingerprint));
            Files.deleteIfExists(path);
            try (final var writer = Files.newBufferedWriter(path)) {
                writer.write(sourceFingerprint);
            }
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not save shader source fingerprint", error);
        }
    }

    @Override
    public String process(final String source,
                          final ShaderProgram program,
                          final ShaderObject object,
                          final Function<ResourceLocation, String> loader) {
        final var buffer = new StringBuffer(source);
        final var location = object.getLocation();
        final var fingerprint = HashUtils.toFingerprint(program.hashCode(), object.hashCode());
        final var directory = FMLLoader.getGamePath().resolve("pda").resolve("shaders");

        final var sourceFingerprint = loadSourceFingerprint(directory, fingerprint);
        final var currentSourceFingerprint = HashUtils.toFingerprint(source);

        if (sourceFingerprint != null && sourceFingerprint.equals(currentSourceFingerprint) && load(directory,
            fingerprint,
            buffer)) {
            return buffer.toString();
        }

        processIncludes(location, buffer, loader);
        processSpecializationConstants(location, insertBuiltinConstants(program.getConstants()), buffer);
        processDefines(insertBuiltinDefines(program.getDefines()), buffer);
        stripCommentsAndWhitespace(buffer);
        save(directory, fingerprint, buffer);
        saveSourceFingerprint(directory, fingerprint, currentSourceFingerprint);

        return buffer.toString();
    }
}
