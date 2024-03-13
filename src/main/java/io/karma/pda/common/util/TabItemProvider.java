package io.karma.pda.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
public interface TabItemProvider {
    void addToTab(final NonNullList<ItemStack> items);
}
