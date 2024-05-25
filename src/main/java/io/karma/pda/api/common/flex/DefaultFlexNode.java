/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

import io.karma.pda.api.common.dispose.Disposable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class DefaultFlexNode implements FlexNode {
    private final ArrayList<FlexNode> children = new ArrayList<>();
    private FlexDirection direction;
    private FlexOverflow overflow;
    private FlexPositionType positionType;
    private FlexAlignment selfAlignment;
    private FlexAlignment itemAlignment;
    private FlexAlignment contentAlignment;
    private FlexJustify contentJustification;
    private FlexWrap wrap;
    private FlexValue x;
    private FlexValue y;
    private FlexValue width;
    private FlexValue height;
    private FlexBorder border;
    private FlexBorder margin;
    private FlexBorder padding;
    private FlexNode parent;
    private float growWeight;
    private float shrinkWeight;
    private FlexValue basis;
    private FlexNodeType type;

    private DefaultFlexNode(final FlexDirection direction, final FlexOverflow overflow,
                            final FlexPositionType positionType, final FlexAlignment selfAlignment,
                            final FlexAlignment itemAlignment, final FlexAlignment contentAlignment,
                            final FlexJustify contentJustification, final FlexWrap wrap, final FlexValue x,
                            final FlexValue y, final FlexValue width, final FlexValue height, final FlexBorder border,
                            final FlexBorder margin, final FlexBorder padding, final float growWeight,
                            final float shrinkWeight, final FlexValue basis, final FlexNodeType type) {
        this.direction = direction;
        this.overflow = overflow;
        this.positionType = positionType;
        this.selfAlignment = selfAlignment;
        this.itemAlignment = itemAlignment;
        this.contentAlignment = contentAlignment;
        this.contentJustification = contentJustification;
        this.wrap = wrap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.border = border;
        this.margin = margin;
        this.padding = padding;
        this.growWeight = growWeight;
        this.shrinkWeight = shrinkWeight;
        this.basis = basis;
        this.type = type;
    }

    public static DefaultFlexNode defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public FlexNodeType getType() {
        return type;
    }

    @Override
    public void setType(final FlexNodeType type) {
        this.type = type;
    }

    @Override
    public float getGrowWeight() {
        return growWeight;
    }

    @Override
    public void setGrowWeight(final float growWeight) {
        this.growWeight = growWeight;
    }

    @Override
    public float getShrinkWeight() {
        return shrinkWeight;
    }

    @Override
    public void setShrinkWeight(final float shrinkWeight) {
        this.shrinkWeight = shrinkWeight;
    }

    @Override
    public FlexValue getBasis() {
        return basis;
    }

    @Override
    public void setBasis(final FlexValue basis) {
        this.basis = basis;
    }

    @Override
    public @Nullable FlexNode getParent() {
        return parent;
    }

    @Override
    public void setFrom(final FlexNode flexNode) {
        direction = flexNode.getDirection();
        overflow = flexNode.getOverflow();
        positionType = flexNode.getPositionType();
        selfAlignment = flexNode.getSelfAlignment();
        itemAlignment = flexNode.getItemAlignment();
        contentAlignment = flexNode.getContentAlignment();
        contentJustification = flexNode.getContentJustification();
        wrap = flexNode.getWrap();
        x = flexNode.getX();
        y = flexNode.getY();
        width = flexNode.getWidth();
        height = flexNode.getHeight();
        border = flexNode.getBorder();
        margin = flexNode.getMargin();
        padding = flexNode.getPadding();
        shrinkWeight = flexNode.getShrinkWeight();
        growWeight = flexNode.getGrowWeight();
        basis = flexNode.getBasis();
        type = flexNode.getType();
    }

    @Override
    public void clearChildren() {
        for (final var child : children) {
            if (!(child instanceof Disposable disposable)) {
                continue;
            }
            disposable.dispose();
        }
        children.clear();
    }

    @Override
    public List<FlexNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addChild(final FlexNode child) {
        if (children.contains(child)) {
            return;
        }
        if (child instanceof DefaultFlexNode defaultNode) {
            defaultNode.parent = this;
        }
        children.add(child);
    }

    @Override
    public void removeChild(final FlexNode child) {
        if (children.remove(child)) {
            if (child instanceof DefaultFlexNode defaultNode) {
                defaultNode.parent = null;
            }
        }
    }

    @Override
    public int indexOfChild(final FlexNode child) {
        return children.indexOf(child);
    }

    @Override
    public @Nullable FlexNode getChild(final int index) {
        if (children.isEmpty() || index < 0 || index >= children.size()) {
            return null;
        }
        return children.get(index);
    }

    @Override
    public FlexWrap getWrap() {
        return wrap;
    }

    @Override
    public void setWrap(final FlexWrap wrap) {
        this.wrap = wrap;
    }

    @Override
    public FlexBorder getBorder() {
        return border;
    }

    @Override
    public void setBorder(final FlexBorder border) {
        this.border = border;
    }

    @Override
    public FlexDirection getDirection() {
        return direction;
    }

    @Override
    public void setDirection(final FlexDirection direction) {
        this.direction = direction;
    }

    @Override
    public FlexPositionType getPositionType() {
        return positionType;
    }

    @Override
    public void setPositionType(final FlexPositionType positionType) {
        this.positionType = positionType;
    }

    @Override
    public FlexOverflow getOverflow() {
        return overflow;
    }

    @Override
    public void setOverflow(final FlexOverflow overflow) {
        this.overflow = overflow;
    }

    @Override
    public FlexAlignment getSelfAlignment() {
        return selfAlignment;
    }

    @Override
    public void setSelfAlignment(final FlexAlignment selfAlignment) {
        this.selfAlignment = selfAlignment;
    }

    @Override
    public FlexAlignment getItemAlignment() {
        return itemAlignment;
    }

    @Override
    public void setItemAlignment(final FlexAlignment itemAlignment) {
        this.itemAlignment = itemAlignment;
    }

    @Override
    public FlexAlignment getContentAlignment() {
        return contentAlignment;
    }

    @Override
    public void setContentAlignment(final FlexAlignment contentAlignment) {
        this.contentAlignment = contentAlignment;
    }

    @Override
    public FlexJustify getContentJustification() {
        return contentJustification;
    }

    @Override
    public void setContentJustification(final FlexJustify contentJustification) {
        this.contentJustification = contentJustification;
    }

    @Override
    public FlexValue getX() {
        return x;
    }

    @Override
    public void setX(final FlexValue x) {
        this.x = x;
    }

    @Override
    public FlexValue getY() {
        return y;
    }

    @Override
    public void setY(final FlexValue y) {
        this.y = y;
    }

    @Override
    public FlexValue getWidth() {
        return width;
    }

    @Override
    public void setWidth(final FlexValue width) {
        this.width = width;
    }

    @Override
    public FlexValue getHeight() {
        return height;
    }

    @Override
    public void setHeight(final FlexValue height) {
        this.height = height;
    }

    @Override
    public FlexBorder getMargin() {
        return margin;
    }

    @Override
    public void setMargin(final FlexBorder margin) {
        this.margin = margin;
    }

    @Override
    public FlexBorder getPadding() {
        return padding;
    }

    @Override
    public void setPadding(final FlexBorder padding) {
        this.padding = padding;
    }

    @Override
    public int getAbsoluteWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAbsoluteHeight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAbsoluteX() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAbsoluteY() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void computeLayout() {
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FlexNode node)) {
            return false;
        }
        // @formatter:off
        return direction == node.getDirection()
            && overflow == node.getOverflow()
            && positionType == node.getPositionType()
            && selfAlignment == node.getSelfAlignment()
            && itemAlignment == node.getItemAlignment()
            && contentAlignment == node.getContentAlignment()
            && contentJustification == node.getContentJustification()
            && wrap == node.getWrap()
            && x.equals(node.getX())
            && y.equals(node.getY())
            && width.equals(node.getWidth())
            && height.equals(node.getHeight())
            && border.equals(node.getBorder())
            && margin.equals(node.getMargin())
            && padding.equals(node.getPadding())
            && growWeight == node.getGrowWeight()
            && shrinkWeight == node.getShrinkWeight()
            && basis.equals(node.getBasis())
            && type.equals(node.getType());
        // @formatter:on
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction,
            overflow,
            positionType,
            selfAlignment,
            itemAlignment,
            contentAlignment,
            contentJustification,
            wrap,
            x,
            y,
            width,
            height,
            border,
            margin,
            padding,
            growWeight,
            shrinkWeight,
            basis,
            type);
    }

    public static class Builder {
        private FlexDirection direction = FlexDirection.COLUMN;
        private FlexOverflow overflow = FlexOverflow.VISIBLE;
        private FlexPositionType positionType = FlexPositionType.RELATIVE;
        private FlexAlignment selfAlignment = FlexAlignment.AUTO;
        private FlexAlignment itemAlignment = FlexAlignment.STRETCH;
        private FlexAlignment contentAlignment = FlexAlignment.FLEX_START;
        private FlexJustify contentJustification = FlexJustify.FLEX_START;
        private FlexWrap wrap = FlexWrap.NONE;
        private FlexValue x = FlexValue.auto();
        private FlexValue y = FlexValue.auto();
        private FlexValue width = FlexValue.auto();
        private FlexValue height = FlexValue.auto();
        private FlexBorder border = FlexBorder.empty();
        private FlexBorder margin = FlexBorder.empty();
        private FlexBorder padding = FlexBorder.empty();
        private float growWeight = 0F;
        private float shrinkWeight = 0F;
        private FlexValue basis = FlexValue.auto();
        private FlexNodeType type = FlexNodeType.DEFAULT;

        // @formatter:off
        protected Builder() {}
        // @formatter:on

        public Builder from(final FlexNode node) {
            direction = node.getDirection();
            overflow = node.getOverflow();
            positionType = node.getPositionType();
            selfAlignment = node.getSelfAlignment();
            itemAlignment = node.getItemAlignment();
            contentAlignment = node.getContentAlignment();
            contentJustification = node.getContentJustification();
            wrap = node.getWrap();
            x = node.getX();
            y = node.getY();
            width = node.getWidth();
            height = node.getHeight();
            margin = node.getMargin();
            padding = node.getPadding();
            growWeight = node.getGrowWeight();
            shrinkWeight = node.getShrinkWeight();
            basis = node.getBasis();
            type = node.getType();
            return this;
        }

        public Builder type(final FlexNodeType type) {
            this.type = type;
            return this;
        }

        public Builder shrink(final float shrinkWeight) {
            this.shrinkWeight = shrinkWeight;
            return this;
        }

        public Builder grow(final float growWeight) {
            this.growWeight = growWeight;
            return this;
        }

        public Builder basis(final FlexValue basis) {
            this.basis = basis;
            return this;
        }

        public Builder border(final FlexBorder border) {
            this.border = border;
            return this;
        }

        public Builder wrap(final FlexWrap wrap) {
            this.wrap = wrap;
            return this;
        }

        public Builder direction(final FlexDirection direction) {
            this.direction = direction;
            return this;
        }

        public Builder overflow(final FlexOverflow overflow) {
            this.overflow = overflow;
            return this;
        }

        public Builder positionType(final FlexPositionType positionType) {
            this.positionType = positionType;
            return this;
        }

        public Builder alignSelf(final FlexAlignment selfAlignment) {
            this.selfAlignment = selfAlignment;
            return this;
        }

        public Builder alignItems(final FlexAlignment itemAlignment) {
            this.itemAlignment = itemAlignment;
            return this;
        }

        public Builder alignContent(final FlexAlignment contentAlignment) {
            this.contentAlignment = contentAlignment;
            return this;
        }

        public Builder justify(final FlexJustify contentJustification) {
            this.contentJustification = contentJustification;
            return this;
        }

        public Builder x(final FlexValue x) {
            this.x = x;
            return this;
        }

        public Builder y(final FlexValue y) {
            this.y = y;
            return this;
        }

        public Builder width(final FlexValue width) {
            this.width = width;
            return this;
        }

        public Builder height(final FlexValue height) {
            this.height = height;
            return this;
        }

        public Builder margin(final FlexBorder margin) {
            this.margin = margin;
            return this;
        }

        public Builder padding(final FlexBorder padding) {
            this.padding = padding;
            return this;
        }

        public DefaultFlexNode build() {
            return new DefaultFlexNode(direction,
                overflow,
                positionType,
                selfAlignment,
                itemAlignment,
                contentAlignment,
                contentJustification,
                wrap,
                x,
                y,
                width,
                height,
                border,
                margin,
                padding,
                growWeight,
                shrinkWeight,
                basis,
                type);
        }
    }
}
