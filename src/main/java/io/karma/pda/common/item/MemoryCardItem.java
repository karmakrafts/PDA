package io.karma.pda.common.item;

import io.karma.pda.common.PDAMod;
import io.karma.pda.common.util.NBTUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class MemoryCardItem extends Item {
    public static final String TAG_OWNER_ID = "owner_id";
    public static final String TAG_OWNER_NAME = "owner_name";

    public MemoryCardItem() {
        super(new Properties());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> ItemProperties.register(this,
                new ResourceLocation(PDAMod.MODID, "is_locked"),
                (stack, world, entity, i) -> NBTUtils.contains(stack.getTag(), TAG_OWNER_ID) ? 1F : 0F));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        final var tag = stack.getOrCreateTag();
        final var isLocked = !tag.contains(TAG_OWNER_ID);
        var didInteract = false;
        if (isLocked) {
            if (!world.isClientSide) {
                tag.putUUID(TAG_OWNER_ID, player.getUUID());
                tag.putString(TAG_OWNER_NAME, player.getName().getString());
            }
            didInteract = true;
        }
        else {
            if (!tag.getUUID(TAG_OWNER_ID).equals(player.getUUID())) {
                // If the owner ID doesn't match, cancel the interaction on both sides
                return InteractionResultHolder.fail(stack);
            }
            if (!world.isClientSide) {
                tag.remove(TAG_OWNER_ID);
                tag.remove(TAG_OWNER_NAME);
            }
            didInteract = true;
        }
        if (world.isClientSide && didInteract) {
            world.playSound(player, player, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.75F, 2F);
        }
        // @formatter:off
        return didInteract
            ? InteractionResultHolder.success(stack)
            : InteractionResultHolder.fail(stack);
        // @formatter:on
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack, final @Nullable Level world,
                                final @NotNull List<Component> components, final @NotNull TooltipFlag isAdvanced) {
        final var tag = stack.getTag();
        if (tag == null) {
            super.appendHoverText(stack, world, components, isAdvanced);
            return;
        }
        final var ownerName = tag.getString(TAG_OWNER_NAME);
        if (ownerName.isBlank()) {
            super.appendHoverText(stack, world, components, isAdvanced);
            return;
        }
        components.add(Component.translatable(String.format("tooltip.%s.owner", PDAMod.MODID), ownerName).withStyle(
            ChatFormatting.RED));
        super.appendHoverText(stack, world, components, isAdvanced);
    }
}
