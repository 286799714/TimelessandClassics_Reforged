package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public interface IModel {
    void render(float partialTicks, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay);
}
