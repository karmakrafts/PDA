package io.karma.pda.common.menu;

import io.karma.pda.common.block.DockBlock;
import io.karma.pda.common.entity.DockBlockEntity;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.inventory.ItemStorageView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class DockMenu extends BasicContainerMenu<DockBlockEntity> {
    public DockMenu(final int id, final Inventory playerInventory, final DockBlockEntity container) {
        super(ModMenus.dock.get(), id, container);
        bindPlayerInventory(playerInventory, 8, 84);
        addSlot(new FilteredSlot(container, 0, 80, 35, ModItems.pda.get()).withSetCallback(stack -> {
            final var player = playerInventory.player;
            final var world = player.level();
            final var pos = container.getBlockPos();
            updateBlockState(world, pos, stack);
        }));
    }

    private static void updateBlockState(final Level world, final BlockPos pos, final ItemStack stack) {
        final var blockState = world.getBlockState(pos);
        if (stack.isEmpty()) {
            world.setBlockAndUpdate(pos, blockState.setValue(DockBlock.STATE, DockBlock.State.NO_ITEM));
            return;
        }
        final var itemStorageView = new ItemStorageView(stack, PDAStorageMenu.CONTAINER_NAME);
        final var memoryCardStack = itemStorageView.getItem(0);
        final var state = memoryCardStack.isEmpty() ? DockBlock.State.ITEM_OFF : DockBlock.State.ITEM_ON;
        world.setBlockAndUpdate(pos, blockState.setValue(DockBlock.STATE, state));
    }
}
