/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
public abstract class AbstractEncodeDecodeTest<T> {
    private final Class<T> type;
    private final Supplier<T> supplier;

    protected AbstractEncodeDecodeTest(final Class<T> type, final Supplier<T> supplier) {
        this.type = type;
        this.supplier = supplier;
    }

    @Test
    void testTranscode() {
        TestHarness.init();

        final var value = supplier.get();
        final var string = API.getObjectMapper().valueToTree(value).toPrettyString();
        TestHarness.logger.info("Encoded {}:\n{}\n", type, string);

        try {
            final var reconstructed = API.getObjectMapper().readValue(string, type);
            TestHarness.logger.info("Decoded {}: {}", type, reconstructed);

            Assertions.assertNotNull(reconstructed);
            Assertions.assertEquals(value, reconstructed);
        }
        catch (Throwable error) {
            Assertions.fail(error);
        }

        TestHarness.dispose();
    }
}
