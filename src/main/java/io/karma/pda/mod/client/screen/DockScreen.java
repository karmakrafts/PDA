/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.screen;

import io.karma.pda.api.session.Session;
import io.karma.pda.mod.client.interaction.DockInteractionHandler;
import io.karma.pda.mod.client.session.ClientSessionHandler;
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
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        // Allow to manually toggle mouse grabbing using CTRL+ESC
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0 && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            final var isGrabbed = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
            GLFW.glfwSetInputMode(window,
                GLFW.GLFW_CURSOR,
                isGrabbed ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        previousInputMode = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    @Override
    public void onClose() {
        final var sessionHandler = ClientSessionHandler.INSTANCE;
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
