/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.flex.FlexBorder;
import io.karma.pda.api.common.flex.FlexValue;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestFlexBorderCodecs extends AbstractEncodeDecodeTest<FlexBorder> {
    public TestFlexBorderCodecs() {
        super(FlexBorder.class, () -> FlexBorder.of(FlexValue.pixel(44)));
    }
}
