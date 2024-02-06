package io.karma.pda.entity;

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
public class BasicBlockEntity extends BlockEntity {
    public BasicBlockEntity(final @NotNull BlockEntityType<?> type, final @NotNull BlockPos pos,
                            final @NotNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void deserializeNBT(final @NotNull CompoundTag tag) {
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, getType(), getUpdateTag());
    }

    @Override
    public void handleUpdateTag(final @NotNull CompoundTag tag) {
        deserializeNBT(tag);
    }

    @Override
    public void onDataPacket(final @NotNull Connection conn, final @NotNull ClientboundBlockEntityDataPacket packet) {
        handleUpdateTag(Objects.requireNonNull(packet.getTag()));
        if (level != null) {
            level.blockUpdated(worldPosition, getBlockState().getBlock());
        }
    }
}
