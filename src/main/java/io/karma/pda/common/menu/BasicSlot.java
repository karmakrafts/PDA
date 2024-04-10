/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public class BasicSlot extends Slot {
    protected final ItemStack icon;
    protected Consumer<ItemStack> setCallback;

    public BasicSlot(final @NotNull Container container, final int id, final int x, final int y, final ItemStack icon) {
        super(container, id, x, y);
        this.icon = icon;
    }

    public BasicSlot(final @NotNull Container container, final int id, final int x, final int y) {
        this(container, id, x, y, ItemStack.EMPTY);
    }

    public BasicSlot withSetCallback(final Consumer<ItemStack> setCallback) {
        this.setCallback = setCallback;
        return this;
    }

    @Override
    public void set(final @NotNull ItemStack stack) {
        super.set(stack);
        if (setCallback == null) {
            return;
        }
        setCallback.accept(stack);
    }

    public ItemStack getIcon() {
        return icon;
    }
}
