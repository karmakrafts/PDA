/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.flex;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public final class DefaultFlexSpec implements FlexSpec {
    private final FlexDirection direction;
    private final FlexOverflow overflow;
    private final FlexPositionType positionType;
    private final FlexAlignment selfAlignment;
    private final FlexAlignment itemAlignment;
    private final FlexAlignment contentAlignment;
    private final FlexJustify contentJustification;
    private final FlexValue x;
    private final FlexValue y;
    private final FlexValue width;
    private final FlexValue height;
    private final FlexBorder margin;
    private final FlexBorder padding;

    private DefaultFlexSpec(final FlexDirection direction, final FlexOverflow overflow,
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

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public FlexDirection getDirection() {
        return direction;
    }

    @Override
    public FlexPositionType getPositionType() {
        return positionType;
    }

    @Override
    public FlexOverflow getOverflow() {
        return overflow;
    }

    @Override
    public FlexAlignment getSelfAlignment() {
        return selfAlignment;
    }

    @Override
    public FlexAlignment getItemAlignment() {
        return itemAlignment;
    }

    @Override
    public FlexAlignment getContentAlignment() {
        return contentAlignment;
    }

    @Override
    public FlexJustify getContentJustification() {
        return contentJustification;
    }

    @Override
    public FlexValue getX() {
        return x;
    }

    @Override
    public FlexValue getY() {
        return y;
    }

    @Override
    public FlexValue getWidth() {
        return width;
    }

    @Override
    public FlexValue getHeight() {
        return height;
    }

    @Override
    public FlexBorder getMargin() {
        return margin;
    }

    @Override
    public FlexBorder getPadding() {
        return padding;
    }

    public static final class Builder {
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
        private Builder() {}
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

        public DefaultFlexSpec build() {
            return new DefaultFlexSpec(direction,
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
