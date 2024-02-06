package io.karma.pda.common.item;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.init.ModMenus;
import io.karma.pda.common.util.NBTUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class PDAItem extends Item {
    public static final String TAG_IS_ON = "is_on";
    public static final String TAG_IS_HORIZONTAL = "is_horizontal";

    public PDAItem() {
        super(new Properties());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ItemProperties.register(this,
                new ResourceLocation(PDAMod.MODID, TAG_IS_ON),
                (stack, world, entity, i) -> NBTUtils.getOrDefault(stack.getTag(), TAG_IS_ON, false) ? 1F : 0F);
            ItemProperties.register(this,
                new ResourceLocation(PDAMod.MODID, TAG_IS_HORIZONTAL),
                (stack, world, entity, i) -> NBTUtils.getOrDefault(stack.getTag(), TAG_IS_HORIZONTAL, false) ? 1F : 0F);
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                NetworkHooks.openScreen((ServerPlayer) player,
                    new SimpleMenuProvider((id, inventory, p) -> ModMenus.pdaItemMenu.get().create(id, inventory),
                        Component.empty()));
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
