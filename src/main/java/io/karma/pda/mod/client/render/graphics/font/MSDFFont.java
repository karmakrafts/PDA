/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.graphics.font;

import io.karma.pda.mod.PDAMod;
import io.karma.pda.mod.client.util.FontVariationAxis;
import io.karma.pda.mod.client.util.FreeTypeUtils;
import io.karma.pda.mod.client.util.MSDFUtils;
import io.karma.pda.mod.util.MemoryUtils;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.msdfgen.MSDFGen;
import org.lwjgl.util.msdfgen.MSDFGenExt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Simple font implementation which allows loading TrueType fonts
 * using FreeType and converting glyphs into vector shapes ready
 * for processing with msdfgen-core. Also employs some libffi based
 * function calling to avoid crashes due to missing call intrinsics in LWJGL core 3.3.1.
 * <p>
 * Based on <a href="https://github.com/Chlumsky/msdfgen/blob/master/ext/import-font.cpp" target="_blank">this code</a> from msdfgen.
 *
 * @author Alexander Hinze
 * @since 12/05/2024
 */
public final class MSDFFont implements AutoCloseable {
    private static final FFICIF NEW_MEMORY_FACE_CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI,
        LibFFI.ffi_type_sint,
        LibFFI.ffi_type_pointer,
        LibFFI.ffi_type_pointer,
        LibFFI.ffi_type_slong,
        LibFFI.ffi_type_slong,
        LibFFI.ffi_type_pointer);

    static {
        MSDFUtils.throwIfError(MSDFGenExt.msdf_ft_set_load_callback(nameAddress -> FreeType.getLibrary().getFunctionAddress(
            MemoryUtil.memASCII(nameAddress))));
    }

    private final InputStream stream;
    private final long library; // FT_Library
    private final FT_Face face;
    private final long font; // msdf_ft_font_handle
    private final List<FontVariationAxis> variationAxes;

    public MSDFFont(final InputStream stream) throws IOException {
        this.stream = stream;

        try (final var stack = MemoryStack.stackPush()) {
            final var ftAddressBuffer = stack.mallocPointer(1);

            if (FreeType.FT_Init_FreeType(ftAddressBuffer) != FreeType.FT_Err_Ok) {
                throw new IllegalStateException("Could not create FreeType library");
            }
            library = Checks.check(ftAddressBuffer.get());
            PDAMod.LOGGER.debug("Created FreeType instance at 0x{}", Long.toHexString(library));

            // Create the font face and load it
            final var data = stream.readAllBytes();
            final var dataSize = data.length;
            final var dataAddress = MemoryUtil.nmemAlloc(dataSize);
            MemoryUtils.wrap(dataAddress, dataSize).put(data);
            PDAMod.LOGGER.debug("Created font memory at 0x{}", Long.toHexString(dataAddress));

            final var resultBuffer = stack.mallocInt(1);
            final var faceAddressBuffer = stack.mallocPointer(1);
            // @formatter:off
            // ()
            LibFFI.ffi_call(NEW_MEMORY_FACE_CIF, FreeType.Functions.New_Memory_Face,
                MemoryUtil.memByteBuffer(resultBuffer),
                stack.pointers(
                    stack.pointers(library).address(),
                    stack.pointers(dataAddress).address(),
                    MemoryUtil.memAddress(stack.longs(dataSize)),
                    MemoryUtil.memAddress(stack.longs(0)),
                    stack.pointers(faceAddressBuffer.address()).address()
                ));
            // @formatter:on
            MemoryUtil.nmemFree(dataAddress); // Free heap memory for file contents
            if (resultBuffer.get() != FreeType.FT_Err_Ok) {
                throw new IllegalStateException("Could not create FreeType face");
            }
            face = FT_Face.create(Checks.check(faceAddressBuffer.get()));
            PDAMod.LOGGER.debug("Created font face instance at 0x{}", Long.toHexString(face.address()));

            // Retrieve variation axes
            variationAxes = FreeTypeUtils.listFontVariationAxes(library, face);
            for (final var axis : variationAxes) {
                PDAMod.LOGGER.debug("Found variation axis '{}' ({}, between {} and {})",
                    axis.name(),
                    String.format("%.04f", axis.def()),
                    String.format("%.04f", axis.min()),
                    String.format("%.04f", axis.max()));
            }

            // Setup msdfgen to use LWJGL FreeType bindings
            final var fontAddressBuffer = stack.mallocPointer(1);
            MSDFUtils.throwIfError(MSDFGenExt.msdf_ft_adopt_font(face.address(), fontAddressBuffer));
            font = Checks.check(fontAddressBuffer.get());
        }
    }

    public boolean isGlyphEmpty(final int c) {
        return getGlyph(c) == null;
    }

    public long createGlyphShape(final int c) {
        try (final var stack = MemoryStack.stackPush()) {
            final var addressBuffer = stack.mallocPointer(1);
            // Convert raw Java character to unicode codepoint to properly support surrogate pairs
            MSDFUtils.throwIfError(MSDFGenExt.msdf_ft_font_load_glyph(font,
                c,
                MSDFGenExt.MSDF_FONT_SCALING_NONE,
                addressBuffer));
            final var shape = Checks.check(addressBuffer.get());
            MSDFUtils.throwIfError(MSDFGen.msdf_shape_normalize(shape));
            MSDFUtils.rewindShapeIfNeeded(shape);
            return shape;
        }
    }

    public @Nullable FT_GlyphSlot getGlyph(final int c) {
        final var charIndex = FreeType.FT_Get_Char_Index(face, c);
        if (charIndex == 0) {
            return null; // The glyph is unsupported
        }
        if (FreeType.FT_Load_Glyph(face, charIndex, FreeType.FT_LOAD_NO_SCALE) != FreeType.FT_Err_Ok) {
            throw new IllegalStateException("Could not load glyph");
        }
        return Objects.requireNonNull(face.glyph());
    }

    public @Nullable DefaultGlyphMetrics createGlyphMetrics(final int c, final float scale) {
        final var glyph = getGlyph(c);
        if (glyph == null) {
            return null;
        }

        final var ascent = FreeTypeUtils.f26Dot6ToFP32(face.ascender()) * scale;
        final var descent = FreeTypeUtils.f26Dot6ToFP32(face.descender()) * scale;
        final var advance = glyph.advance(); // Warning can be ignored
        final var advanceX = FreeTypeUtils.f26Dot6ToFP32(advance.x()) * scale;
        final var advanceY = FreeTypeUtils.f26Dot6ToFP32(advance.y()) * scale;

        final var metrics = glyph.metrics();
        final var width = FreeTypeUtils.f26Dot6ToFP32(metrics.width()) * scale;
        final var height = FreeTypeUtils.f26Dot6ToFP32(metrics.height()) * scale;
        final var bearingX = FreeTypeUtils.f26Dot6ToFP32(metrics.horiBearingX()) * scale;
        final var bearingY = FreeTypeUtils.f26Dot6ToFP32(metrics.horiBearingY()) * scale;

        return new DefaultGlyphMetrics(width, height, ascent, descent, advanceX, advanceY, bearingX, bearingY);
    }

    public List<FontVariationAxis> getVariationAxes() {
        return variationAxes;
    }

    public void setVariationAxis(final FontVariationAxis axis, final float coord) {
        if (!FreeTypeUtils.setFontVariationAxis(library, face, axis.name(), Mth.clamp(coord, axis.min(), axis.max()))) {
            PDAMod.LOGGER.warn("Could not set font variation axis {}, ignoring", axis.name());
        }
    }

    public FT_Face getFace() {
        return face;
    }

    @Override
    public void close() throws Exception {
        stream.close();
        MSDFGenExt.msdf_ft_font_destroy(font);
        PDAMod.LOGGER.debug("Freed font instance at 0x{}", Long.toHexString(font));
        FreeType.FT_Done_Face(face);
        PDAMod.LOGGER.debug("Freed font face instance at 0x{}", Long.toHexString(face.address()));
        FreeType.FT_Done_FreeType(library);
        PDAMod.LOGGER.debug("Freed FreeType instance at 0x{}", Long.toHexString(library));
    }
}
