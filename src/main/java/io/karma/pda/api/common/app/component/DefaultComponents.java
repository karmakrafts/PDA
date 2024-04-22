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
    @ObjectHolder(value = Constants.MODID + ":item_image", registryName = REGISTRY_NAME)
    public static final ComponentType<ItemImage> ITEM_IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":block_image", registryName = REGISTRY_NAME)
    public static final ComponentType<BlockImage> BLOCK_IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":entity_image", registryName = REGISTRY_NAME)
    public static final ComponentType<EntityImage> ENTITY_IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":recipe_image", registryName = REGISTRY_NAME)
    public static final ComponentType<RecipeImage> RECIPE_IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":player_image", registryName = REGISTRY_NAME)
    public static final ComponentType<PlayerImage> PLAYER_IMAGE = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":spinner", registryName = REGISTRY_NAME)
    public static final ComponentType<Spinner> SPINNER = ComponentType.nullType();
    @ObjectHolder(value = Constants.MODID + ":panel", registryName = REGISTRY_NAME)
    public static final ComponentType<Panel> PANEL = ComponentType.nullType();

    // @formatter:off
    private DefaultComponents() {}
    // @formatter:on
}
