/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.flex.DefaultFlexNode;
import io.karma.pda.api.common.flex.FlexNode;
import org.junit.jupiter.api.TestInstance;

/**
 * @author Alexander Hinze
 * @since 22/04/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TestFlexNodeCodecs extends AbstractEncodeDecodeTest<FlexNode> {
    public TestFlexNodeCodecs() {
        super(FlexNode.class, DefaultFlexNode::defaults);
    }
}
