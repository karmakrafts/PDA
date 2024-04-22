/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.app.component.Container;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 23/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestContainerCodecs extends AbstractEncodeDecodeTest<Container> {
    public TestContainerCodecs() {
        super(Container.class, () -> {
            final var container = TestHarness.CONTAINER.create();
            container.addChild(TestHarness.COMPONENT.create());
            return container;
        });
    }
}
