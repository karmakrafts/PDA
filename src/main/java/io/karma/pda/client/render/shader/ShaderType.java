/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.render.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20;

/**
 * @author Alexander Hinze
 * @since 02/06/2024
 */
@OnlyIn(Dist.CLIENT)
public enum ShaderType {
    VERTEX(GL20.GL_VERTEX_SHADER), FRAGMENT(GL20.GL_FRAGMENT_SHADER);

    private final int glType;

    ShaderType(final int glType) {
        this.glType = glType;
    }

    public int getGlType() {
        return glType;
    }

    public int create() {
        return GL20.glCreateShader(glType);
    }
}
