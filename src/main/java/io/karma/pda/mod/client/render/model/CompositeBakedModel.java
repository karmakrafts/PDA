/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.mod.client.util.BakedQuadUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Alexander Hinze
 * @since 12/08/2024
 */
@OnlyIn(Dist.CLIENT)
public final class CompositeBakedModel implements BakedModel {
    private final List<Pair<BakedModel, BiFunction<BakedQuad, Direction, BakedQuad>>> models;
    private final BakedModel delegate;

    public CompositeBakedModel(final List<Pair<BakedModel, BiFunction<BakedQuad, Direction, BakedQuad>>> models) {
        this.models = models;
        delegate = models.get(0).getLeft();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(final @Nullable BlockState state, final @Nullable Direction side,
                                             final @NotNull RandomSource rand) {
        final var quads = new ArrayList<BakedQuad>();
        for (final var model : models) {
            // @formatter:off
            BakedQuadUtils.transformQuads(model.getLeft().getQuads(state, side, rand), quads,
                q -> model.getRight().apply(q, q.getDirection()));
            // @formatter:on
        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return delegate.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return delegate.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return delegate.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return delegate.isCustomRenderer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return delegate.getParticleIcon();
    }

    @Override
    public boolean useAmbientOcclusion(final @NotNull BlockState state) {
        return delegate.useAmbientOcclusion(state);
    }

    @Override
    public boolean useAmbientOcclusion(final @NotNull BlockState state, final @NotNull RenderType renderType) {
        return delegate.useAmbientOcclusion(state, renderType);
    }

    @Override
    public @NotNull BakedModel applyTransform(final @NotNull ItemDisplayContext transformType,
                                              final @NotNull PoseStack poseStack,
                                              final boolean applyLeftHandTransform) {
        return delegate.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public @NotNull ModelData getModelData(final @NotNull BlockAndTintGetter level, final @NotNull BlockPos pos,
                                           final @NotNull BlockState state, final @NotNull ModelData modelData) {
        return delegate.getModelData(level, pos, state, modelData);
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(final @NotNull ModelData data) {
        return delegate.getParticleIcon(data);
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(final @NotNull BlockState state, final @NotNull RandomSource rand,
                                                      final @NotNull ModelData data) {
        return delegate.getRenderTypes(state, rand, data);
    }

    @Override
    public @NotNull List<RenderType> getRenderTypes(final @NotNull ItemStack stack, final boolean fabulous) {
        return delegate.getRenderTypes(stack, fabulous);
    }

    @Override
    public @NotNull List<BakedModel> getRenderPasses(final @NotNull ItemStack stack, final boolean fabulous) {
        return delegate.getRenderPasses(stack, fabulous);
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return delegate.getOverrides();
    }
}
