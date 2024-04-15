/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexander Hinze
 * @since 15/04/2024
 */
public abstract class AbstractTest {
    protected final ExecutorService executor = Executors.newWorkStealingPool();

    // @formatter:off
    protected AbstractTest() {}
    // @formatter:on
}
