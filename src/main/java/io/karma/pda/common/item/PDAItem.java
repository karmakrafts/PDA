/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.item;

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.DefaultApps;
import io.karma.pda.api.common.display.DisplayModeSpec;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.client.session.ClientSessionHandler;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.session.HandheldSessionContext;
import io.karma.pda.common.util.PlayerUtils;
import io.karma.pda.common.util.TabItemProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class PDAItem extends Item implements TabItemProvider {
    public static final String TAG_HAS_CARD = "has_card";
    public static final String TAG_IS_ON = "is_on";
    public static final String TAG_DISPLAY_MODE = "display_mode";
    /*
     * This flag tracks the immediate state of the UI to prevent double openScreen invocations
     * caused by vanilla invoking use twice, once for each hand.
     * << If you touch this flag, things will most likely break in horrible ways. >>
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isScreenOpen; // TODO: Move to PDAInteractionHandler

    public PDAItem() {
        super(new Properties().stacksTo(1));
    }

    public static void setDisplayMode(final ItemStack stack, final DisplayModeSpec spec) {
        if (stack.isEmpty()) {
            return;
        }
        stack.getOrCreateTag().putString(TAG_DISPLAY_MODE, spec.name());
    }

    public static Optional<DisplayModeSpec> getDisplayMode(final ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        final var tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_DISPLAY_MODE)) {
            return Optional.empty();
        }
        final var name = ResourceLocation.tryParse(tag.getString(TAG_DISPLAY_MODE));
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(API.getDisplayModeRegistry().getValue(name));
    }

    @Override
    public void addToTab(final NonNullList<ItemStack> items) {
        for (final var mode : API.getDisplayModes()) {
            final var stack = new ItemStack(this);
            setDisplayMode(stack, mode);
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack, @Nullable Level world,
                                final @NotNull List<Component> components, final @NotNull TooltipFlag isAdvanced) {
        final var tag = stack.getTag();
        if (tag == null) {
            super.appendHoverText(stack, world, components, isAdvanced);
            return;
        }
        final var mode = getDisplayMode(stack).orElse(null);
        if (mode == null) {
            super.appendHoverText(stack, world, components, isAdvanced);
            return;
        }
        components.add(Component.translatable(String.format("tooltip.%s.resolution", Constants.MODID),
            mode.resolution().getResolutionString()));
        components.add(Component.translatable(String.format("tooltip.%s.display_type", Constants.MODID),
            mode.type().getTranslatedName()));
        super.appendHoverText(stack, world, components, isAdvanced);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            PlayerUtils.openMenu(player, hand, PDAStorageMenu::new);
            return InteractionResultHolder.success(stack);
        }
        final var hands = EnumSet.of(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            final var offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!offhandStack.isEmpty() && offhandStack.getItem() == ModItems.pda.get()) {
                offhandStack.getOrCreateTag().putBoolean(TAG_IS_ON, true);
                hands.add(InteractionHand.OFF_HAND); // Open off-hand at the same time
            }
        }
        stack.getOrCreateTag().putBoolean(TAG_IS_ON, true);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> openScreen(player, hands.size() == 1 ? hand : InteractionHand.MAIN_HAND, hands));
        return InteractionResultHolder.pass(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(final Player player, final InteractionHand defaultHand,
                            final EnumSet<InteractionHand> hands) {
        final var game = Minecraft.getInstance();
        if (isScreenOpen || game.player != player || !game.options.getCameraType().isFirstPerson()) {
            return;
        }
        final var sessionHandler = ClientSessionHandler.INSTANCE;
        // @formatter:off
        sessionHandler.createSession(hands.stream()
            .map(hand -> new HandheldSessionContext(player, hand))
            .toList(), defaultHand).thenAccept(session -> {
                for(final var hand : hands) { // Launch app for both hands
                    session.setSelector(hand);
                    session.getLauncher().openApp(DefaultApps.LAUNCHER).join();
                }
                sessionHandler.setActiveSession(session);
                game.execute(() -> {
                    game.setScreen(new PDAScreen(hands,
                        sessionHandler.getActiveSession()));
                    player.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.3F, 1.75F);
                });
            });
        // @formatter:on
        isScreenOpen = true;
    }
}
