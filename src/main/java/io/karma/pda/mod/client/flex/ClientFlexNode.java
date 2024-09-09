/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.flex;

import io.karma.pda.api.flex.*;
import io.karma.peregrine.api.dispose.Disposable;
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
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 20/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientFlexNode implements FlexNode, Disposable {
    private final long address;
    private final ArrayList<FlexNode> children = new ArrayList<>();
    private FlexNode parent;
    private boolean isDisposed;

    public ClientFlexNode() {
        address = Yoga.YGNodeNew();
        if (address == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not allocate layout node");
        }
    }

    @Override
    public @Nullable FlexNode getParent() {
        return parent;
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
            defaultNode.parent = this;
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
            defaultNode.parent = null;
        }
        children.remove(child);
    }

    @Override
    public List<FlexNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setFrom(final FlexNode node) {
        setDirection(node.getDirection());
        setOverflow(node.getOverflow());
        setSelfAlignment(node.getSelfAlignment());
        setItemAlignment(node.getItemAlignment());
        setContentAlignment(node.getContentAlignment());
        setContentJustification(node.getContentJustification());
        setWrap(node.getWrap());
        setMargin(node.getMargin());
        setPadding(node.getPadding());
        setPositionType(node.getPositionType());
        setWidth(node.getWidth());
        setHeight(node.getHeight());
        setX(node.getX());
        setY(node.getY());
        setGrowWeight(node.getGrowWeight());
        setShrinkWeight(node.getShrinkWeight());
        setBasis(node.getBasis());
        setType(node.getType());
    }

    // Type

    @Override
    public FlexNodeType getType() {
        return FlexUtils.getType(Yoga.YGNodeGetNodeType(address));
    }

    @Override
    public void setType(final FlexNodeType type) {
        Yoga.YGNodeSetNodeType(address, FlexUtils.getType(type));
    }

    // Grow

    @Override
    public float getGrowWeight() {
        return Yoga.YGNodeStyleGetFlexGrow(address);
    }

    @Override
    public void setGrowWeight(final float growWeight) {
        Yoga.YGNodeStyleSetFlexGrow(address, growWeight);
    }

    // Shrink

    @Override
    public float getShrinkWeight() {
        return Yoga.YGNodeStyleGetFlexShrink(address);
    }

    @Override
    public void setShrinkWeight(final float shrinkWeight) {
        Yoga.YGNodeStyleSetFlexShrink(address, shrinkWeight);
    }

    // Base

    @Override
    public FlexValue getBasis() {
        try (final var stack = MemoryStack.stackPush()) {
            final var value = YGValue.malloc(stack);
            Yoga.YGNodeStyleGetFlexBasis(address, value);
            return FlexUtils.getValue(value);
        }
    }

    @Override
    public void setBasis(final FlexValue basis) {
        switch(basis.getType()) { // @formatter:off
            case PIXEL   -> Yoga.YGNodeStyleSetFlexBasis(address, basis.get());
            case PERCENT -> Yoga.YGNodeStyleSetFlexBasisPercent(address, basis.get());
            default      -> Yoga.YGNodeStyleSetFlexBasisAuto(address);
        } // @formatter:on
    }

    // Wrap

    @Override
    public FlexWrap getWrap() {
        return FlexUtils.getWrap(Yoga.YGNodeStyleGetFlexWrap(address));
    }

    @Override
    public void setWrap(final FlexWrap wrap) {
        Yoga.YGNodeStyleSetFlexWrap(address, FlexUtils.getWrap(wrap));
    }

    // Direction

    @Override
    public FlexDirection getDirection() {
        return FlexUtils.getDirection(Yoga.YGNodeStyleGetFlexDirection(address));
    }

    @Override
    public void setDirection(final FlexDirection direction) {
        Yoga.YGNodeStyleSetFlexDirection(address, FlexUtils.getDirection(direction));
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

    // Border

    public void setBorder(final FlexEdge edge, final FlexValue value) {
        if (Objects.requireNonNull(value.getType()) != FlexValueType.PIXEL) {
            throw new UnsupportedOperationException();
        }
        Yoga.YGNodeStyleSetBorder(address, edge.getValue(), value.get());
    }

    public FlexValue getBorder(final FlexEdge edge) {
        return FlexValue.pixel((int) Yoga.YGNodeStyleGetBorder(address, edge.getValue()));
    }

    @Override
    public FlexBorder getBorder() {
        return FlexBorder.of(getBorder(FlexEdge.LEFT),
            getBorder(FlexEdge.RIGHT),
            getBorder(FlexEdge.TOP),
            getMargin(FlexEdge.BOTTOM));
    }

    @Override
    public void setBorder(final FlexBorder margin) {
        setBorder(FlexEdge.LEFT, margin.getLeft());
        setBorder(FlexEdge.RIGHT, margin.getRight());
        setBorder(FlexEdge.TOP, margin.getTop());
        setBorder(FlexEdge.BOTTOM, margin.getBottom());
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

    @Override
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
        final var x = (int) Yoga.YGNodeLayoutGetLeft(address);
        if (parent != null) {
            return x + parent.getAbsoluteX();
        }
        return x;
    }

    @Override
    public int getAbsoluteY() {
        final var y = (int) Yoga.YGNodeLayoutGetTop(address);
        if (parent != null) {
            return y + parent.getAbsoluteY();
        }
        return y;
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
        for (final var child : children) {
            if (!(child instanceof Disposable disposable)) {
                continue;
            }
            disposable.dispose();
        }
        Yoga.YGNodeFree(address);
        isDisposed = true;
    }

    @Override
    public void computeLayout() {
        Yoga.YGNodeCalculateLayout(address, Yoga.YGUndefined, Yoga.YGUndefined, Yoga.YGDirectionLTR);
    }
}
