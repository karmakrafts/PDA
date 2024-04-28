/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.screen;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.client.interaction.PDAInteractionHandler;
import io.karma.pda.common.item.PDAItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
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
    private final Session session;

    public PDAScreen(final EnumSet<InteractionHand> hands, final Session session) {
        super(Component.empty());
        this.hands = hands;
        this.session = session;
        hands.forEach(hand -> PDAInteractionHandler.INSTANCE.setEngaged(hand, true));
    }

    public EnumSet<InteractionHand> getHands() {
        return hands;
    }

    @Override
    public void onClose() {
        // Terminate session
        final var sessionHandler = ClientAPI.getSessionHandler();
        sessionHandler.terminateSession(session).thenAccept(v -> {
            sessionHandler.setActiveSession(null);
            Minecraft.getInstance().execute(() -> {
                // Cursed state notify for the flag in the Item class
                PDAItem.isScreenOpen = false;
                // Disengage the renderer
                hands.forEach(hand -> PDAInteractionHandler.INSTANCE.setEngaged(hand, false));
                // Play sound when disengaging
                final var player = Objects.requireNonNull(Minecraft.getInstance().player);
                player.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.3F, 1.75F);
                super.onClose();
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
