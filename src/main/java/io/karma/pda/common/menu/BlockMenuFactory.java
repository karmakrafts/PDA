/*
 * Copyright (c) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 07/02/2024
 */
public final class BlockMenuFactory<M extends AbstractContainerMenu, E extends BlockEntity & Container>
    implements IContainerFactory<M> {
    private final BlockMenuSupplier<M, E> supplier;
    private final Class<E> entityType;

    public BlockMenuFactory(final BlockMenuSupplier<M, E> supplier, final Class<E> entityType) {
        this.supplier = supplier;
        this.entityType = entityType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M create(final int id, final @NotNull Inventory inventory, final @Nullable FriendlyByteBuf buffer) {
        if (buffer == null) {
            throw new IllegalStateException("Buffer cannot be null");
        }
        final var clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) {
            throw new IllegalStateException("Player cannot be null");
        }
        final var world = clientPlayer.clientLevel;
        final var pos = buffer.readBlockPos();
        final var entity = world.getBlockEntity(pos);
        if (entity == null || !entityType.isAssignableFrom(entity.getClass())) {
            throw new IllegalStateException("Block entity cannot be null");
        }
        return supplier.create(id, inventory, (E) entity);
    }
}
