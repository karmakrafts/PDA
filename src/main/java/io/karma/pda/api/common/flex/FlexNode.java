package io.karma.pda.api.common.flex;

import io.karma.pda.api.common.dispose.Disposable;
import io.karma.sliced.slice.Slice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.yoga.YGValue;
import org.lwjgl.util.yoga.Yoga;

import java.util.ArrayList;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
public class FlexNode implements Disposable {
    private final long address;
    private final ArrayList<FlexNode> children = new ArrayList<>();

    public FlexNode() {
        address = Yoga.YGNodeNew();
        if (address == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not allocate layout node");
        }
    }

    public void addChild(final FlexNode node) {
        if (children.contains(node)) {
            return;
        }
        Yoga.YGNodeInsertChild(address, node.address, children.size());
        children.add(node);
    }

    public void removeChild(final FlexNode node) {
        if (!children.contains(node)) {
            return;
        }
        Yoga.YGNodeRemoveChild(address, node.address);
        children.remove(node);
    }

    public Slice<FlexNode> getChildren() {
        return Slice.of(children);
    }

    // Direction

    public void setDirection(final FlexDirection direction) {
        Yoga.YGNodeStyleSetDirection(address, direction.getValue());
    }

    public FlexDirection getDirection() {
        return switch(Yoga.YGNodeStyleGetDirection(address)) { // @formatter:off
            case Yoga.YGFlexDirectionRow            -> FlexDirection.ROW;
            case Yoga.YGFlexDirectionRowReverse     -> FlexDirection.ROW_REVERSE;
            case Yoga.YGFlexDirectionColumn         -> FlexDirection.COLUMN;
            case Yoga.YGFlexDirectionColumnReverse  -> FlexDirection.COLUMN_REVERSE;
            default                                 -> throw new UnsupportedOperationException();
        }; // @formatter:on
    }

    // Margin

    public void setLeftMargin(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetMargin(address, Yoga.YGEdgeLeft, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetMarginPercent(address, Yoga.YGEdgeLeft, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setRightMargin(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetMargin(address, Yoga.YGEdgeRight, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetMarginPercent(address, Yoga.YGEdgeRight, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setTopMargin(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetMargin(address, Yoga.YGEdgeTop, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetMarginPercent(address, Yoga.YGEdgeTop, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setBottomMargin(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetMargin(address, Yoga.YGEdgeBottom, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetMarginPercent(address, Yoga.YGEdgeBottom, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    // Padding

    public void setLeftPadding(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPadding(address, Yoga.YGEdgeLeft, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPaddingPercent(address, Yoga.YGEdgeLeft, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setRightPadding(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPadding(address, Yoga.YGEdgeRight, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPaddingPercent(address, Yoga.YGEdgeRight, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setTopPadding(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPadding(address, Yoga.YGEdgeTop, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPaddingPercent(address, Yoga.YGEdgeTop, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public void setBottomPadding(final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPadding(address, Yoga.YGEdgeBottom, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPaddingPercent(address, Yoga.YGEdgeBottom, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    // Size

    public void setWidth(final FlexValue width) {
        switch (width.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetWidth(address, width.get());
            case PERCENT -> Yoga.YGNodeStyleSetWidthPercent(address, width.get());
            default      -> Yoga.YGNodeStyleSetWidthAuto(address);
        } // @formatter:on
    }

    public void setHeight(final FlexValue height) {
        switch (height.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetHeight(address, height.get());
            case PERCENT -> Yoga.YGNodeStyleSetHeightPercent(address, height.get());
            default      -> Yoga.YGNodeStyleSetHeightAuto(address);
        } // @formatter:on
    }

    public FlexValue getWidth() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.calloc(stack);
            Yoga.YGNodeStyleGetWidth(address, value);
            return FlexValue.fromStruct(value);
        }
    }

    public FlexValue getHeight() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.calloc(stack);
            Yoga.YGNodeStyleGetHeight(address, value);
            return FlexValue.fromStruct(value);
        }
    }

    // Absolute size

    public int getAbsoluteX() {
        return (int) Yoga.YGNodeLayoutGetLeft(address);
    }

    public int getAbsoluteY() {
        return (int) Yoga.YGNodeLayoutGetTop(address);
    }

    public int getAbsoluteWidth() {
        return (int) Yoga.YGNodeLayoutGetWidth(address);
    }

    public int getAbsoluteHeight() {
        return (int) Yoga.YGNodeLayoutGetHeight(address);
    }

    @Override
    public void dispose() {
        Yoga.YGNodeFree(address);
        for (final var child : children) {
            child.dispose();
        }
    }
}
