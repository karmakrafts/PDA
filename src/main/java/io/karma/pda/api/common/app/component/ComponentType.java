package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.util.FactoryType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 17/02/2024
 */
public final class ComponentType<C extends Component> extends FactoryType<C> {
    public ComponentType(final ResourceLocation name, final Supplier<C> factory) {
        super(name, factory);
    }
}
