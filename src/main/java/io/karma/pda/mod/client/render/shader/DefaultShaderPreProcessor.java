/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.shader;

import io.karma.pda.api.client.render.shader.ShaderPreProcessor;
import io.karma.pda.api.client.render.shader.type.MatrixType;
import io.karma.pda.api.client.render.shader.type.ScalarType;
import io.karma.pda.api.client.render.shader.type.Type;
import io.karma.pda.api.client.render.shader.type.VectorType;
import io.karma.pda.api.util.ToBooleanBiFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
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
        "\\b(special)\\s+(const)\\s+(\\w+)\\s+(\\w+)(\\s*=\\s*([\\w.\"'+]+))?\\s*;");
    private static final Pattern INCLUDE_PATTERN = Pattern.compile(
        "^(#include)\\s+((<((\\w+(:))?[\\w/]+)>)|(\"([\\w/]+)\"))");
    private static final HashMap<String, Type> TYPES = new HashMap<>();

    static { // @formatter:off
        for(final var type : ScalarType.values()) {
            TYPES.put(type.getName(), type);
        }
        for(final var type : VectorType.values()) {
            TYPES.put(type.getName(), type);
        }
        for(final var type : MatrixType.values()) {
            TYPES.put(type.getName(), type);
        }
    } // @formatter:on

    // @formatter:off
    private DefaultShaderPreProcessor() {}
    // @formatter:on

    private static boolean isBoolean(final String value) {
        return value.equals("true") || value.equals("false");
    }

    private static boolean isIntegral(final String value) {
        for (var i = 0; i < value.length(); i++) {
            final var c = value.charAt(i);
            if (c >= 48 && c <= 57) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean isDecimal(final String value) {
        var pastFloatingPoint = false;
        var pastE = false;
        var pastSign = false;
        for (var i = 0; i < value.length(); i++) {
            final var c = value.charAt(i);
            final var isSign = c == '+' || c == '-';
            final var isE = c == 'e' || c == 'E';
            if ((pastSign && isSign) || (pastFloatingPoint && c == '.') || (pastE && isE)) {
                return false;
            }
            if (pastE && isSign) {
                pastSign = true;
                continue;
            }
            if (isE) {
                pastE = true;
                continue;
            }
            if (c == '.') {
                pastFloatingPoint = true;
                continue;
            }
            if (c >= 48 && c <= 57) {
                continue;
            }
            return false;
        }
        return true;
    }

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

    private void processIncludes(final String resourceName, final StringBuffer buffer) {
        processGreedy(buffer, INCLUDE_PATTERN, (matcher, currentBuffer) -> {
            String replacement = "";
            if (matcher.groupCount() >= 8) { // We have a relative include path
                final var path = matcher.group(8);
                LOGGER.debug("Processing relative include '{}' in {}", path, resourceName);
            }
            else {
                final var path = matcher.group(4); // Otherwise grab the absolute one
                LOGGER.debug("Processing absolute include '{}' in {}", path, resourceName);
            }
            buffer.replace(matcher.start(), matcher.end(), replacement);
            return true;
        });
    }

    private void processSpecializationConstants(final String resourceName, final StringBuffer buffer) {
        processGreedy(buffer, SPECIAL_CONST_PATTERN, (matcher, currentBuffer) -> {
            final var replacement = new StringBuilder();
            final var type = TYPES.get(matcher.group(3));
            final var name = matcher.group(4);
            LOGGER.debug("Processing constant '{}' in {}", name, resourceName);
            replacement.append(String.format("const %s %s", type.getName(), name));
            if (matcher.groupCount() >= 5) { // We have a default value
                final var defaultValue = matcher.group(6);
            }
            else {
            }
            replacement.append(';');
            buffer.replace(matcher.start(), matcher.end(), replacement.toString());
            return true;
        });
    }

    @Override
    public String process(final String resourceName, final String source) {
        final var buffer = new StringBuffer(source);
        processIncludes(resourceName, buffer);
        processSpecializationConstants(resourceName, buffer);
        return buffer.toString();
    }
}
