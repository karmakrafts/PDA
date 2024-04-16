/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Hinze
 * @since 15/04/2024
 */
public abstract class AbstractTest {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected static ExecutorService executorService;

    // @formatter:off
    protected AbstractTest() {}
    // @formatter:on

    @BeforeAll
    static void beforeAll() {
        executorService = Executors.newWorkStealingPool();
    }

    @AfterAll
    static void afterAll() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow().forEach(Runnable::run);
            }
        }
        catch (Throwable error) {
            LOGGER.error("Could not shutdown test executor service: {}", error.getMessage());
        }
    }
}
