/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.item;

import io.karma.pda.api.common.util.DisplayType;
import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.util.PlayerUtils;
import io.karma.pda.common.util.TabItemProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class PDAItem extends Item implements TabItemProvider {
    public static final String TAG_HAS_CARD = "has_card";
    public static final String TAG_IS_ON = "is_on";
    public static final String TAG_DISPLAY_TYPE = "display_type";

    public PDAItem() {
        super(new Properties().stacksTo(1));
    }

    public static Optional<DisplayType> getDisplayType(final ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        final var tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_DISPLAY_TYPE)) {
            return Optional.empty();
        }
        return Optional.of(DisplayType.values()[tag.getInt(TAG_DISPLAY_TYPE)]);
    }

    public static void setDisplayType(final ItemStack stack, final DisplayType type) {
        if (stack.isEmpty()) {
            return;
        }
        stack.getOrCreateTag().putInt(TAG_DISPLAY_TYPE, type.ordinal());
    }

    @Override
    public void addToTab(final NonNullList<ItemStack> items) {
        for (final var type : DisplayType.values()) {
            final var stack = new ItemStack(this);
            setDisplayType(stack, type);
            items.add(stack);
        }
    }

    @Override
    public void inventoryTick(final @NotNull ItemStack stack, final @NotNull Level world, final @NotNull Entity entity,
                              final int slot, final boolean isSelected) {

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            PlayerUtils.openMenu(player, hand, PDAStorageMenu::new);
        }
        else {
            final var hands = EnumSet.of(hand);
            if (hand == InteractionHand.MAIN_HAND) {
                final var offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
                if (!offhandStack.isEmpty() && offhandStack.getItem() == ModItems.pda.get()) {
                    offhandStack.getOrCreateTag().putBoolean(TAG_IS_ON, true);
                    hands.add(InteractionHand.OFF_HAND); // Open off-hand at the same time
                }
            }
            stack.getOrCreateTag().putBoolean(TAG_IS_ON, true);
            if (world.isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(player, hands));
                // Play sound when engaging
                player.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.3F, 1.75F);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, false); // Prevent builtin animation
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(final Player player, final EnumSet<InteractionHand> hands) {
        Minecraft.getInstance().setScreen(new PDAScreen(player, hands));
    }
}
