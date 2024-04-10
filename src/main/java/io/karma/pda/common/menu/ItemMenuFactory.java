/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class ItemMenuFactory<M extends AbstractContainerMenu> implements IContainerFactory<M> {
    private final ItemMenuSupplier<M> supplier;

    public ItemMenuFactory(final ItemMenuSupplier<M> supplier) {
        this.supplier = supplier;
    }

    @Override
    public M create(final int id, final @NotNull Inventory inventory, final @Nullable FriendlyByteBuf buffer) {
        if (buffer == null) {
            throw new IllegalStateException("Created menu without passing required data because buffer was null");
        }
        final var clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) {
            throw new IllegalStateException("Created menu without passing required data because player was null");
        }
        final var isOffhand = buffer.readBoolean();
        final var stack = clientPlayer.getItemInHand(isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return supplier.create(id, inventory, stack);
    }
}
