/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.util;

import io.karma.pda.api.util.Exceptions;
import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.render.DSA;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;

/**
 * @author Alexander Hinze
 * @since 08/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class TextureUtils {
    private static int previousUnpackAlignment;
    private static int previousUnpackRowLength;
    private static int previousUnpackSkipPixels;
    private static int previousUnpackSkipRows;

    // @formatter:off
    private TextureUtils() {}
    // @formatter:on

    public static void setUnpackAlignment(final int alignment) {
        previousUnpackAlignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        previousUnpackRowLength = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        previousUnpackSkipPixels = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        previousUnpackSkipRows = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, alignment);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
    }

    public static void restoreUnpackAlignment() {
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, previousUnpackAlignment);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, previousUnpackRowLength);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, previousUnpackSkipPixels);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, previousUnpackSkipRows);
    }

    public static int createDefaultTexture() {
        final var id = DSA.createTexture();
        DSA.texParameteri(id, setter -> {
            setter.accept(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            setter.accept(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            setter.accept(GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            setter.accept(GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        });
        return id;
    }

    public static void uploadTexture(final int texture, final Image image) {
        final var bufferedImage = buffer(image);
        setUnpackAlignment(1);
        DSA.texImage2D(texture,
            0,
            0,
            bufferedImage.getWidth(),
            bufferedImage.getHeight(),
            GL30.GL_RGBA8,
            GL30.GL_BGRA,
            GL30.GL_UNSIGNED_INT_8_8_8_8_REV,
            TextureUtils.toArray(bufferedImage));
        restoreUnpackAlignment();
        DSA.texParameteri(texture, setter -> {
            setter.accept(GL33.GL_TEXTURE_SWIZZLE_R, GL11.GL_BLUE);
            setter.accept(GL33.GL_TEXTURE_SWIZZLE_G, GL11.GL_GREEN);
            setter.accept(GL33.GL_TEXTURE_SWIZZLE_B, GL11.GL_RED);
            setter.accept(GL33.GL_TEXTURE_SWIZZLE_A, GL11.GL_ALPHA);
        });
    }

    public static int[] toArray(final BufferedImage image) {
        final var width = image.getWidth();
        return image.getRGB(0, 0, width, image.getHeight(), null, 0, width);
    }

    public static BufferedImage buffer(final Image image) {
        if (image instanceof BufferedImage bufferedImage) {
            return bufferedImage;
        }
        final var width = image.getWidth(null);
        final var height = image.getHeight(null);
        final var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final var graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        return bufferedImage;
    }

    public static void save(final Image image, final ResourceLocation location) {
        try {
            final var directory = FMLLoader.getGamePath().resolve("pda").resolve("textures");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            final var fileName = String.format("%s_%s", location.getNamespace(), location.getPath().replace('/', '_'));
            final var filePath = directory.resolve(fileName);
            Files.deleteIfExists(filePath);
            try (final var outStream = Files.newOutputStream(filePath)) {
                ImageIO.write(buffer(image), "PNG", outStream);
            }
            PDAMod.LOGGER.debug("Dumped image for {} to {}", location, filePath);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not dump image {}: {}", location, Exceptions.toFancyString(error));
        }
    }
}
