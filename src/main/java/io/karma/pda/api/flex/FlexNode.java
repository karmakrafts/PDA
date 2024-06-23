/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.flex;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 10/04/2024
 */
public interface FlexNode {
    @Nullable
    FlexNode getParent();

    void setFrom(final FlexNode flexNode);

    List<FlexNode> getChildren();

    void addChild(final FlexNode child);

    void removeChild(final FlexNode child);

    int indexOfChild(final FlexNode child);

    @Nullable
    FlexNode getChild(final int index);

    void clearChildren();

    FlexNodeType getType();

    void setType(final FlexNodeType type);

    float getGrowWeight();

    void setGrowWeight(final float growWeight);

    float getShrinkWeight();

    void setShrinkWeight(final float shrinkWeight);

    FlexValue getBasis();

    void setBasis(final FlexValue basis);

    FlexDirection getDirection();

    void setDirection(final FlexDirection direction);

    FlexOverflow getOverflow();

    void setOverflow(final FlexOverflow overflow);

    FlexPositionType getPositionType();

    void setPositionType(final FlexPositionType positionType);

    FlexAlignment getSelfAlignment();

    void setSelfAlignment(final FlexAlignment selfAlignment);

    FlexAlignment getItemAlignment();

    void setItemAlignment(final FlexAlignment itemAlignment);

    FlexAlignment getContentAlignment();

    void setContentAlignment(final FlexAlignment contentAlignment);

    FlexJustify getContentJustification();

    void setContentJustification(final FlexJustify contentJustification);

    FlexWrap getWrap();

    void setWrap(final FlexWrap wrap);

    FlexValue getX();

    void setX(final FlexValue x);

    FlexValue getY();

    void setY(final FlexValue y);

    FlexValue getWidth();

    void setWidth(final FlexValue width);

    FlexValue getHeight();

    void setHeight(final FlexValue height);

    FlexBorder getBorder();

    void setBorder(final FlexBorder border);

    FlexBorder getMargin();

    void setMargin(final FlexBorder margin);

    FlexBorder getPadding();

    void setPadding(final FlexBorder padding);

    int getAbsoluteWidth();

    int getAbsoluteHeight();

    int getAbsoluteX();

    int getAbsoluteY();

    void computeLayout();
}
