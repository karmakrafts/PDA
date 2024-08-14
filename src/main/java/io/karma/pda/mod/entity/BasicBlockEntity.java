/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 06/02/2024
 */
public abstract class BasicBlockEntity extends BlockEntity {
    public BasicBlockEntity(final @NotNull BlockEntityType<?> type,
                            final @NotNull BlockPos pos,
                            final @NotNull BlockState state) {
        super(type, pos, state);
    }

    protected abstract void readFromNBT(final CompoundTag tag);

    protected abstract void writeToNBT(final CompoundTag tag);

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        final var tag = new CompoundTag();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(final @NotNull CompoundTag tag) {
        readFromNBT(tag);
    }

    @Override
    public void load(final @NotNull CompoundTag tag) {
        super.load(tag);
        readFromNBT(tag);
    }

    @Override
    protected void saveAdditional(final @NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        writeToNBT(tag);
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::serializeNBT);
    }

    @Override
    public void onDataPacket(final @NotNull Connection conn, final @NotNull ClientboundBlockEntityDataPacket packet) {
        deserializeNBT(Objects.requireNonNull(packet.getTag()));
    }
}
