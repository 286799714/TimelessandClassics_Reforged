package com.tac.guns.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.tac.guns.api.item.IAmmo;
import com.tac.guns.client.model.SlotModel;
import com.tac.guns.client.resource.ClientGunPackLoader;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AmmoItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final SlotModel SLOT_AMMO_MODEL = new SlotModel();

    public AmmoItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType transformType, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (stack.getItem() instanceof IAmmo iAmmo) {
            ResourceLocation ammoId = iAmmo.getAmmoId(stack);
            // TODO 如果没有这个 ammoID，应该渲染个什么错误材质提醒别人
            ClientGunPackLoader.getAmmoIndex(ammoId).ifPresent(ammoIndex -> {
                poseStack.pushPose();
                poseStack.translate(0.5, 1.5, 0.5);
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(180));
                VertexConsumer buffer = pBuffer.getBuffer(ammoIndex.getSlotRenderType());
                SLOT_AMMO_MODEL.renderToBuffer(poseStack, buffer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            });
        }
    }
}
