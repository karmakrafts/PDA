/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.karma.pda.api.common.dispose.Disposable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class DefaultFlexNode implements FlexNode {
    private static final DefaultFlexNode DEFAULTS = new DefaultFlexNode(FlexDirection.ROW,
        FlexOverflow.VISIBLE,
        FlexPositionType.RELATIVE,
        FlexAlignment.CENTER,
        FlexAlignment.CENTER,
        FlexAlignment.CENTER,
        FlexJustify.CENTER,
        FlexValue.auto(),
        FlexValue.auto(),
        FlexValue.auto(),
        FlexValue.auto(),
        FlexBorder.empty(),
        FlexBorder.empty());

    private final ArrayList<FlexNode> children = new ArrayList<>();
    private FlexDirection direction;
    private FlexOverflow overflow;
    private FlexPositionType positionType;
    private FlexAlignment selfAlignment;
    private FlexAlignment itemAlignment;
    private FlexAlignment contentAlignment;
    private FlexJustify contentJustification;
    private FlexValue x;
    private FlexValue y;
    private FlexValue width;
    private FlexValue height;
    private FlexBorder margin;
    private FlexBorder padding;

    public DefaultFlexNode() {
        setFrom(DEFAULTS);
    }

    @JsonIgnore
    private DefaultFlexNode(final FlexDirection direction, final FlexOverflow overflow,
                            final FlexPositionType positionType, final FlexAlignment selfAlignment,
                            final FlexAlignment itemAlignment, final FlexAlignment contentAlignment,
                            final FlexJustify contentJustification, final FlexValue x, final FlexValue y,
                            final FlexValue width, final FlexValue height, final FlexBorder margin,
                            final FlexBorder padding) {
        this.direction = direction;
        this.overflow = overflow;
        this.positionType = positionType;
        this.selfAlignment = selfAlignment;
        this.itemAlignment = itemAlignment;
        this.contentAlignment = contentAlignment;
        this.contentJustification = contentJustification;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.padding = padding;
    }

    public static DefaultFlexNode defaults() {
        return DEFAULTS;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnore
    @Override
    public void setFrom(final FlexNode flexNode) {
        direction = flexNode.getDirection();
        overflow = flexNode.getOverflow();
        positionType = flexNode.getPositionType();
        selfAlignment = flexNode.getSelfAlignment();
        itemAlignment = flexNode.getItemAlignment();
        contentAlignment = flexNode.getContentAlignment();
        contentJustification = flexNode.getContentJustification();
        x = flexNode.getX();
        y = flexNode.getY();
        width = flexNode.getWidth();
        height = flexNode.getHeight();
        margin = flexNode.getMargin();
        padding = flexNode.getPadding();
    }

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public List<FlexNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @JsonIgnore
    @Override
    public void addChild(final FlexNode child) {
        if (children.contains(child)) {
            return;
        }
        children.add(child);
    }

    @JsonIgnore
    @Override
    public void removeChild(final FlexNode child) {
        children.remove(child);
    }

    @JsonIgnore
    @Override
    public int indexOfChild(final FlexNode child) {
        return children.indexOf(child);
    }

    @JsonIgnore
    @Override
    public @Nullable FlexNode getChild(final int index) {
        if (children.isEmpty() || index < 0 || index >= children.size()) {
            return null;
        }
        return children.get(index);
    }

    @JsonSetter("direction")
    @Override
    public void setDirection(final FlexDirection direction) {
        this.direction = direction;
    }

    @JsonSetter("overflow")
    @Override
    public void setOverflow(final FlexOverflow overflow) {
        this.overflow = overflow;
    }

    @JsonSetter("position_type")
    @Override
    public void setPositionType(final FlexPositionType positionType) {
        this.positionType = positionType;
    }

    @JsonSetter("self_alignment")
    @Override
    public void setSelfAlignment(final FlexAlignment selfAlignment) {
        this.selfAlignment = selfAlignment;
    }

    @JsonSetter("item_alignment")
    @Override
    public void setItemAlignment(final FlexAlignment itemAlignment) {
        this.itemAlignment = itemAlignment;
    }

    @JsonSetter("content_alignment")
    @Override
    public void setContentAlignment(final FlexAlignment contentAlignment) {
        this.contentAlignment = contentAlignment;
    }

    @JsonSetter("content_justification")
    @Override
    public void setContentJustification(final FlexJustify contentJustification) {
        this.contentJustification = contentJustification;
    }

    @JsonSetter("x")
    @Override
    public void setX(final FlexValue x) {
        this.x = x;
    }

    @JsonSetter("y")
    @Override
    public void setY(final FlexValue y) {
        this.y = y;
    }

    @JsonSetter("width")
    @Override
    public void setWidth(final FlexValue width) {
        this.width = width;
    }

    @JsonSetter("height")
    @Override
    public void setHeight(final FlexValue height) {
        this.height = height;
    }

    @JsonSetter("margin")
    @Override
    public void setMargin(final FlexBorder margin) {
        this.margin = margin;
    }

    @JsonSetter("padding")
    @Override
    public void setPadding(final FlexBorder padding) {
        this.padding = padding;
    }

    @JsonGetter("direction")
    @Override
    public FlexDirection getDirection() {
        return direction;
    }

    @JsonGetter("position_type")
    @Override
    public FlexPositionType getPositionType() {
        return positionType;
    }

    @JsonGetter("overflow")
    @Override
    public FlexOverflow getOverflow() {
        return overflow;
    }

    @JsonGetter("self_alignment")
    @Override
    public FlexAlignment getSelfAlignment() {
        return selfAlignment;
    }

    @JsonGetter("item_alignment")
    @Override
    public FlexAlignment getItemAlignment() {
        return itemAlignment;
    }

    @JsonGetter("content_alignment")
    @Override
    public FlexAlignment getContentAlignment() {
        return contentAlignment;
    }

    @JsonGetter("content_justification")
    @Override
    public FlexJustify getContentJustification() {
        return contentJustification;
    }

    @JsonGetter("x")
    @Override
    public FlexValue getX() {
        return x;
    }

    @JsonGetter("y")
    @Override
    public FlexValue getY() {
        return y;
    }

    @JsonGetter("width")
    @Override
    public FlexValue getWidth() {
        return width;
    }

    @JsonGetter("height")
    @Override
    public FlexValue getHeight() {
        return height;
    }

    @JsonGetter("margin")
    @Override
    public FlexBorder getMargin() {
        return margin;
    }

    @JsonGetter("padding")
    @Override
    public FlexBorder getPadding() {
        return padding;
    }

    @JsonIgnore
    @Override
    public int getAbsoluteWidth() {
        throw new UnsupportedOperationException();
    }

    @JsonIgnore
    @Override
    public int getAbsoluteHeight() {
        throw new UnsupportedOperationException();
    }

    @JsonIgnore
    @Override
    public int getAbsoluteX() {
        throw new UnsupportedOperationException();
    }

    @JsonIgnore
    @Override
    public int getAbsoluteY() {
        throw new UnsupportedOperationException();
    }

    @JsonIgnore
    @Override
    public void computeLayout(final int width, final int height) {
    }

    public static class Builder {
        private FlexDirection direction = FlexDirection.ROW;
        private FlexOverflow overflow = FlexOverflow.HIDDEN;
        private FlexPositionType positionType = FlexPositionType.RELATIVE;
        private FlexAlignment selfAlignment = FlexAlignment.AUTO;
        private FlexAlignment itemAlignment = FlexAlignment.AUTO;
        private FlexAlignment contentAlignment = FlexAlignment.AUTO;
        private FlexJustify contentJustification = FlexJustify.CENTER;
        private FlexValue x = FlexValue.auto();
        private FlexValue y = FlexValue.auto();
        private FlexValue width = FlexValue.auto();
        private FlexValue height = FlexValue.auto();
        private FlexBorder margin = FlexBorder.empty();
        private FlexBorder padding = FlexBorder.empty();

        // @formatter:off
        protected Builder() {}
        // @formatter:on

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
                x,
                y,
                width,
                height,
                margin,
                padding);
        }
    }
}
