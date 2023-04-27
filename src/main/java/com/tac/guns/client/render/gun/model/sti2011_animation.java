package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Ak47AnimationController;
import com.tac.guns.client.render.animation.DBShotgunAnimationController;
import com.tac.guns.client.render.animation.STI2011AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import com.tac.guns.util.GunModifierHelper;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class sti2011_animation implements IOverrideModel {

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(float v, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay) {
        STI2011AnimationController controller = STI2011AnimationController.getInstance();
        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(),STI2011AnimationController.INDEX_BODY,transformType,matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.STI2011_BASIC_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                matrices.push();
                matrices.translate(0, 0, -0.35);
                matrices.scale(1, 1, 9);
                matrices.translate(0, 0, 0.35);
                RenderUtil.renderLaserModuleModel(SpecialModels.STI2011_BASIC_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                matrices.pop();
            }
            if (Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack).getItem() == ModItems.PISTOL_SILENCER.get()) {

                matrices.push();
                {
                    matrices.translate(0, 0, -0.225+0.125);
                    RenderUtil.renderModel(SpecialModels.STI2011_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
                matrices.pop();
            }

            RenderUtil.renderModel(SpecialModels.STI2011_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            if(transformType.isFirstPerson()) {
                controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_SLIDE, transformType, matrices);
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
            RenderUtil.renderModel(SpecialModels.STI2011_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_HAMMER, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.STI2011_HAMMER.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_MAG, transformType, matrices);
            if(GunModifierHelper.getAmmoCapacity(stack) > -1)
            {
                RenderUtil.renderModel(SpecialModels.STI2011_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            else
            {
                RenderUtil.renderModel(SpecialModels.STI2011_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation())) {
            matrices.push();
            {
                controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                matrices.translate(0, -0.1, 2.2);
                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.STI2011_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.STI2011_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.pop();
        }

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_BULLET1, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.STI2011_BULLET1.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) ) {
            matrices.push();
            {
                controller.applySpecialModelTransform(SpecialModels.STI2011_BODY.getModel(), STI2011AnimationController.INDEX_BULLET2, transformType, matrices);
                matrices.translate(0, -0.1, 2.2);
                RenderUtil.renderModel(SpecialModels.STI2011_BULLET2.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0.1, -2.2);
            }
            matrices.pop();
        }

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
