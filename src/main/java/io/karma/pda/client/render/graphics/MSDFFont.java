/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.graphics;

import io.karma.pda.common.PDAMod;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
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
import org.lwjgl.util.msdfgen.MSDFGenBounds;
import org.lwjgl.util.msdfgen.MSDFGenVector2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final InputStream stream;
    private final long memory; // Raw memory
    private final long library; // FT_Library
    private final FT_Face face;
    private final long font; // msdf_ft_font_handle

    public MSDFFont(final InputStream stream) throws IOException {
        this.stream = stream;

        final var stack = MemoryStack.stackGet();
        final var previousSP = stack.getPointer();
        final var ftAddressBuffer = stack.mallocPointer(1);

        if (FreeType.FT_Init_FreeType(ftAddressBuffer) != FreeType.FT_Err_Ok) {
            throw new IllegalStateException("Could not create FreeType library");
        }
        library = Checks.check(ftAddressBuffer.get());
        PDAMod.LOGGER.debug("Created FreeType instance at 0x{}", Long.toHexString(library));

        // Create the font face and load it
        final var data = stream.readAllBytes();
        final var dataSize = data.length;
        final var srcBuffer = ByteBuffer.allocateDirect(dataSize).order(ByteOrder.nativeOrder());
        srcBuffer.put(data);
        srcBuffer.flip();
        memory = MemoryUtil.nmemAllocChecked(dataSize);
        MemoryUtil.memCopy(MemoryUtil.memAddress(srcBuffer), memory, dataSize);
        PDAMod.LOGGER.debug("Created font memory at 0x{}", Long.toHexString(memory));

        {
            final var resultBuffer = stack.mallocInt(1);
            final var faceAddressBuffer = stack.mallocPointer(1);
            // @formatter:off
            LibFFI.ffi_call(NEW_MEMORY_FACE_CIF, FreeType.Functions.New_Memory_Face,
                MemoryUtil.memByteBuffer(resultBuffer),
                stack.pointers(
                    stack.pointers(library).address(),
                    stack.pointers(memory).address(),
                    MemoryUtil.memAddress(stack.longs(dataSize)),
                    MemoryUtil.memAddress(stack.longs(0)),
                    stack.pointers(faceAddressBuffer.address()).address()
                ));
            // @formatter:on
            if (resultBuffer.get() != FreeType.FT_Err_Ok) {
                throw new IllegalStateException("Could not create FreeType face");
            }
            face = FT_Face.create(Checks.check(faceAddressBuffer.get()));
            PDAMod.LOGGER.debug("Created font face instance at 0x{}", Long.toHexString(face.address()));
        }

        {
            // Setup msdfgen to use LWJGL FreeType bindings
            MSDFGenUtil.throwIfError(MSDFGen.msdf_ft_set_load_callback(nameAddress -> FreeType.getLibrary().getFunctionAddress(
                MemoryUtil.memASCII(nameAddress))));
            final var fontAddressBuffer = stack.mallocPointer(1);
            MSDFGenUtil.throwIfError(MSDFGen.nmsdf_ft_adopt_font(face.address(), fontAddressBuffer.address()));
            font = Checks.check(fontAddressBuffer.get());
        }

        stack.setPointer(previousSP);
    }

    public MSDFFont(final Path filePath) throws IOException {
        this(Files.newInputStream(filePath));
    }

    public boolean isGlyphEmpty(final int c) {
        return getGlyph(c) == null;
    }

    public long createGlyphShape(final int c) {
        final var stack = MemoryStack.stackGet();
        final var previousSP = stack.getPointer();

        final var addressBuffer = stack.mallocPointer(1);
        // Convert raw Java character to unicode codepoint to properly support surrogate pairs
        MSDFGenUtil.throwIfError(MSDFGen.msdf_ft_font_load_glyph(font, c, addressBuffer));
        final var shape = Checks.check(addressBuffer.get());

        final var resultBuffer = stack.mallocInt(1);
        MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_validate(shape, resultBuffer));
        if (resultBuffer.get() != MSDFGen.MSDF_TRUE) {
            throw new MSDFGenException("Could not validate shape");
        }
        MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_normalize(shape));

        // Correct incorrectly wound contours (taken from https://github.com/Chlumsky/msdf-atlas-gen/blob/master/msdf-atlas-gen/GlyphGeometry.cpp)
        {
            final var bounds = MSDFGenBounds.malloc(1, stack);
            MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_get_bounds(shape, bounds));
            final var outerPoint = new Vector2d(bounds.l() - (bounds.r() - bounds.l()) - 1,
                bounds.b() - (bounds.t() - bounds.b()) - 1);
            final var distanceBuffer = stack.mallocDouble(1);
            MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_one_shot_distance(shape,
                MSDFGenVector2.malloc(1, stack).x(outerPoint.x).y(outerPoint.y),
                distanceBuffer));
            if (distanceBuffer.get() > 0.0) {
                PDAMod.LOGGER.debug("Shape wound incorrectly, correcting winding order");
                final var contourCountBuffer = stack.mallocPointer(1);
                MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_get_contour_count(shape, contourCountBuffer));
                final var contourCount = contourCountBuffer.get();
                final var contourAddressBuffer = stack.mallocPointer(1);
                for (long i = 0; i < contourCount; i++) {
                    contourAddressBuffer.rewind();
                    MSDFGenUtil.throwIfError(MSDFGen.msdf_shape_get_contour(shape, i, contourAddressBuffer));
                    MSDFGenUtil.throwIfError(MSDFGen.msdf_contour_reverse(Checks.check(contourAddressBuffer.get())));
                }
            }
        }

        stack.setPointer(previousSP);
        return shape;
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

    public @Nullable DefaultGlyphMetrics getGlyphMetrics(final int c) {
        final var glyph = getGlyph(c);
        if (glyph == null) {
            return null;
        }
        final var ascent = face.ascender() >> 6;
        final var descent = face.descender() >> 6;
        final var advance = (int) glyph.advance().x() >> 6;
        final var metrics = glyph.metrics();
        final var width = (int) metrics.width() >> 6;
        final var height = (int) metrics.height() >> 6;
        final var bearingX = (int) metrics.horiBearingX() >> 6;
        final var bearingY = (int) metrics.horiBearingY() >> 6;
        return new DefaultGlyphMetrics(width, height, ascent, descent, advance, bearingX, bearingY);
    }

    public long getLibrary() {
        return library;
    }

    public FT_Face getFace() {
        return face;
    }

    @Override
    public void close() throws Exception {
        stream.close();
        MSDFGen.msdf_ft_font_destroy(font);
        PDAMod.LOGGER.debug("Freed font instance at 0x{}", Long.toHexString(font));
        FreeType.FT_Done_Face(face);
        PDAMod.LOGGER.debug("Freed font face instance at 0x{}", Long.toHexString(face.address()));
        FreeType.FT_Done_FreeType(library);
        PDAMod.LOGGER.debug("Freed FreeType instance at 0x{}", Long.toHexString(library));
        MemoryUtil.nmemFree(memory);
        PDAMod.LOGGER.debug("Freed font memory at 0x{}", Long.toHexString(memory));
    }
}
