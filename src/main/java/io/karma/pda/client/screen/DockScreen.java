/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.screen;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.client.DockInteractionHandler;
import io.karma.pda.common.session.DockedSessionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

/**
 * @author Alexander Hinze
 * @since 10/03/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DockScreen extends Screen {
    private final long window;
    private int previousInputMode;

    public DockScreen(final Player player, final BlockPos pos) {
        super(Component.empty());
        window = Minecraft.getInstance().getWindow().getWindow();
        DockInteractionHandler.INSTANCE.engage(pos);
        // Set up the session
        final var sessionHandler = ClientAPI.getSessionHandler();
        // Make this the current session
        sessionHandler.createSession(new DockedSessionContext(player, pos)).thenAccept(sessionHandler::setSession);
    }

    @Override
    protected void init() {
        super.init();
        previousInputMode = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    @Override
    public void onClose() {
        ClientAPI.getSessionHandler().terminateSession();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, previousInputMode);
        DockInteractionHandler.INSTANCE.disengage();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
