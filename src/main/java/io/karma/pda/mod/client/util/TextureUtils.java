/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.util;

import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;

/**
 * @author Alexander Hinze
 * @since 08/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class TextureUtils {
    // @formatter:off
    private TextureUtils() {}
    // @formatter:on

    public static int createTexture() {
        final var id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return id;
    }

    public static int[] toArray(final BufferedImage image) {
        final var width = image.getWidth();
        return image.getRGB(0, 0, width, image.getHeight(), null, 0, width);
    }

    public static void dump(final BufferedImage image, final ResourceLocation location) {
        try {
            final var directory = FMLLoader.getGamePath().resolve("pda");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            final var fileName = String.format("%s_%s", location.getNamespace(), location.getPath().replace('/', '_'));
            final var filePath = directory.resolve(fileName);
            Files.deleteIfExists(filePath);
            try (final var outStream = Files.newOutputStream(filePath)) {
                ImageIO.write(image, "PNG", outStream);
            }
            PDAMod.LOGGER.debug("Dumped image for {} to {}", location, filePath);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not dump image {}: {}", location, Exceptions.toFancyString(error));
        }
    }
}
