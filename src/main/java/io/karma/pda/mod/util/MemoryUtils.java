/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import io.karma.pda.mod.PDAMod;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.LongPredicate;

import static org.lwjgl.system.Pointer.BITS32;
import static org.lwjgl.system.jni.JNINativeInterface.NewDirectByteBuffer;

/**
 * Some duplicated code taken from LWJGLs internals for dealing with NIO buffers.
 *
 * @author Alexander Hinze
 * @since 12/08/2024
 */
public final class MemoryUtils {
    private static final int MAGIC_CAPACITY = 0x0D15EA5E;
    private static final int MAGIC_POSITION = 0x00FACADE;
    private static final long ADDRESS;
    private static final long MARK;
    private static final long LIMIT;
    private static final long CAPACITY;
    private static final Class<? extends ByteBuffer> BYTE_BUFFER_TYPE;
    private static Unsafe unsafe;

    static {
        try {
            final var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            field.setAccessible(false);
        }
        catch (Throwable error) {
            PDAMod.LOGGER.error("Could not retrieve sun.misc.Unsafe instance", error);
        }
        ADDRESS = getAddressOffset();
        MARK = getMarkOffset();
        LIMIT = getLimitOffset();
        CAPACITY = getCapacityOffset();
        BYTE_BUFFER_TYPE = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder()).getClass();
    }

    // @formatter:off
    private MemoryUtils() {}

    private static long getFieldOffset(Class<?> containerType, Class<?> fieldType, LongPredicate predicate) {
        Class<?> c = containerType;
        while (c != Object.class) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (!field.getType().isAssignableFrom(fieldType) || Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                long offset = unsafe.objectFieldOffset(field);
                if (predicate.test(offset)) {
                    return offset;
                }
            }
            c = c.getSuperclass();
        }
        throw new UnsupportedOperationException("Failed to find field offset in class.");
    }

    private static long getAddressOffset() {
        long MAGIC_ADDRESS = 0xDEADBEEF8BADF00DL & (BITS32 ? 0xFFFF_FFFFL : 0xFFFF_FFFF_FFFF_FFFFL);
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(MAGIC_ADDRESS, 0));
        return getFieldOffset(bb.getClass(), long.class, offset -> unsafe.getLong(bb, offset) == MAGIC_ADDRESS);
    }

    private static long getFieldOffsetInt(Object container, int value) {
        return getFieldOffset(container.getClass(), int.class, offset -> unsafe.getInt(container, offset) == value);
    }

    private static long getMarkOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(1L, 0));
        return getFieldOffsetInt(bb, -1);
    }

    private static long getLimitOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(MAGIC_POSITION);
        return getFieldOffsetInt(bb, MAGIC_POSITION);
    }

    private static long getCapacityOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(0);
        return getFieldOffsetInt(bb, MAGIC_CAPACITY);
    }

    public static ByteBuffer wrap(final long address, final int capacity) {
        ByteBuffer buffer;
        try {
            buffer = (ByteBuffer)unsafe.allocateInstance(BYTE_BUFFER_TYPE);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
        unsafe.putLong(buffer, ADDRESS, address);
        unsafe.putInt(buffer, MARK, -1);
        unsafe.putInt(buffer, LIMIT, capacity);
        unsafe.putInt(buffer, CAPACITY, capacity);
        return buffer;
    }
}
