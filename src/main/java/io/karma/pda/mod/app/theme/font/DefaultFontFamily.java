/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.app.theme.font;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.karma.pda.api.API;
import io.karma.pda.api.app.theme.font.FontFamily;
import io.karma.pda.api.app.theme.font.FontStyle;
import io.karma.pda.api.app.theme.font.FontVariant;
import io.karma.pda.api.reload.ReloadPriority;
import io.karma.pda.api.reload.Reloadable;
import io.karma.pda.api.util.Exceptions;
import io.karma.pda.api.util.JSONUtils;
import io.karma.pda.mod.reload.DefaultReloadHandler;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.*;

/**
 * @author Alexander Hinze
 * @since 28/04/2024
 */
@ReloadPriority(49) // Right before FontRenderer
public final class DefaultFontFamily implements FontFamily, Reloadable {
    private final ResourceLocation name;
    private final Set<FontStyle> styles = Collections.synchronizedSet(EnumSet.noneOf(FontStyle.class));
    private final Map<FontStyle, DefaultFont> fonts = Collections.synchronizedMap(new EnumMap<>(FontStyle.class));
    private Config config;

    public DefaultFontFamily(final ResourceLocation name) {
        this.name = name;
        DefaultReloadHandler.INSTANCE.register(this);
    }

    @Override
    public synchronized void prepareReload(final ResourceManager manager) {
        try {
            final var configPath = String.format("fonts/%s.json", name.getPath());
            final var configLocation = new ResourceLocation(name.getNamespace(), configPath);
            API.getLogger().debug("Loading font family from {}", configLocation);
            config = Objects.requireNonNull(JSONUtils.read(manager.getResourceOrThrow(configLocation), Config.class));
            if (config.version < Config.VERSION) {
                throw new IllegalStateException(String.format("Invalid font config version %d, expected at least %d",
                    config.version,
                    Config.VERSION));
            }
            styles.clear();
            styles.addAll(config.variants.keySet());
            for (final var style : styles) {
                getFont(style, FontVariant.DEFAULT_SIZE);
            }
        }
        catch (Throwable error) {
            API.getLogger().error("Could not read font config {}: {}", name, Exceptions.toFancyString(error));
        }
    }

    @Override
    public void reload(final ResourceManager manager) {
    }

    @Override
    public float getDistanceFieldRange() {
        return config.sdfRange;
    }

    @Override
    public int getGlyphSpriteBorder() {
        return config.glyphSpriteBorder;
    }

    @Override
    public int getGlyphSpriteSize() {
        return config.glyphSpriteSize;
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
    public synchronized FontVariant getFont(final FontStyle style,
                                            final float size,
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

    public static final class Config {
        @JsonIgnore
        public static final int VERSION = 1;
        @JsonProperty
        public int version = VERSION;
        @JsonProperty
        public String name;
        @JsonProperty("sdf_range")
        public float sdfRange = 4F;
        @JsonProperty("glyph_sprite_size")
        public int glyphSpriteSize = 32;
        @JsonProperty("glyph_sprite_border")
        public int glyphSpriteBorder = 2;
        @JsonProperty("supported_char_set")
        public DefaultCharSet supportedCharSet = DefaultCharSet.EXTENDED_ASCII;
        @JsonProperty
        public HashMap<FontStyle, Variant> variants = new HashMap<>();

        public static final class Variant {
            @JsonProperty
            public String location;
            @JsonProperty("variation_axes")
            public Object2FloatOpenHashMap<String> variationAxes = new Object2FloatOpenHashMap<>();
        }
    }
}
