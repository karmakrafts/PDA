package io.karma.pda.common.item;

import io.karma.pda.client.screen.PDAScreen;
import io.karma.pda.common.menu.PDAStorageMenu;
import io.karma.pda.common.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
            Minecraft.getInstance().setScreen(new PDAScreen());
        }
        return InteractionResultHolder.pass(stack);
    }
}
