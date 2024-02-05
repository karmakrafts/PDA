package io.karma.pda;

import io.karma.pda.init.ModBlocks;
import io.karma.pda.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Alexander Hinze
 * @since 05/02/2024
 */
@Mod(PDAMod.MODID)
public class PDAMod {
    public static final String MODID = "pda";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    static {
        ModBlocks.register(BLOCKS);
        ModItems.register(ITEMS);
    }
}
