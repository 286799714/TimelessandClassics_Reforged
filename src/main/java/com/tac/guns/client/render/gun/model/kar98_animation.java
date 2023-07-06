package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.HK_G3AnimationController;
import com.tac.guns.client.render.animation.KAR98AnimationController;
import com.tac.guns.client.render.animation.M1014AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class kar98_animation implements IOverrideModel {
    @Override
    public void render(float v, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay) {

        KAR98AnimationController controller = KAR98AnimationController.getInstance();
        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(SpecialModels.KAR98_MOUNT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            matrices.push();
            matrices.translate(0, 0, -0.375);
            RenderUtil.renderModel(SpecialModels.KAR98_FRONT.getModel(), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(0, 0, 0.375);
            matrices.pop();

            RenderUtil.renderModel(SpecialModels.KAR98_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BOLT_FIX, transformType, matrices);
        RenderUtil.renderModel(SpecialModels.KAR98_BOLT_FIXED.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.pop();

        matrices.push();
        controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BOLT_ROTATE, transformType, matrices);
        RenderUtil.renderModel(SpecialModels.KAR98_BOLT_ROTATE.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_CLIP, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY))
                RenderUtil.renderModel(SpecialModels.KAR98_CLIP.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BULLETS, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY))
                RenderUtil.renderModel(SpecialModels.KAR98_BULLETS.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BULLET3, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_EMPTY) || controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_LOOP))
                RenderUtil.renderModel(SpecialModels.KAR98_BULLET3.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.KAR98_BODY.getModel(), KAR98AnimationController.INDEX_BULLET2, transformType, matrices);
            if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.PULL_BOLT)) {
                RenderUtil.renderModel(SpecialModels.KAR98_SHELL.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.KAR98_BULLET2.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}