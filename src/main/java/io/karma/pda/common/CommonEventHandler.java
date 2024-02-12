package io.karma.pda.common;

import io.karma.pda.common.init.ModBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class CommonEventHandler {
    public static final CommonEventHandler INSTANCE = new CommonEventHandler();

    // @formatter:off
    private CommonEventHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final var world = event.getLevel();
        final var player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }
        final var pos = event.getPos();
        final var state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.dock.get()) {
            return;
        }
        event.setUseBlock(Event.Result.ALLOW);
    }
}
