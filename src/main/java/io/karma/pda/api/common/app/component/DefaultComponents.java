/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app.component;

import io.karma.pda.api.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Alexander Hinze
 * @since 18/02/2024
 */
public final class DefaultComponents {
    private static final String REGISTRY_NAME = Constants.MODID + ":components";

    @ObjectHolder(value = Constants.MODID + ":container", registryName = REGISTRY_NAME)
    public static final ComponentType<DefaultContainer> CONTAINER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":label", registryName = REGISTRY_NAME)
    public static final ComponentType<Label> LABEL = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":button", registryName = REGISTRY_NAME)
    public static final ComponentType<Button> BUTTON = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":image", registryName = REGISTRY_NAME)
    public static final ComponentType<Image> IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":separator", registryName = REGISTRY_NAME)
    public static final ComponentType<Separator> SEPARATOR = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":item_render", registryName = REGISTRY_NAME)
    public static final ComponentType<ItemRender> ITEM_RENDER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":block_render", registryName = REGISTRY_NAME)
    public static final ComponentType<BlockRender> BLOCK_RENDER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":entity_render", registryName = REGISTRY_NAME)
    public static final ComponentType<EntityRender> ENTITY_RENDER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":recipe_render", registryName = REGISTRY_NAME)
    public static final ComponentType<RecipeRender> RECIPE_RENDER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":player_render", registryName = REGISTRY_NAME)
    public static final ComponentType<PlayerRender> PLAYER_RENDER = ComponentType.nullType();

    // @formatter:off
    private DefaultComponents() {}
    // @formatter:on
}
