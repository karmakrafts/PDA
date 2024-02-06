package io.karma.pda.common.item;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.util.NBTUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
public final class MemoryCardItem extends Item {
    public static final String TAG_IS_LOCKED = "is_locked";

    public MemoryCardItem() {
        super(new Properties());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ItemProperties.register(this,
                new ResourceLocation(PDAMod.MODID, TAG_IS_LOCKED),
                (stack, world, entity, i) -> NBTUtils.getOrDefault(stack.getTag(), TAG_IS_LOCKED, false) ? 1F : 0F);
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            final var tag = stack.getOrCreateTag();
            tag.putBoolean(TAG_IS_LOCKED, !tag.contains(TAG_IS_LOCKED) || !tag.getBoolean(TAG_IS_LOCKED));
        }
        else {
            world.playSound(player, player, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.75F, 2F);
        }
        return InteractionResultHolder.success(stack);
    }
}
