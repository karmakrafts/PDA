/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.flex;

import io.karma.pda.api.common.flex.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.util.yoga.YGValue;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class FlexUtils {
    // @formatter:off
    private FlexUtils() {}
    // @formatter:on

    public static int getType(final FlexNodeType type) {
        return switch(type) { // @formatter:off
            case TEXT -> Yoga.YGNodeTypeText;
            default   -> Yoga.YGNodeTypeDefault;
        }; // @formatter:on
    }

    public static FlexNodeType getType(final int value) {
        return switch(value) { // @formatter:off
            case Yoga.YGNodeTypeText -> FlexNodeType.TEXT;
            default                  -> FlexNodeType.DEFAULT;
        }; // @formatter:on
    }

    public static FlexValue getValue(final YGValue value) {
        return switch (value.unit()) { // @formatter:off
            case Yoga.YGUnitPoint   -> FlexValue.pixel((int) value.value());
            case Yoga.YGUnitPercent -> FlexValue.percent(value.value());
            default                 -> FlexValue.auto();
        }; // @formatter:on
    }

    public static int getWrap(final FlexWrap wrap) {
        return switch(wrap) { // @formatter:off
            case WRAP         -> Yoga.YGWrapWrap;
            case WRAP_REVERSE -> Yoga.YGWrapReverse;
            default           -> Yoga.YGWrapNoWrap;
        }; // @formatter:on
    }

    public static FlexWrap getWrap(final int value) {
        return switch(value) { // @formatter:off
            case Yoga.YGWrapWrap    -> FlexWrap.WRAP;
            case Yoga.YGWrapReverse -> FlexWrap.WRAP_REVERSE;
            default                 -> FlexWrap.NONE;
        }; // @formatter:on
    }

    public static FlexAlignment getAlignment(final int value) {
        return switch (value) { // @formatter:off
            case Yoga.YGAlignAuto          -> FlexAlignment.AUTO;
            case Yoga.YGAlignFlexStart     -> FlexAlignment.FLEX_START;
            case Yoga.YGAlignCenter        -> FlexAlignment.CENTER;
            case Yoga.YGAlignFlexEnd       -> FlexAlignment.FLEX_END;
            case Yoga.YGAlignStretch       -> FlexAlignment.STRETCH;
            case Yoga.YGAlignBaseline      -> FlexAlignment.BASELINE;
            case Yoga.YGAlignSpaceBetween  -> FlexAlignment.SPACE_BETWEEN;
            case Yoga.YGAlignSpaceAround   -> FlexAlignment.SPACE_AROUND;
            default                        -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static int getAlignment(final FlexAlignment alignment) {
        return switch (alignment) { // @formatter:off
            case AUTO           -> Yoga.YGAlignAuto;
            case FLEX_START     -> Yoga.YGAlignFlexStart;
            case CENTER         -> Yoga.YGAlignCenter;
            case FLEX_END       -> Yoga.YGAlignFlexEnd;
            case STRETCH        -> Yoga.YGAlignStretch;
            case BASELINE       -> Yoga.YGAlignBaseline;
            case SPACE_BETWEEN  -> Yoga.YGAlignSpaceBetween;
            case SPACE_AROUND   -> Yoga.YGAlignSpaceAround;
            default             -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static FlexJustify getJustify(final int value) {
        return switch (value) {// @formatter:off
            case Yoga.YGJustifyFlexStart    -> FlexJustify.FLEX_START;
            case Yoga.YGJustifyCenter       -> FlexJustify.CENTER;
            case Yoga.YGJustifyFlexEnd      -> FlexJustify.FLEX_END;
            case Yoga.YGJustifySpaceBetween -> FlexJustify.SPACE_BETWEEN;
            case Yoga.YGJustifySpaceAround  -> FlexJustify.SPACE_AROUND;
            case Yoga.YGJustifySpaceEvenly  -> FlexJustify.SPACE_EVENLY;
            default                         -> throw new IllegalArgumentException();
        };// @formatter:on
    }

    public static int getJustify(final FlexJustify value) {
        return switch (value) {// @formatter:off
            case FLEX_START     -> Yoga.YGJustifyFlexStart;
            case CENTER         -> Yoga.YGJustifyCenter;
            case FLEX_END       -> Yoga.YGJustifyFlexEnd;
            case SPACE_BETWEEN  -> Yoga.YGJustifySpaceBetween;
            case SPACE_AROUND   -> Yoga.YGJustifySpaceAround;
            case SPACE_EVENLY   -> Yoga.YGJustifySpaceEvenly;
            default             -> throw new IllegalArgumentException();
        };// @formatter:on
    }

    public static FlexOverflow getOverflow(final int value) {
        return switch (value) { // @formatter:off
            case Yoga.YGOverflowVisible -> FlexOverflow.VISIBLE;
            case Yoga.YGOverflowHidden  -> FlexOverflow.HIDDEN;
            case Yoga.YGOverflowScroll  -> FlexOverflow.SCROLL;
            default                     -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static int getOverflow(final FlexOverflow value) {
        return switch (value) { // @formatter:off
            case VISIBLE -> Yoga.YGOverflowVisible;
            case HIDDEN  -> Yoga.YGOverflowHidden;
            case SCROLL  -> Yoga.YGOverflowScroll;
            default      -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static FlexPositionType getPositionType(final int value) {
        return switch(value) { // @formatter:off
            case Yoga.YGPositionTypeStatic   -> FlexPositionType.STATIC;
            case Yoga.YGPositionTypeRelative -> FlexPositionType.RELATIVE;
            case Yoga.YGPositionTypeAbsolute -> FlexPositionType.ABSOLUTE;
            default                          -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static int getPositionType(final FlexPositionType value) {
        return switch(value) { // @formatter:off
            case STATIC  -> Yoga.YGPositionTypeStatic;
            case RELATIVE -> Yoga.YGPositionTypeRelative;
            case ABSOLUTE -> Yoga.YGPositionTypeAbsolute;
            default                          -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static FlexDirection getDirection(final int value) {
        return switch(value) { // @formatter:off
            case Yoga.YGFlexDirectionRow            -> FlexDirection.ROW;
            case Yoga.YGFlexDirectionRowReverse     -> FlexDirection.ROW_REVERSE;
            case Yoga.YGFlexDirectionColumn         -> FlexDirection.COLUMN;
            case Yoga.YGFlexDirectionColumnReverse  -> FlexDirection.COLUMN_REVERSE;
            default                                 -> throw new IllegalArgumentException();
        }; // @formatter:on
    }

    public static int getDirection(final FlexDirection direction) {
        return switch(direction) { // @formatter:off
            case ROW            -> Yoga.YGFlexDirectionRow;
            case ROW_REVERSE    -> Yoga.YGFlexDirectionRowReverse;
            case COLUMN         -> Yoga.YGFlexDirectionColumn;
            case COLUMN_REVERSE -> Yoga.YGFlexDirectionColumnReverse;
            default             -> throw new IllegalArgumentException();
        }; // @formatter:on
    }
}
