package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.SCAR_LAnimationController;
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
public class scar_l_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        SCAR_LAnimationController controller = SCAR_LAnimationController.getInstance();
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.SCAR_L_BODY.getModel(), SCAR_LAnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_FSU.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SCAR_L_FS.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_LIGHT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);

            } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_TAC_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                // TODO: Add RenderUtil to render specifically laser and body in order to apply tint, also yk just replace this entire render...
                RenderUtil.renderLaserModuleModel(SpecialModels.SCAR_L_MINI_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                matrices.pushPose();
                // needs specific length
                //if(transformType.isFirstPerson()) {
                    matrices.translate(0, 0, 4.5);
                    matrices.scale(1,1,9);
                //}
                RenderUtil.renderLaserModuleModel(SpecialModels.SCAR_L_MINI_LASER_BEAM.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                matrices.popPose();
            }else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) || Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.SCAR_L_IR_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, light, overlay);
                matrices.pushPose();
                if(transformType.firstPerson()) {
                    // TODO: Build some sort of scaler for this
                    matrices.translate(0, 0, -0.35);
                    matrices.scale(1, 1, 9);
                    matrices.translate(0, 0, 0.35);
                    RenderUtil.renderLaserModuleModel(SpecialModels.SCAR_L_IR_LASER.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                }
                matrices.popPose();
            }
            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                matrices.pushPose();
                matrices.translate(0, 0, -0.0225f);
                RenderUtil.renderModel(SpecialModels.SCAR_L_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.0225f);
                matrices.popPose();
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else
                RenderUtil.renderModel(SpecialModels.SCAR_L_DEFAULT_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);

            RenderUtil.renderModel(SpecialModels.SCAR_L_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.SCAR_L_BODY.getModel(), SCAR_LAnimationController.INDEX_MAGAZINE, transformType, matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.SCAR_L_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SCAR_L_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            if(transformType.firstPerson() && controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL)) {
                controller.applySpecialModelTransform(SpecialModels.SCAR_L_BODY.getModel(), SCAR_LAnimationController.INDEX_MAGAZINE2, transformType, matrices);
                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.SCAR_L_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.SCAR_L_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }
        }
        matrices.popPose();

        matrices.pushPose();
        {
            if(transformType.firstPerson()) {
                controller.applySpecialModelTransform(SpecialModels.SCAR_L_BODY.getModel(), SCAR_LAnimationController.INDEX_BOLT, transformType, matrices);
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());


                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
                if (Gun.hasAmmo(stack) || shouldOffset) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    matrices.translate(0, 0, 0.225f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                } else if (!Gun.hasAmmo(stack)) {
                    {
                        matrices.translate(0, 0, 0.225f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                }
                matrices.translate(0, 0, 0.025F);
            }
            RenderUtil.renderModel(SpecialModels.SCAR_L_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();
        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
