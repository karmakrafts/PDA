/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.screen;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.common.session.HandheldSessionContext;
import io.karma.pda.client.render.item.PDAItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class PDAScreen extends Screen {
    private final EnumSet<InteractionHand> hands;

    public PDAScreen(final Player player, final EnumSet<InteractionHand> hands) {
        super(Component.empty());
        this.hands = hands;
        hands.forEach(hand -> PDAItemRenderer.INSTANCE.setEngaged(hand, true));
        // Set up session
        final var sessionHandler = ClientAPI.getSessionHandler();
        sessionHandler.createSession(hands.stream().map(hand -> new HandheldSessionContext(player, hand)).toList(),
            InteractionHand.MAIN_HAND).thenAccept(sessionHandler::setSession);
    }

    @Override
    public void onClose() {
        ClientAPI.getSessionHandler().terminateSession();
        hands.forEach(hand -> PDAItemRenderer.INSTANCE.setEngaged(hand, false));
        // Play sound when disengaging
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        player.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.3F, 1.75F);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
