/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.flex;

import io.karma.pda.api.common.dispose.Disposable;
import io.karma.pda.api.common.flex.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.yoga.YGValue;
import org.lwjgl.util.yoga.Yoga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientFlexNode implements FlexNode, Disposable {
    private final long address;
    private final ArrayList<FlexNode> children = new ArrayList<>();
    private boolean isDisposed;

    public ClientFlexNode() {
        address = Yoga.YGNodeNew();
        if (address == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not allocate layout node");
        }
    }

    public static ClientFlexNode copyOf(final FlexNode flexNode) {
        final var node = new ClientFlexNode();
        node.setFrom(flexNode);
        return node;
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
    public void addChild(final FlexNode child) {
        if (children.contains(child)) {
            return;
        }
        if (child instanceof ClientFlexNode defaultNode) {
            Yoga.YGNodeInsertChild(address, defaultNode.address, children.size());
        }
        children.add(child);
    }

    @Override
    public void removeChild(final FlexNode child) {
        if (!children.contains(child)) {
            return;
        }
        if (child instanceof ClientFlexNode defaultNode) {
            Yoga.YGNodeRemoveChild(address, defaultNode.address);
        }
        children.remove(child);
    }

    @Override
    public List<FlexNode> getChildren() {
        return Collections.unmodifiableList(children);
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

    @Override
    public void setDirection(final FlexDirection direction) {
        Yoga.YGNodeStyleSetDirection(address, FlexUtils.getDirection(direction));
    }

    // Overflow

    @Override
    public FlexOverflow getOverflow() {
        return FlexUtils.getOverflow(Yoga.YGNodeStyleGetOverflow(address));
    }

    @Override
    public void setOverflow(final FlexOverflow overflow) {
        Yoga.YGNodeStyleSetOverflow(address, FlexUtils.getOverflow(overflow));
    }

    // Alignments

    @Override
    public FlexAlignment getSelfAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignSelf(address));
    }

    @Override
    public void setSelfAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignSelf(address, FlexUtils.getAlignment(alignment));
    }

    @Override
    public FlexAlignment getItemAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignItems(address));
    }

    @Override
    public void setItemAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignItems(address, FlexUtils.getAlignment(alignment));
    }

    @Override
    public FlexAlignment getContentAlignment() {
        return FlexUtils.getAlignment(Yoga.YGNodeStyleGetAlignContent(address));
    }

    @Override
    public void setContentAlignment(final FlexAlignment alignment) {
        Yoga.YGNodeStyleSetAlignContent(address, FlexUtils.getAlignment(alignment));
    }

    // Justification

    @Override
    public FlexJustify getContentJustification() {
        return FlexUtils.getJustify(Yoga.YGNodeStyleGetJustifyContent(address));
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void setX(final FlexValue value) {
        switch (value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPosition(address, Yoga.YGEdgeLeft, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPositionPercent(address, Yoga.YGEdgeLeft, value.get());
            default      -> {}
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

    @Override
    public void setY(final FlexValue value) {
        switch (value.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetPosition(address, Yoga.YGEdgeTop, value.get());
            case PERCENT -> Yoga.YGNodeStyleSetPositionPercent(address, Yoga.YGEdgeTop, value.get());
            default      -> {}
        } // @formatter:on
    }

    @Override
    public int getAbsoluteX() {
        return (int) Yoga.YGNodeLayoutGetLeft(address);
    }

    @Override
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

    @Override
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

    @Override
    public void setHeight(final FlexValue height) {
        switch (height.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetHeight(address, height.get());
            case PERCENT -> Yoga.YGNodeStyleSetHeightPercent(address, height.get());
            default      -> Yoga.YGNodeStyleSetHeightAuto(address);
        } // @formatter:on
    }

    @Override
    public int getAbsoluteWidth() {
        return (int) Yoga.YGNodeLayoutGetWidth(address);
    }

    @Override
    public int getAbsoluteHeight() {
        return (int) Yoga.YGNodeLayoutGetHeight(address);
    }

    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        // We do not recursively dispose our children here since the node handler takes care of that
        Yoga.YGNodeFree(address);
        isDisposed = true;
    }

    @Override
    public void computeLayout(final int width, final int height) {
        Yoga.YGNodeCalculateLayout(address, width, height, Yoga.YGDirectionLTR);
    }
}
