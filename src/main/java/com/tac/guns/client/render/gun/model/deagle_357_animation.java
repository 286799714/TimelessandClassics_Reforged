package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Deagle50AnimationController;
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
public class deagle_357_animation implements IOverrideModel {

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        
        Deagle50AnimationController controller = Deagle50AnimationController.getInstance();
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DEAGLE_50.getModel(),Deagle50AnimationController.INDEX_MAG,transformType,matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.DEAGLE_50_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.DEAGLE_50_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL)) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(SpecialModels.DEAGLE_50.getModel(), Deagle50AnimationController.EXTRA_MAG, transformType, matrices);
                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.DEAGLE_50_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.DEAGLE_50_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }
            matrices.popPose();
        }

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.DEAGLE_50.getModel(),Deagle50AnimationController.INDEX_BODY,transformType,matrices);
            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.DEAGLE_50_SILENCER.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.DEAGLE_50_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.DEAGLE_50_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(SpecialModels.DEAGLE_50.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();
        //Always push
        matrices.pushPose(); // push();
        controller.applySpecialModelTransform(SpecialModels.DEAGLE_50.getModel(),Deagle50AnimationController.INDEX_SLIDE,transformType,matrices);

        if(transformType.firstPerson()) {
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
            boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
            if (Gun.hasAmmo(stack) || shouldOffset) {
                // Math provided by Bomb787 on GitHub and Curseforge!!!
                matrices.translate(0, 0, 0.280f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
            } else if (!Gun.hasAmmo(stack)) {
            /*if(cooldownOg > 0.5){
                // Math provided by Bomb787 on GitHub and Curseforge!!!
                matrices.translate(0, 0, 0.280f * (-4.5 * Math.pow(cooldownOg-0.5, 2) + 1.0));
            }
            else*/
                {
                    matrices.translate(0, 0, 0.280f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }
            }
            matrices.translate(0.00, 0.0, 0.035);
        }
            RenderUtil.renderModel(SpecialModels.DEAGLE_50_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);

            //Always pop
            matrices.popPose();
        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }

     

    //TODO comments
}
