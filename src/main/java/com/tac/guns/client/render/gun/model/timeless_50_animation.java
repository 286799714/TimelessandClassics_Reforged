package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Timeless50AnimationController;
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

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class timeless_50_animation implements IOverrideModel {

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(float partialTicks, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay)
    {
        Timeless50AnimationController controller = Timeless50AnimationController.getInstance();

        boolean renderClumsy = Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.get() ||
                Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.get();
        double yAdjust = -0.0795; //Neko accidently made the .50 slightly too high in model space compared to the STI

        matrices.push();
        {
            matrices.translate(0,yAdjust,0);
            /*matrices.translate(0, 0, -0.105);
                RenderUtil.renderModel(SpecialModels.STI2011_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.105);*/
            controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(),Timeless50AnimationController.INDEX_BODY,transformType,matrices);
            if (renderClumsy) {
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_E_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_CLUMSYYY.getModel(), stack, matrices, renderBuffer, 15728880, overlay);
            }
            else {
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_S_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_NEKOOO.getModel(), stack, matrices, renderBuffer, 15728880, overlay);
            }
            RenderUtil.renderModel(SpecialModels.TIMELESS_50.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            matrices.translate(0,yAdjust,0);
            if(transformType.isFirstPerson()) {
                controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_SLIDE, transformType, matrices);
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 :
                        ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());
                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();


                //matrices.translate(0.00, 0.0, 0.035); // Issues with the slide starting out further forward, seems to be ~ a 0.035 movement
                if (Gun.hasAmmo(stack) || shouldOffset) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    matrices.translate(0, 0, 0.235f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    GunRenderingHandler.get().opticMovement = 0.235f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0);
                } else if (!Gun.hasAmmo(stack)) {
                    {
                        matrices.translate(0, 0, 0.235f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                }
                matrices.translate(0, 0, 0.025F);
            }
            if(renderClumsy)
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_E_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);
            else
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_S_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            matrices.translate(0,yAdjust,0);
            controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_HAMMER, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.TIMELESS_50_HAMMER.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            matrices.translate(0,yAdjust,0);
            controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_MAG, transformType, matrices);
            if(GunModifierHelper.getAmmoCapacity(stack) > -1)
            {
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_E_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            else
            {
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_S_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation())) {
            matrices.push();
            {
                matrices.translate(0,yAdjust,0);
                controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                matrices.translate(0, -0.1, 2.2);
                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.TIMELESS_50_E_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.TIMELESS_50_S_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.pop();
        }

        if(!controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY).equals(controller.getPreviousAnimation())) {
            matrices.push();
            {
                matrices.translate(0, yAdjust, 0);
                controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_BULLET1, transformType, matrices);
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_BULLET1.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.pop();
        }

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) && !controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY).equals(controller.getPreviousAnimation())) {
            matrices.push();
            {
                matrices.translate(0,yAdjust,0);
                controller.applySpecialModelTransform(SpecialModels.TIMELESS_50.getModel(), Timeless50AnimationController.INDEX_BULLET2, transformType, matrices);
                matrices.translate(0, -0.1, 2.2);
                RenderUtil.renderModel(SpecialModels.TIMELESS_50_BULLET2.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.pop();
        }

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
