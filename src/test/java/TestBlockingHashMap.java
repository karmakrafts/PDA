/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.common.util.BlockingHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 15/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestBlockingHashMap {
    @Test
    void testPut() {
        final var map = new BlockingHashMap<String, String>();
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        map.put("foo", "bar");
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("bar", map.get("foo"));
    }

    @Test
    void testRemove() {
        final var map = new BlockingHashMap<String, String>();
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        map.put("foo", "bar");
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(1, map.size());

        Assertions.assertEquals("bar", map.remove("foo"));
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void testRemoveLater() {
        TestHarness.init();

        final var map = new BlockingHashMap<String, String>();
        map.removeLater("foo", TestHarness.executor).thenAccept(value -> Assertions.assertEquals("bar", value));

        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        map.put("foo", "bar");

        TestHarness.dispose();
    }
}
