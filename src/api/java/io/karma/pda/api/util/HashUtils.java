/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import io.karma.pda.api.API;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 24/08/2024
 */
public final class HashUtils {
    private static final ThreadLocal<MessageDigest> FINGERPRINT_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        }
        catch (Throwable error) {
            API.getLogger().error("Could not retrieve fingerprint message digest", error);
            return null;
        }
    });

    // @formatter:off
    private HashUtils() {}
    // @formatter:on

    public static int combine(final int hash1, final int hash2) {
        return 31 * hash1 + hash2;
    }

    public static int combine(final int hash1, final int hash2, final int hash3, final int... hashes) {
        var hash = hashes[0];
        for (var i = 0; i < hashes.length - 1; i++) {
            hash = combine(hash, hashes[i + 1]);
        }
        return combine(hash1, combine(hash2, combine(hash3, hash)));
    }

    public static int hash(final Collection<?> objects) {
        var hash = 0;
        for (final var object : objects) {
            hash = combine(object.hashCode(), hash);
        }
        return hash;
    }

    public static int hash(final Map<?, ?> map) {
        return combine(hash(map.keySet()), hash(map.values()));
    }

    public static int hashValuesAsStrings(final Map<?, ?> map) {
        return combine(hash(map.keySet()), hash(map.values().stream().map(Object::toString).toList()));
    }

    public static String toFingerprint(final int hash) {
        final var data = new byte[4];
        data[0] = (byte) ((hash >> 24) & 0xFF);
        data[1] = (byte) ((hash >> 16) & 0xFF);
        data[2] = (byte) ((hash >> 8) & 0xFF);
        data[3] = (byte) (hash & 0xFF);
        final var digest = FINGERPRINT_DIGEST.get();
        digest.update(data);
        final var fingerprintData = digest.digest();
        final var builder = new StringBuilder();
        for (final var b : fingerprintData) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    public static String toFingerprint(final long hash) {
        final var data = new byte[8];
        data[0] = (byte) ((hash >> 56) & 0xFF);
        data[1] = (byte) ((hash >> 48) & 0xFF);
        data[2] = (byte) ((hash >> 40) & 0xFF);
        data[3] = (byte) ((hash >> 32) & 0xFF);
        data[4] = (byte) ((hash >> 24) & 0xFF);
        data[5] = (byte) ((hash >> 16) & 0xFF);
        data[6] = (byte) ((hash >> 8) & 0xFF);
        data[7] = (byte) (hash & 0xFF);
        final var digest = FINGERPRINT_DIGEST.get();
        digest.update(data);
        final var fingerprintData = digest.digest();
        final var builder = new StringBuilder();
        for (final var b : fingerprintData) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    public static String toFingerprint(final int hash1, final int hash2) {
        return toFingerprint(((long) hash1 << 32) | hash2);
    }
}
