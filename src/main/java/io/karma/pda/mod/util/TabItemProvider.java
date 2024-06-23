/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
public interface TabItemProvider {
    void addToTab(final NonNullList<ItemStack> items);
}
