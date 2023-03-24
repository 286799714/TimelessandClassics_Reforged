package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Mp7AnimationController;
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
public class mp7_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        
        Mp7AnimationController controller = Mp7AnimationController.getInstance();
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.MP7.getModel(),Mp7AnimationController.INDEX_BODY,transformType,matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.MP7_BASIC_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                matrices.pushPose();
                matrices.translate(0, 0, -0.645);
                matrices.scale(1, 1, 11);
                matrices.translate(0, 0, 0.645);
                RenderUtil.renderLaserModuleModel(SpecialModels.MP7_BASIC_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                matrices.popPose();
            }
            else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) || Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.MP7_IR_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, light, overlay);
                matrices.pushPose();
                if(transformType.firstPerson()) {
                    // TODO: Build some sort of scaler for this
                    matrices.translate(0, 0, -0.625);
                    matrices.scale(1, 1, 9);
                    matrices.translate(0, 0, 0.625);
                    RenderUtil.renderLaserModuleModel(SpecialModels.MP7_IR_LASER.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                }
                matrices.popPose();
            }
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(SpecialModels.MP7_SIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MP7_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MP7_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MP7_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(SpecialModels.MP7.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.MP7.getModel(),Mp7AnimationController.INDEX_MAG,transformType,matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.MP7_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.MP7_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        matrices.pushPose();
        if(transformType.firstPerson()) {
            controller.applySpecialModelTransform(SpecialModels.MP7.getModel(), Mp7AnimationController.INDEX_BODY, transformType, matrices);
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            if (Gun.hasAmmo(stack)) {
                // Math provided by Bomb787 on GitHub and Curseforge!!!
                matrices.translate(0, 0, 0.085f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
            } else if (!Gun.hasAmmo(stack)) {
                {
                    matrices.translate(0, 0, 0.085f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }
            }
        }
        matrices.translate(0, 0, 0.025F);
        RenderUtil.renderModel(SpecialModels.MP7_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
