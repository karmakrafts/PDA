package io.karma.pda.common.init;

import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.app.component.ContainerComponent;
import io.karma.pda.common.app.component.LabelComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class ModComponents {
    public static RegistryObject<ComponentType<ContainerComponent>> container;
    public static RegistryObject<ComponentType<LabelComponent>> label;

    // @formatter:off
    private ModComponents() {}
    // @formatter:on

    @ApiStatus.Internal
    public static void register(final DeferredRegister<ComponentType<?>> register) {
        container = register.register("container",
            () -> new ComponentType<>(new ResourceLocation(Constants.MODID, "container"), ContainerComponent::new));
        label = register.register("label",
            () -> new ComponentType<>(new ResourceLocation(Constants.MODID, "label"), LabelComponent::new));
    }
}
