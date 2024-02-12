package io.karma.pda.client;

import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
    public static final ResourceLocation PDA_MODEL_DISENGAGED = new ResourceLocation(PDAMod.MODID,
        "item/pda_disengaged");
    public static final ResourceLocation PDA_MODEL_DISENGAGED_H = new ResourceLocation(PDAMod.MODID,
        "item/pda_disengaged_horizontal");
    private float partialTick;

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    @ApiStatus.Internal
    public void setup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterAdditionalModels);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderTick);
    }

    // Make sure our actual baked models get loaded by the game
    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        event.register(PDA_MODEL_DISENGAGED);
        event.register(PDA_MODEL_DISENGAGED_H);
    }

    private void onRenderTick(final TickEvent.RenderTickEvent event) {
        if (event.type == TickEvent.Type.RENDER && event.phase == TickEvent.Phase.START) {
            partialTick = event.renderTickTime;
        }
    }

    public float getPartialTick() {
        return partialTick;
    }
}
