/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_MM_Var;
import org.lwjgl.util.freetype.FreeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 21/05/2024
 */
@OnlyIn(Dist.CLIENT)
public final class FreeTypeUtils {
    private static final float F16DOT16_SCALE = 65536F;
    private static final float F26DOT6_SCALE = 64F;

    // @formatter:off
    private FreeTypeUtils() {}
    // @formatter:on

    // F16DOT16 conversions

    public static float f16Dot16ToFP32(final long value) {
        return (1F / F16DOT16_SCALE) * (float) value;
    }

    public static float fp32ToF16Dot16(final float value) {
        return value * F16DOT16_SCALE;
    }

    // F26DOT6 conversions

    public static float f26Dot6ToFP32(final long value) {
        return (1F / F26DOT6_SCALE) * (float) value;
    }

    public static float fp32ToF26Dot6(final float value) {
        return value * F26DOT6_SCALE;
    }

    // Font variation axes - adapted from https://github.com/Chlumsky/msdfgen/blob/master/ext/import-font.cpp#L269-L311

    public record FontVariationAxis(String name, float min, float max, float def) {
    }

    public static List<FontVariationAxis> listFontVariationAxes(final long library, final FT_Face face) {
        try (final var stack = MemoryStack.stackPush()) {
            final var faceFlags = face.face_flags();
            if ((faceFlags & FreeType.FT_FACE_FLAG_MULTIPLE_MASTERS) == 0) {
                return Collections.emptyList();
            }
            final var masterAddressBuffer = stack.mallocPointer(1);
            if (FreeType.FT_Get_MM_Var(face, masterAddressBuffer) != FreeType.FT_Err_Ok) {
                return Collections.emptyList();
            }
            final var master = FT_MM_Var.create(Checks.check(masterAddressBuffer.get()));
            final var numAxes = master.num_axis();
            final var axes = new ArrayList<FontVariationAxis>(numAxes);
            for (var i = 0; i < numAxes; i++) {
                final var axis = master.axis().get(i);
                axes.add(new FontVariationAxis(axis.nameString(),
                    f16Dot16ToFP32(axis.minimum()),
                    f16Dot16ToFP32(axis.maximum()),
                    f16Dot16ToFP32(axis.def())));
            }
            FreeType.FT_Done_MM_Var(library, master);
            return axes;
        }
    }

    public static boolean setFontVariationAxis(final long library, final FT_Face face, final String name,
                                               final float coord) {
        try (final var stack = MemoryStack.stackPush()) {
            final var faceFlags = face.face_flags();
            if ((faceFlags & FreeType.FT_FACE_FLAG_MULTIPLE_MASTERS) == 0) {
                return false;
            }
            final var masterAddressBuffer = stack.mallocPointer(1);
            if (FreeType.FT_Get_MM_Var(face, masterAddressBuffer) != FreeType.FT_Err_Ok) {
                return false;
            }
            final var master = FT_MM_Var.create(Checks.check(masterAddressBuffer.get()));
            final var numAxes = master.num_axis();
            final var coordsBuffer = stack.callocCLong(numAxes); // Make sure this is zeroed
            var result = false;
            if (FreeType.FT_Get_Var_Design_Coordinates(face, coordsBuffer) != FreeType.FT_Err_Ok) {
                for (var i = 0; i < numAxes; i++) {
                    final var axis = master.axis().get(i);
                    if (!axis.nameString().equals(name)) {
                        continue;
                    }
                    MemoryUtil.memPutDouble(coordsBuffer.address(i), fp32ToF16Dot16(coord));
                    result = true;
                    break;
                }
            }
            if (FreeType.FT_Set_Var_Design_Coordinates(face, coordsBuffer) != FreeType.FT_Err_Ok) {
                result = false;
            }
            FreeType.FT_Done_MM_Var(library, master);
            return result;
        }
    }
}
