package io.karma.pda.common.inventory;

import io.karma.pda.common.PDAMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class ItemContainerFactory<M extends AbstractContainerMenu> implements IContainerFactory<M> {
    private final ItemContainerSupplier<M> supplier;

    public ItemContainerFactory(final ItemContainerSupplier<M> supplier) {
        this.supplier = supplier;
    }

    @Override
    public M create(final int id, final @NotNull Inventory inventory, final @Nullable FriendlyByteBuf buffer) {
        if (buffer == null) {
            PDAMod.LOGGER.warn("Created menu without passing required data because buffer was null");
            return supplier.create(id, inventory, ItemStack.EMPTY);
        }
        final var clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) {
            PDAMod.LOGGER.warn("Created menu without passing required data because player was null");
            return supplier.create(id, inventory, ItemStack.EMPTY);
        }
        final var isOffhand = buffer.readBoolean();
        final var stack = clientPlayer.getItemInHand(isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return supplier.create(id, inventory, stack);
    }
}
