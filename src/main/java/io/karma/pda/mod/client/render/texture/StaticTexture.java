/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.texture;

import io.karma.pda.api.dispose.Disposable;
import io.karma.pda.api.reload.Reloadable;
import io.karma.pda.api.util.LogMarkers;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.util.TextureUtils;
import io.karma.pda.mod.reload.DefaultReloadHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 25/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class StaticTexture implements Disposable, Reloadable {
    private static final HashMap<ResourceLocation, StaticTexture> CACHE = new HashMap<>();
    private final ResourceLocation location;
    private int id = -1;

    private StaticTexture(final ResourceLocation location) {
        this.location = location;
        PDAMod.DISPOSITION_HANDLER.register(this);
        DefaultReloadHandler.INSTANCE.register(this);
    }

    public static StaticTexture get(final ResourceLocation location) {
        return CACHE.computeIfAbsent(location, StaticTexture::new);
    }

    public int getId() {
        return id;
    }

    @Override
    public void dispose() {
        GL11.glDeleteTextures(id);
    }

    @Override
    public void prepareReload(final ResourceManager manager) {
        if (id != -1) {
            GL11.glDeleteTextures(id);
        }
        id = TextureUtils.createDefaultTexture();
    }

    @Override
    public void reload(final ResourceManager manager) {
        try (final var stream = manager.open(location)) {
            TextureUtils.uploadTexture(id, ImageIO.read(stream));
            PDAMod.LOGGER.debug(LogMarkers.RENDERER, "Uploaded image {} to texture {}", location, id);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error(LogMarkers.RENDERER, "Could not read static texture", error);
        }
    }

    @Override
    public String toString() {
        return String.format("StaticTexture[%s]", location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StaticTexture texture)) {
            return false;
        }
        return location.equals(texture.location);
    }
}
