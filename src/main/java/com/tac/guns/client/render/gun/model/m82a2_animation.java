package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.M82A2AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class m82a2_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        M82A2AnimationController controller = M82A2AnimationController.getInstance();
        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

        matrices.pushPose();
        controller.applySpecialModelTransform(SpecialModels.M82A2_BODY.getModel(), M82A2AnimationController.INDEX_BODY, transformType, matrices);
        if (Gun.getScope(stack) == null) {
            RenderUtil.renderModel(SpecialModels.M82A2_SIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
        } else {
            RenderUtil.renderModel(SpecialModels.M82A2_SIGHT_FOLDED.getModel(), stack, matrices, renderBuffer, light, overlay);
        }

        RenderUtil.renderModel(SpecialModels.M82A2_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);

        //matrices.push();
        if(transformType.firstPerson()) {
            matrices.translate(0, 0, 0.375f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
            matrices.translate(0, 0, 0.025F);
        }

        matrices.translate(0, 0, -1.5);
        RenderUtil.renderModel(SpecialModels.M82A2_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.translate(0, 0, 1.5);
        matrices.popPose();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.M82A2_BODY.getModel(), M82A2AnimationController.INDEX_BOLT, transformType, matrices);
            if (transformType.firstPerson()) {
                matrices.translate(0, 0, 0.375f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                matrices.translate(0, 0, 0.025F);
            }
            RenderUtil.renderModel(SpecialModels.M82A2_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.M82A2_BODY.getModel(), M82A2AnimationController.INDEX_MAGAZINE, transformType, matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.M82A2_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.M82A2_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.M82A2_BODY.getModel(), M82A2AnimationController.INDEX_BULLET, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.M82A2_BULLET.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.M82A2_BODY.getModel(), M82A2AnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.M82A2_HANDLE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
