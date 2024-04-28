/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.app.theme.font;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.theme.font.Font;
import io.karma.pda.api.common.app.theme.font.FontFamily;
import io.karma.pda.api.common.app.theme.font.FontStyle;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
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
    private final Map<FontStyle, Font> fonts = Collections.synchronizedMap(new EnumMap<>(FontStyle.class));
    private Config config;

    public DefaultFontFamily(final ResourceLocation name) {
        this.name = name;
        ((ReloadableResourceManager) API.getResourceManager()).registerReloadListener(new ReloadListener());
    }

    private synchronized void loadConfig() {
        final var configPath = String.format("fonts/%s.json", name.getPath());
        final var configLocation = new ResourceLocation(name.getNamespace(), configPath);
        config = Objects.requireNonNull(JSONUtils.read(configLocation, Config.class));
        if (config.version < Config.VERSION) {
            throw new IllegalStateException(String.format("Invalid font config version %d, expected at least %d",
                config.version,
                Config.VERSION));
        }
        styles.clear();
        styles.addAll(config.variants.keySet());
        PDAMod.LOGGER.debug("Loaded font config for family {}", name);
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
    public synchronized Font getFont(final FontStyle style, final float size) {
        if (size < 0F) {
            throw new IllegalArgumentException("Size must be greater than or equal to zero");
        }
        return fonts.computeIfAbsent(style, s -> {
            final var shortLocation = Objects.requireNonNull(ResourceLocation.tryParse(config.variants.get(s)));
            final var path = String.format("fonts/%s.ttf", shortLocation.getPath());
            return new DefaultFont(this, new ResourceLocation(shortLocation.getNamespace(), path), s, size);
        });
    }

    private final class ReloadListener implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(final @NotNull ResourceManager manager) {
            loadConfig();
        }
    }

    public static final class Config {
        @JsonIgnore
        public static final int VERSION = 1;
        @JsonProperty
        public int version = VERSION;
        @JsonProperty
        public String name;
        @JsonProperty
        public HashMap<FontStyle, String> variants = new HashMap<>();
    }
}
