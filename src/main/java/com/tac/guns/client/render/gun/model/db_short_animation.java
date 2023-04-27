package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.render.animation.DBShotgunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class db_short_animation implements IOverrideModel {
    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        
        DBShotgunAnimationController controller = DBShotgunAnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DB_SHORT_REAR.getModel(), DBShotgunAnimationController.INDEX_REAR,transformType,matrices);
            RenderUtil.renderModel(SpecialModels.DB_SHORT_REAR.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DB_SHORT_REAR.getModel(), DBShotgunAnimationController.INDEX_FRONT,transformType,matrices);
            RenderUtil.renderModel(SpecialModels.DB_SHORT_FRONT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DB_SHORT_REAR.getModel(), DBShotgunAnimationController.INDEX_LEVER,transformType,matrices);
            RenderUtil.renderModel(SpecialModels.DB_SHORT_LEVER.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DB_SHORT_REAR.getModel(), DBShotgunAnimationController.INDEX_BULLET1,transformType,matrices);
            RenderUtil.renderModel(SpecialModels.DB_SHORT_BULLET1.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DB_SHORT_REAR.getModel(), DBShotgunAnimationController.INDEX_BULLET2,transformType,matrices);
            RenderUtil.renderModel(SpecialModels.DB_SHORT_BULLET2.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
