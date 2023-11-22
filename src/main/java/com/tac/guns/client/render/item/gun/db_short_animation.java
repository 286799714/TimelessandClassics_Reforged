package com.tac.guns.client.render.item.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.resource.gunskin.GunSkin;
import com.tac.guns.client.animation.DBShotgunAnimationController;
import com.tac.guns.client.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.item.SkinnedGunModel;
import com.tac.guns.client.resource.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.resource.gunskin.CommonComponents.*;

public class db_short_animation extends SkinnedGunModel {
    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        DBShotgunAnimationController controller = DBShotgunAnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), DBShotgunAnimationController.INDEX_REAR, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), DBShotgunAnimationController.INDEX_FRONT, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), DBShotgunAnimationController.INDEX_LEVER, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.HAMMER), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), DBShotgunAnimationController.INDEX_BULLET1, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET1), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), DBShotgunAnimationController.INDEX_BULLET2, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET2), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}