package io.karma.pda.item;

import io.karma.pda.PDAMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            ItemProperties.register(this, new ResourceLocation(PDAMod.MODID, TAG_IS_ON), (stack, world, entity, i) -> {
                final var tag = stack.getTag();
                if (tag == null) {
                    return 0F;
                }
                return tag.contains(TAG_IS_ON) && tag.getBoolean(TAG_IS_ON) ? 1F : 0F;
            });
            ItemProperties.register(this,
                new ResourceLocation(PDAMod.MODID, TAG_IS_HORIZONTAL),
                (stack, world, entity, i) -> {
                    final var tag = stack.getTag();
                    if (tag == null) {
                        return 0F;
                    }
                    return tag.contains(TAG_IS_HORIZONTAL) && tag.getBoolean(TAG_IS_HORIZONTAL) ? 1F : 0F;
                });
            return null;
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                final var tag = stack.getOrCreateTag();
                tag.putBoolean(TAG_IS_HORIZONTAL,
                    !tag.contains(TAG_IS_HORIZONTAL) || !tag.getBoolean(TAG_IS_HORIZONTAL));
            }
            else {
                // open menu..
            }
        }
        return InteractionResultHolder.success(stack);
    }
}
