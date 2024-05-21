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
    private static final double F16DOT16_SCALE = 65536.0;
    private static final double F26DOT6_SCALE = 64.0;

    // @formatter:off
    private FreeTypeUtils() {}
    // @formatter:on

    // F16DOT16 conversions

    public static double f16Dot16ToDouble(final double value) {
        return (1.0 / F16DOT16_SCALE) * value;
    }

    public static double f16Dot16ToDouble(final long value) {
        return (1.0 / F16DOT16_SCALE) * (double) value;
    }

    public static double doubleToF16Dot16(final double value) {
        return value * F16DOT16_SCALE;
    }

    public static double longToF16Dot16(final long value) {
        return (double) value * F16DOT16_SCALE;
    }

    // F26DOT6 conversions

    public static double f26Dot6ToDouble(final long value) {
        return (1.0 / F26DOT6_SCALE) * (double) value;
    }

    public static long f26Dot6ToLong(final long value) {
        return value >> 6;
    }

    public static long doubleToF26Dot6(final double value) {
        return (long) (value * F26DOT6_SCALE);
    }

    public static long longToF26Dot6(final long value) {
        return value << 6;
    }

    // Font variation axes - adapted from https://github.com/Chlumsky/msdfgen/blob/master/ext/import-font.cpp#L269-L311

    public record FontVariationAxis(String name, double min, double max, double def) {
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
                    f16Dot16ToDouble(axis.minimum()),
                    f16Dot16ToDouble(axis.maximum()),
                    f16Dot16ToDouble(axis.def())));
            }
            FreeType.FT_Done_MM_Var(library, master);
            return axes;
        }
    }

    public static boolean setFontVariationAxis(final long library, final FT_Face face, final String name,
                                               final double coord) {
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
                    MemoryUtil.memPutDouble(coordsBuffer.address(i), doubleToF16Dot16(coord));
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
