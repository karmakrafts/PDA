package io.karma.pda.item;

import io.karma.pda.PDAMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

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
}
