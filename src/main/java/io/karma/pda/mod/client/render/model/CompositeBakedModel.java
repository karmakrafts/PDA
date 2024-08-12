/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.pda.mod.client.util.BakedQuadUtils;
import net.minecraft.client.renderer.LightTexture;
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
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.sampling.BestCandidateSampling.Quad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alexander Hinze
 * @since 12/08/2024
 */
@OnlyIn(Dist.CLIENT)
public class CompositeBakedModel implements BakedModel {
    private final BakedModel delegate;
    private final List<BakedModel> fullBrightModels;
    private final Direction orientation;
    private final ConcurrentHashMap<Direction, ArrayList<BakedQuad>> sideQuads = new ConcurrentHashMap<>();
    private final AtomicReference<List<BakedQuad>> quads = new AtomicReference<>();

    public CompositeBakedModel(final BakedModel delegate, final List<BakedModel> fullBrightModels,
                               final Direction orientation) {
        this.delegate = delegate;
        this.fullBrightModels = fullBrightModels;
        this.orientation = orientation;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(final @Nullable BlockState state, final @Nullable Direction side,
                                             final @NotNull RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(final @Nullable BlockState state, final @Nullable Direction side,
                                             final @NotNull RandomSource rand, final @NotNull ModelData data,
                                             final @Nullable RenderType renderType) {
        // Handle side-less quads
        if (side == null) {
            if (quads.compareAndSet(null, new ArrayList<>())) {
                final var quads = this.quads.get();
                for (final var model : fullBrightModels) {
                    // @formatter:off
                    BakedQuadUtils.transformQuads(model.getQuads(state, null, rand, data, renderType), quads,
                        QuadTransformers.applyingLightmap(LightTexture.FULL_BRIGHT)
                            .andThen(BakedQuadUtils.applyRotation(orientation))::process);
                    // @formatter:on
                }
                quads.addAll(delegate.getQuads(state, null, rand, data, renderType));
            }
            return quads.get();
        }
        // Handle sided quads
        var sideQuads = this.sideQuads.get(side);
        if (sideQuads == null) {
            sideQuads = new ArrayList<>();
            for (final var model : fullBrightModels) {
                // @formatter:off
                BakedQuadUtils.transformQuads(model.getQuads(state, side, rand, data, renderType), sideQuads,
                    QuadTransformers.applyingLightmap(LightTexture.FULL_BRIGHT)
                        .andThen(BakedQuadUtils.applyRotation(orientation))::process);
                // @formatter:on
            }
            sideQuads.addAll(delegate.getQuads(state, side, rand, data, renderType));
            this.sideQuads.put(side, sideQuads);
        }
        return sideQuads;
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
