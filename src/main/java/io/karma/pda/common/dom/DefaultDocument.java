package io.karma.pda.common.dom;

/**
 * @author Alexander Hinze
 * @since 08/02/2024
 */
public final class DefaultDocument extends ContainerNode implements Document {
    @Override
    public NodeType getType() {
        return NodeType.DOCUMENT;
    }
}
