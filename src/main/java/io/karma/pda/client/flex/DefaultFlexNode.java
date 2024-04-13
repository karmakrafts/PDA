/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.flex;

import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.flex.*;
import io.karma.sliced.slice.Slice;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.yoga.YGValue;
import org.lwjgl.util.yoga.Yoga;

import java.util.ArrayList;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DefaultFlexNode implements FlexNode, Disposable {
    private final long address;
    private final ArrayList<DefaultFlexNode> children = new ArrayList<>();

    public DefaultFlexNode() {
        address = Yoga.YGNodeNew();
        if (address == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not allocate layout node");
        }
    }

    public static DefaultFlexNode copyOf(final FlexNode flexNode) {
        final var node = new DefaultFlexNode();
        node.setFrom(flexNode);
        return node;
    }

    public void addChild(final DefaultFlexNode node) {
        if (children.contains(node)) {
            return;
        }
        Yoga.YGNodeInsertChild(address, node.address, children.size());
        children.add(node);
    }

    public void removeChild(final DefaultFlexNode node) {
        if (!children.contains(node)) {
            return;
        }
        Yoga.YGNodeRemoveChild(address, node.address);
        children.remove(node);
    }

    public Slice<DefaultFlexNode> getChildren() {
        return Slice.of(children);
    }

    @Override
    public void setFrom(final FlexNode flexNode) {
        setDirection(flexNode.getDirection());
        setOverflow(flexNode.getOverflow());
        setSelfAlignment(flexNode.getSelfAlignment());
        setItemAlignment(flexNode.getItemAlignment());
        setContentAlignment(flexNode.getContentAlignment());
        setContentJustification(flexNode.getContentJustification());
        setMargin(flexNode.getMargin());
        setPadding(flexNode.getPadding());
        setPositionType(flexNode.getPositionType());
        setWidth(flexNode.getWidth());
        setHeight(flexNode.getHeight());
        setX(flexNode.getX());
        setY(flexNode.getY());
    }

    // Direction

    @Override
    public FlexDirection getDirection() {
        return FlexUtils.getDirection(Yoga.YGNodeStyleGetDirection(address));
    }

    public void setDirection(final FlexDirection direction) {
        Yoga.YGNodeStyleSetDirection(address, FlexUtils.getDirection(direction));
    }

    // Overflow

    @Override
    public FlexOverflow getOverflow() {
        return FlexUtils.getOverflow(Yoga.YGNodeStyleGetOverflow(address));
    }

    public void setOverflow(final FlexOverflow overflow) {
        Yoga.YGNodeStyleSetOverflow(address, FlexUtils.getOverflow(overflow));
    }

    // Alignments

    @Override
    public FlexAlignment getSelfAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignSelf(address));
    }

    public void setSelfAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignSelf(address, FlexUtils.getAlignment(alignment));
    }

    @Override
    public FlexAlignment getItemAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignItems(address));
    }

    public void setItemAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignItems(address, FlexUtils.getAlignment(alignment));
    }

    @Override
    public FlexAlignment getContentAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignContent(address));
    }

    public void setContentAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignContent(address, FlexUtils.getAlignment(alignment));
    }

    // Justification

    @Override
    public FlexJustify getContentJustification() {
        return FlexUtils.getJustify(Yoga.YGNodeStyleGetJustifyContent(address));
    }

    public void setContentJustification(final FlexJustify justify) {
        Yoga.YGNodeStyleSetJustifyContent(address, FlexUtils.getJustify(justify));
    }

    // Margin

    public void setMargin(final FlexEdge edge, final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetMargin(address, edge.getValue(), value.get());
            case PERCENT -> Yoga.YGNodeStyleSetMarginPercent(address, edge.getValue(), value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public FlexValue getMargin(final FlexEdge edge) {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetMargin(address, edge.getValue(), value);
            return FlexUtils.getValue(value);
        }
    }

    @Override
    public FlexBorder getMargin() {
        return FlexBorder.of(getMargin(FlexEdge.LEFT),
            getMargin(FlexEdge.RIGHT),
            getMargin(FlexEdge.TOP),
            getMargin(FlexEdge.BOTTOM));
    }

    public void setMargin(final FlexBorder margin) {
        setMargin(FlexEdge.LEFT, margin.getLeft());
        setMargin(FlexEdge.RIGHT, margin.getRight());
        setMargin(FlexEdge.TOP, margin.getTop());
        setMargin(FlexEdge.BOTTOM, margin.getBottom());
    }

    // Padding

    public void setPadding(final FlexEdge edge, final FlexValue value) {
        switch(value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPadding(address, edge.getValue(), value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPaddingPercent(address, edge.getValue(), value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public FlexValue getPadding(final FlexEdge edge) {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetPadding(address, edge.getValue(), value);
            return FlexUtils.getValue(value);
        }
    }

    @Override
    public FlexBorder getPadding() {
        return FlexBorder.of(getPadding(FlexEdge.LEFT),
            getPadding(FlexEdge.RIGHT),
            getPadding(FlexEdge.TOP),
            getPadding(FlexEdge.BOTTOM));
    }

    public void setPadding(final FlexBorder padding) {
        setPadding(FlexEdge.LEFT, padding.getLeft());
        setPadding(FlexEdge.RIGHT, padding.getRight());
        setPadding(FlexEdge.TOP, padding.getTop());
        setPadding(FlexEdge.BOTTOM, padding.getBottom());
    }

    // Position type

    @Override
    public FlexPositionType getPositionType() {
        return FlexUtils.getPositionType(Yoga.YGNodeStyleGetPositionType(address));
    }

    public void setPositionType(final FlexPositionType type) {
        Yoga.YGNodeStyleSetPositionType(address, FlexUtils.getPositionType(type));
    }

    // Position

    @Override
    public FlexValue getX() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetPosition(address, Yoga.YGEdgeLeft, value);
            return FlexUtils.getValue(value);
        }
    }

    public void setX(final FlexValue value) {
        switch (value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPosition(address, Yoga.YGEdgeLeft, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPositionPercent(address, Yoga.YGEdgeLeft, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    @Override
    public FlexValue getY() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetPosition(address, Yoga.YGEdgeTop, value);
            return FlexUtils.getValue(value);
        }
    }

    public void setY(final FlexValue value) {
        switch (value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPosition(address, Yoga.YGEdgeTop, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPositionPercent(address, Yoga.YGEdgeTop, value.get());
            default      -> throw new UnsupportedOperationException();
        } // @formatter:on
    }

    public int getAbsoluteX() {
        return (int) Yoga.YGNodeLayoutGetLeft(address);
    }

    public int getAbsoluteY() {
        return (int) Yoga.YGNodeLayoutGetTop(address);
    }

    // Size

    @Override
    public FlexValue getWidth() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetWidth(address, value);
            return FlexUtils.getValue(value);
        }
    }

    public void setWidth(final FlexValue width) {
        switch (width.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetWidth(address, width.get());
            case PERCENT -> Yoga.YGNodeStyleSetWidthPercent(address, width.get());
            default      -> Yoga.YGNodeStyleSetWidthAuto(address);
        } // @formatter:on
    }

    @Override
    public FlexValue getHeight() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetHeight(address, value);
            return FlexUtils.getValue(value);
        }
    }

    public void setHeight(final FlexValue height) {
        switch (height.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetHeight(address, height.get());
            case PERCENT -> Yoga.YGNodeStyleSetHeightPercent(address, height.get());
            default      -> Yoga.YGNodeStyleSetHeightAuto(address);
        } // @formatter:on
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
