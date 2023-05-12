package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.MAC10AnimationController;
import com.tac.guns.client.render.animation.MP9AnimationController;
import com.tac.guns.client.render.animation.UZIAnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class uzi_animation implements IOverrideModel {

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(float v, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay)
    {
        UZIAnimationController controller = UZIAnimationController.getInstance();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(SpecialModels.UZI_EXTENDED_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.UZI_FOLDED_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack).getItem() == ModItems.PISTOL_SILENCER.get()) {
                RenderUtil.renderModel(SpecialModels.UZI_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(SpecialModels.UZI.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_MAGAZINE, transformType, matrices);

            if(GunModifierHelper.getAmmoCapacity(stack) > -1)
            {
                RenderUtil.renderModel(SpecialModels.UZI_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            else
            {
                RenderUtil.renderModel(SpecialModels.UZI_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_BOLT, transformType, matrices);
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            if(transformType.isFirstPerson()) {
                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
                if(!shouldOffset && !Gun.hasAmmo(stack)){
                    matrices.translate(0, 0, -0.175f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }else {
                    matrices.translate(0, 0, -0.175f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                }
            }
            RenderUtil.renderModel(SpecialModels.UZI_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.UZI_HANDLE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_BULLET1, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.UZI_BULLET.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) && transformType.isFirstPerson()) {
            matrices.push();
            {
                controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_EXTENDED_MAGAZINE, transformType, matrices);

                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.UZI_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.UZI_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }
            matrices.pop();

            matrices.push();
            {
                controller.applySpecialModelTransform(SpecialModels.UZI.getModel(), UZIAnimationController.INDEX_BULLET2, transformType, matrices);
                RenderUtil.renderModel(SpecialModels.UZI_BULLET.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.pop();
        }

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }



    //TODO comments
}