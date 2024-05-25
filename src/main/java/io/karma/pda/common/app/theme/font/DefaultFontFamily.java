/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app.theme.font;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.theme.font.FontCharSet;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.app.theme.font.FontStyle;
import io.karma.pda.api.common.app.theme.font.FontVariant;
import io.karma.pda.api.common.util.Exceptions;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
public final class DefaultFontFamily implements FontFamily {
    private final ResourceLocation name;
    private final Set<FontStyle> styles = Collections.synchronizedSet(EnumSet.noneOf(FontStyle.class));
    private final Map<FontStyle, DefaultFont> fonts = Collections.synchronizedMap(new EnumMap<>(FontStyle.class));
    private Config config;

    public DefaultFontFamily(final ResourceLocation name) {
        this.name = name;
        ((ReloadableResourceManager) API.getResourceManager()).registerReloadListener(new ReloadListener());
    }

    private synchronized void reload(final ResourceManager manager) {
        try {
            final var configPath = String.format("fonts/%s.json", name.getPath());
            final var configLocation = new ResourceLocation(name.getNamespace(), configPath);
            API.getLogger().debug("Loading font family config from {}", configLocation);
            config = Objects.requireNonNull(JSONUtils.read(manager.getResourceOrThrow(configLocation), Config.class));
            if (config.version < Config.VERSION) {
                throw new IllegalStateException(String.format("Invalid font config version %d, expected at least %d",
                    config.version,
                    Config.VERSION));
            }
            styles.clear();
            styles.addAll(config.variants.keySet());

            API.getLogger().debug("Preloading default fonts for {}", configLocation);
            for (final var style : styles) {
                getFont(style, FontVariant.DEFAULT_SIZE);
            }
        }
        catch (Throwable error) {
            API.getLogger().error("Could not read font config {}: {}", name, Exceptions.toFancyString(error));
        }
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public synchronized String getDisplayName() {
        return config.name;
    }

    @Override
    public synchronized Set<FontStyle> getStyles() {
        return styles;
    }

    @Override
    public synchronized FontVariant getFont(final FontStyle style, final float size) {
        if (size < 0F) {
            throw new IllegalArgumentException("Size must be greater than or equal to zero");
        }
        return new DefaultFontVariant(fonts.computeIfAbsent(style, s -> {
            final var variant = config.variants.get(s);
            final var locationString = variant.location;
            final var location = ResourceLocation.tryParse(locationString);
            if (location == null) {
                throw new IllegalStateException(String.format("Malformed font location: %s", locationString));
            }
            final var font = new DefaultFont(this, config.supportedCharSet, location);
            font.setVariationAxes(variant.variationAxes);
            return font;
        }), style, size);
    }

    @Override
    public synchronized FontVariant getFont(final FontStyle style, final float size,
                                            final Object2FloatMap<String> variationAxes) {
        if (size < 0F) {
            throw new IllegalArgumentException("Size must be greater than or equal to zero");
        }
        return new DefaultFontVariant(fonts.computeIfAbsent(style, s -> {
            final var variant = config.variants.get(s);
            final var locationString = variant.location;
            final var location = ResourceLocation.tryParse(locationString);
            if (location == null) {
                throw new IllegalStateException(String.format("Malformed font location: %s", locationString));
            }
            final var font = new DefaultFont(this, config.supportedCharSet, location);
            font.setVariationAxes(variant.variationAxes);
            return font;
        }), style, size); // TODO: finish implementing this
    }

    public enum DefaultCharSet implements FontCharSet {
        // @formatter:off
        ASCII         (IntIntPair.of(0x20, 0x7E)),
        EXTENDED_ASCII(IntIntPair.of(0x20, 0x7E), IntIntPair.of(0xA0, 0xFF)),
        UNICODE       (
            IntIntPair.of(0x0020, 0x007E), // ASCII
            IntIntPair.of(0x00A0, 0x00FF), // Extended ASCII
            IntIntPair.of(0x0100, 0x017F), // Latin Extended-A
            IntIntPair.of(0x0370, 0x03FF), // Greek and Coptic
            IntIntPair.of(0x0400, 0x04FF), // Cyrillic
            IntIntPair.of(0x0590, 0x05FF), // Hebrew
            IntIntPair.of(0x0600, 0x06FF), // Arabic
            IntIntPair.of(0x0900, 0x097F), // Devanagari
            IntIntPair.of(0x4E00, 0x9FFF)  // Chinese, Japanese, Korean (CJK) Unified Ideographs
        );
        // @formatter:on

        private final IntIntPair[] ranges;

        DefaultCharSet(final IntIntPair... ranges) {
            this.ranges = ranges;
        }

        @Override
        public IntIntPair[] getRanges() {
            return ranges;
        }
    }

    public static final class Config {
        @JsonIgnore
        public static final int VERSION = 1;
        @JsonProperty
        public int version = VERSION;
        @JsonProperty
        public String name;
        @JsonProperty("default_size")
        public float defaultSize;
        @JsonProperty("supported_char_set")
        public DefaultCharSet supportedCharSet;
        @JsonProperty
        public HashMap<FontStyle, Variant> variants = new HashMap<>();

        public static final class Variant {
            @JsonProperty
            public String location;
            @JsonProperty("variation_axes")
            public Object2FloatOpenHashMap<String> variationAxes = new Object2FloatOpenHashMap<>();
        }
    }

    private final class ReloadListener implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(final @NotNull ResourceManager manager) {
            PDAMod.LOGGER.debug("Reloading font family {}", name);
            DefaultFontFamily.this.reload(manager);
        }
    }
}
