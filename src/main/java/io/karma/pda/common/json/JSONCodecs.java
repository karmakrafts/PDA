/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.color.GradientFunction;
import io.karma.pda.api.common.util.Constants;
import io.karma.pda.common.PDAMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class JSONCodecs {
    // @formatter:off
    private JSONCodecs() {}
    // @formatter:on

    @SuppressWarnings("all")
    public static void register() {
        PDAMod.LOGGER.debug("Registering JSON codecs");
        final var module = new SimpleModule(Constants.MODID);

        module.addSerializer(GradientFunction.class,
            new RegistrySerializer<>(GradientFunction.class, API::getGradientFunctionRegistry));
        module.addSerializer(new RegistrySerializer<>(Item.class, () -> ForgeRegistries.ITEMS));
        module.addSerializer(new RegistrySerializer<>(Block.class, () -> ForgeRegistries.BLOCKS));
        module.addSerializer(new RegistrySerializer<EntityType<?>>(EntityType.class,
            () -> ForgeRegistries.ENTITY_TYPES));
        module.addSerializer(new RegistrySerializer<BlockEntityType<?>>(BlockEntityType.class,
            () -> ForgeRegistries.BLOCK_ENTITY_TYPES));
        module.addSerializer(new ResourceLocationSerializer());
        module.addSerializer(new ItemStackSerializer());

        module.addDeserializer(GradientFunction.class,
            new RegistryDeserializer<>(GradientFunction.class, API::getGradientFunctionRegistry));
        module.addDeserializer(Item.class, new RegistryDeserializer<>(Item.class, () -> ForgeRegistries.ITEMS));
        module.addDeserializer(Block.class, new RegistryDeserializer<>(Block.class, () -> ForgeRegistries.BLOCKS));
        module.addDeserializer(EntityType.class,
            new RegistryDeserializer<>(EntityType.class, () -> ForgeRegistries.ENTITY_TYPES));
        module.addDeserializer(BlockEntityType.class,
            new RegistryDeserializer<>(BlockEntityType.class, () -> ForgeRegistries.BLOCK_ENTITY_TYPES));
        module.addDeserializer(ResourceLocation.class, new ResourceLocationDeserializer());
        module.addDeserializer(ItemStack.class, new ItemStackDeserializer());

        API.getObjectMapper().registerModule(module);
    }
}
