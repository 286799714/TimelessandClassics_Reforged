package com.tac.guns.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tac.guns.client.model.bedrock.BedrockModel;
import com.tac.guns.client.renderer.block.GunSmithTableRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class GunSmithTableItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GunSmithTableItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType transformType, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BedrockModel model = GunSmithTableRenderer.getModel();
        if (model != null) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Vector3f.ZN.rotationDegrees(180));
            model.render(ItemTransforms.TransformType.NONE, poseStack, pBuffer, pPackedLight, pPackedOverlay);
            poseStack.popPose();
        }
    }
}
