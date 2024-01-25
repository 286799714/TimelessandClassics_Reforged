package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.resource.model.bedrock.BedrockVersion;
import com.tac.guns.client.resource.model.bedrock.pojo.BedrockModelPOJO;
import com.tac.guns.client.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class BedrockGunModel extends BedrockAnimatedModel implements IOverrideModel {
    public BedrockGunModel(BedrockModelPOJO pojo, BedrockVersion version, RenderType renderType) {
        super(pojo, version, renderType);
    }

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        render(partialTicks, transformType, matrixStack, buffer, light, overlay);
    }
}
