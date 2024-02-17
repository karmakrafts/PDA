package io.karma.pda.common.item;

import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.common.init.ModItems;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.util.PlayerUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
public final class PDAItem extends Item {
    public static final String TAG_IS_ON = "is_on";

    public PDAItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level world, final @NotNull Player player,
                                                           final @NotNull InteractionHand hand) {
        final var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            PlayerUtils.openMenu(player, hand, PDAStorageMenu::new);
        }
        else if (world.isClientSide) {
            final var game = Minecraft.getInstance();
            if (game.options.getCameraType() != CameraType.FIRST_PERSON) { // Can only interact with it in first-person
                return InteractionResultHolder.fail(stack);
            }
            final var hands = EnumSet.of(hand);
            if (hand == InteractionHand.MAIN_HAND) {
                final var offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
                if (!offhandStack.isEmpty() && offhandStack.getItem() == ModItems.pda.get()) {
                    hands.add(InteractionHand.OFF_HAND); // Open off-hand at the same time
                }
            }
            game.setScreen(new PDAScreen(hands));
            // Play sound when engaging
            player.playSound(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.3F, 1.75F);
        }
        return InteractionResultHolder.sidedSuccess(stack, false); // Prevent builtin animation
    }
}
