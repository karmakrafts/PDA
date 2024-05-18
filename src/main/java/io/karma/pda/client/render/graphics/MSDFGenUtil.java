/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import net.minecraft.util.Mth;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.msdfgen.*;

import java.awt.image.BufferedImage;

/**
 * This class provides general utilities for working with msdfgen bitmaps
 * and transforming them into more workable formats.
 *
 * @author Alexander Hinze
 */
public final class MSDFGenUtil {
    // @formatter:off
    private MSDFGenUtil() {}
    // @formatter:on

    public static void scaleShape(final long shape, final double scale) {
        final var stack = MemoryStack.stackGet();
        final var previousSP = stack.getPointer();

        final var countBuffer = stack.mallocPointer(1);
        throwIfError(MSDFGen.msdf_shape_get_contour_count(shape, countBuffer));
        final var contourCount = countBuffer.get();
        final var addressBuffer = stack.mallocPointer(1);
        final var pos = MSDFGenVector2.malloc(1, stack);
        for (long i = 0; i < contourCount; i++) {
            addressBuffer.rewind();
            throwIfError(MSDFGen.msdf_shape_get_contour(shape, i, addressBuffer));
            final var contour = Checks.check(addressBuffer.get());
            countBuffer.rewind();
            throwIfError(MSDFGen.msdf_contour_get_edge_count(contour, countBuffer));
            final var edgeCount = countBuffer.get();
            for (long j = 0; j < edgeCount; j++) {
                addressBuffer.rewind();
                throwIfError(MSDFGen.msdf_contour_get_edge(contour, j, addressBuffer));
                final var segment = Checks.check(addressBuffer.get());
                countBuffer.rewind();
                throwIfError(MSDFGen.msdf_segment_get_point_count(segment, countBuffer));
                final var pointCount = countBuffer.get();
                for (long k = 0; k < pointCount; k++) {
                    pos.rewind();
                    throwIfError(MSDFGen.msdf_segment_get_point(segment, k, pos));
                    pos.x(pos.x() * scale);
                    pos.y(pos.y() * scale);
                    throwIfError(MSDFGen.msdf_segment_set_point(segment, k, pos));
                }
            }
        }

        stack.setPointer(previousSP);
    }

    /**
     * Throws an {@link MSDFGenException} if the given result is not
     * {@link MSDFGen#MSDF_SUCCESS} with a fitting message.
     *
     * @param result The result to validate.
     */
    public static void throwIfError(final int result) {
        if (result != MSDFGen.MSDF_SUCCESS) {
            switch (result) {
                case MSDFGen.MSDF_ERR_FAILED:
                    throw new MSDFGenException("Operation failed");
                case MSDFGen.MSDF_ERR_INVALID_ARG:
                    throw new MSDFGenException("Invalid argument");
                case MSDFGen.MSDF_ERR_INVALID_TYPE:
                    throw new MSDFGenException("Invalid type");
                case MSDFGen.MSDF_ERR_INVALID_SIZE:
                    throw new MSDFGenException("Invalid size");
                case MSDFGen.MSDF_ERR_INVALID_INDEX:
                    throw new MSDFGenException("Invalid index");
                default:
                    throw new MSDFGenException("Unknown error");
            }
        }
    }

    private static int getChannel(final long address, final int index) {
        final var raw = MemoryUtil.memGetFloat(address + ((long) Float.BYTES * index));
        return ~(int) (255.5F - 255F * Mth.clamp(raw, 0.0F, 1.0F)) & 0xFF;
    }

    private static int getBitmapPixel1(final long address, final int x, final int y, final int width) {
        final var pixelIndex = y * width + x;
        final var pixelAddress = address + ((long) Float.BYTES * pixelIndex);
        final var r = getChannel(pixelAddress, 0);
        return (0xFF << 24) | (r << 16) | (r << 8) | r;
    }

    private static int getBitmapPixel3(final long address, final int x, final int y, final int width) {
        final var pixelIndex = y * width + x;
        final var pixelAddress = address + ((long) Float.BYTES * 3 * pixelIndex);
        final var r = getChannel(pixelAddress, 0);
        final var g = getChannel(pixelAddress, 1);
        final var b = getChannel(pixelAddress, 2);
        return (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private static int getBitmapPixel4(final long address, final int x, final int y, final int width) {
        final var pixelIndex = y * width + x;
        final var pixelAddress = address + ((long) Float.BYTES * 4 * pixelIndex);
        final var r = getChannel(pixelAddress, 0);
        final var g = getChannel(pixelAddress, 1);
        final var b = getChannel(pixelAddress, 2);
        final var a = getChannel(pixelAddress, 3);
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Creates a new buffered image with the same size and channel count
     * as the given bitmap, and blits the bitmaps contents to the newly created image.
     *
     * @param src  The source bitmap to blit to an image.
     * @param dst  The destination image which the bitmap is blitted to.
     * @param dstX The x-coordinate at which to blit the given bitmap to the image.
     * @param dstY The y-coordinate at which to blit the given bitmap to the image.
     */
    public static void blitBitmapToImage(final MSDFGenBitmap.Buffer src, final BufferedImage dst, final int dstX,
                                         final int dstY) {
        final var stack = MemoryStack.stackGet();
        final var previousSP = stack.getPointer();
        final var addressBuffer = stack.mallocPointer(1);
        throwIfError(MSDFGen.msdf_bitmap_get_pixels(src, addressBuffer));
        final var address = Checks.check(addressBuffer.get());

        final var width = src.width();
        final var height = src.height();
        PixelGetter pixelGetter;
        switch (src.type()) {
            case MSDFGen.MSDF_BITMAP_TYPE_MSDF:
                pixelGetter = MSDFGenUtil::getBitmapPixel3;
                break;
            case MSDFGen.MSDF_BITMAP_TYPE_MTSDF:
                pixelGetter = MSDFGenUtil::getBitmapPixel4;
                break;
            default:
                pixelGetter = MSDFGenUtil::getBitmapPixel1;
                break;
        }

        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                dst.setRGB(dstX + x, dstY + (height - y - 1), pixelGetter.get(address, x, y, width));
            }
        }

        stack.setPointer(previousSP);
    }

    /**
     * See {@link #blitBitmapToImage(MSDFGenBitmap.Buffer, BufferedImage, int, int)}.
     */
    public static BufferedImage bitmapToImage(final MSDFGenBitmap.Buffer bitmap) {
        final var image = new BufferedImage(bitmap.width(), bitmap.height(), BufferedImage.TYPE_INT_ARGB);
        blitBitmapToImage(bitmap, image, 0, 0);
        return image;
    }

    /**
     * Renders the given shape to the given image, at the given pixel coordinates.
     *
     * @param type    The type of image to render into.
     * @param width   The width of the shape to render in pixels.
     * @param height  The height of the shape to render in pixels.
     * @param shape   The shape to render into the given image.
     * @param xScale  The x-scaling factor applied to the render transform.
     * @param yScale  The y-scaling factor applied to the render transform.
     * @param xOffset The x-offset applied to the render transform.
     * @param yOffset The y-offset applied to the render transform.
     * @param range   The distance mapping range.
     * @param dst     The destination image to render into.
     * @param dstX    The destination x-coordinate in the destination image in pixels.
     * @param dstY    The destination y-coordinate in the destination image in pixels.
     */
    public static void renderShapeToImage(final int type, final int width, final int height, final long shape,
                                          final double xScale, final double yScale, final double xOffset,
                                          final double yOffset, final double range, final BufferedImage dst,
                                          final int dstX, final int dstY) {
        final var stack = MemoryStack.stackGet();
        final var previousSP = stack.getPointer();
        final var bitmap = MSDFGenBitmap.malloc(1, stack);
        throwIfError(MSDFGen.msdf_bitmap_alloc(type, width, height, bitmap));
        final var transform = MSDFGenTransform.malloc(1, stack);
        transform.scale().set(xScale, yScale);
        transform.translation().set(xOffset, yOffset);
        transform.distance_mapping().set(-0.5 * range, 0.5 * range);
        switch (type) {
            case MSDFGen.MSDF_BITMAP_TYPE_SDF:
                throwIfError(MSDFGen.msdf_generate_sdf(bitmap, shape, transform));
                break;
            case MSDFGen.MSDF_BITMAP_TYPE_PSDF:
                throwIfError(MSDFGen.msdf_generate_psdf(bitmap, shape, transform));
                break;
            case MSDFGen.MSDF_BITMAP_TYPE_MSDF: {
                final var config = MSDFGenMultichannelConfig.malloc(1, stack);
                config.mode(MSDFGen.MSDF_ERROR_CORRECTION_MODE_INDISCRIMINATE);
                config.distance_check_mode(MSDFGen.MSDF_DISTANCE_CHECK_MODE_ALWAYS);
                throwIfError(MSDFGen.msdf_generate_msdf_with_config(bitmap, shape, transform, config));
                break;
            }
            case MSDFGen.MSDF_BITMAP_TYPE_MTSDF: {
                final var config = MSDFGenMultichannelConfig.malloc(1, stack);
                config.mode(MSDFGen.MSDF_ERROR_CORRECTION_MODE_INDISCRIMINATE);
                config.distance_check_mode(MSDFGen.MSDF_DISTANCE_CHECK_MODE_ALWAYS);
                throwIfError(MSDFGen.msdf_generate_mtsdf_with_config(bitmap, shape, transform, config));
                break;
            }
        }
        blitBitmapToImage(bitmap, dst, dstX, dstY);
        MSDFGen.msdf_bitmap_free(bitmap);
        stack.setPointer(previousSP);
    }

    /**
     * See {@link #renderShapeToImage(int, int, int, long, double, double, double, double, double, BufferedImage, int, int)}.
     */
    public static BufferedImage renderShapeToImage(final int type, final int width, final int height, final long shape,
                                                   final double xScale, final double yScale, final double xOffset,
                                                   final double yOffset, final double range) {
        final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        renderShapeToImage(type, width, height, shape, xScale, yScale, xOffset, yOffset, range, image, 0, 0);
        return image;
    }

    @FunctionalInterface
    private interface PixelGetter {
        int get(final long address, final int x, final int y, final int width);
    }
}