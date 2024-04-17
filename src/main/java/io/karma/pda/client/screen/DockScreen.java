/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.screen;

import io.karma.pda.api.client.ClientAPI;
import io.karma.pda.api.common.session.Session;
import io.karma.pda.client.interaction.DockInteractionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
    private final Session session;
    private int previousInputMode;

    public DockScreen(final BlockPos pos, final Session session) {
        super(Component.empty());
        window = Minecraft.getInstance().getWindow().getWindow();
        this.session = session;
        DockInteractionHandler.INSTANCE.engage(pos);
    }

    @Override
    protected void init() {
        super.init();
        previousInputMode = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    @Override
    public void onClose() {
        final var sessionHandler = ClientAPI.getSessionHandler();
        sessionHandler.terminateSession(session).thenAccept(v -> {
            sessionHandler.setActiveSession(null);
            Minecraft.getInstance().execute(() -> {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, previousInputMode);
                DockInteractionHandler.INSTANCE.disengage();
                super.onClose();
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
