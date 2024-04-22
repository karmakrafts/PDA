/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.app.component.Component;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestComponentCodecs extends AbstractEncodeDecodeTest<Component> {
    public TestComponentCodecs() {
        super(Component.class, TestHarness.COMPONENT::create);
    }
}
