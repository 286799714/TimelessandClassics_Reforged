package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.HK_G3AnimationController;
import com.tac.guns.client.render.animation.SCAR_MK20AnimationController;
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

public class hk_g3_animation implements IOverrideModel {

    @Override
    public void render(float partialTicks, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay)
    {
        HK_G3AnimationController controller = HK_G3AnimationController.getInstance();
        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.SCAR_MK20_BODY.getModel(), HK_G3AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(SpecialModels.HK_G3_RAIL.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.HK_G3_B_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                RenderUtil.renderLaserModuleModel(SpecialModels.HK_G3_B_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                    RenderUtil.renderModel(SpecialModels.HK_G3_LIGHT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                    RenderUtil.renderModel(SpecialModels.HK_G3_TAC_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
                RenderUtil.renderModel(SpecialModels.HK_G3_TACTICAL_HG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                    RenderUtil.renderModel(SpecialModels.HK_G3_LIGHT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
                    RenderUtil.renderModel(SpecialModels.HK_G3_TACTICAL_HG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                    RenderUtil.renderModel(SpecialModels.HK_G3_TAC_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
                    RenderUtil.renderModel(SpecialModels.HK_G3_TACTICAL_HG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.HK_G3_DEFAULT_HG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }

            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                matrices.push();
                matrices.translate(0, 0, -0.485f);
                RenderUtil.renderModel(SpecialModels.HK_G3_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.485f);
                matrices.pop();
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                matrices.translate(0, 0, -0.11f);
                RenderUtil.renderModel(SpecialModels.HK_G3_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.11f);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                matrices.translate(0, 0, -0.11f);
                RenderUtil.renderModel(SpecialModels.HK_G3_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.11f);
            } else {
                matrices.translate(0, 0, -0.11f);
                RenderUtil.renderModel(SpecialModels.HK_G3_DEFAULT_MUZZLE.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.11f);
            }

            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.HK_G3_LIGHT_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.HK_G3_TACTICAL_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.HK_G3_HEAVY_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.HK_G3_DEFAULT_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(SpecialModels.HK_G3_SIGHT_LIGHT.getModel(), stack, matrices, renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(SpecialModels.HK_G3_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.HK_G3_BODY.getModel(), HK_G3AnimationController.INDEX_MAGAZINE, transformType, matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.HK_G3_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.HK_G3_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        matrices.push();
        {
            if(transformType.isFirstPerson()) {
                controller.applySpecialModelTransform(SpecialModels.HK_G3_BODY.getModel(), HK_G3AnimationController.INDEX_BOLT, transformType, matrices);
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
                if (Gun.hasAmmo(stack) || shouldOffset) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    matrices.translate(0, 0, 0.175f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                } else if (!Gun.hasAmmo(stack)) {
                    {
                        matrices.translate(0, 0, 0.175f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                }
            }
            RenderUtil.renderModel(SpecialModels.HK_G3_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        controller.applySpecialModelTransform(SpecialModels.HK_G3_BODY.getModel(), HK_G3AnimationController.INDEX_PULL, transformType, matrices);
        RenderUtil.renderModel(SpecialModels.HK_G3_PULL.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.pop();

        matrices.push();
        controller.applySpecialModelTransform(SpecialModels.HK_G3_BODY.getModel(), HK_G3AnimationController.INDEX_HANDLE, transformType, matrices);
        RenderUtil.renderModel(SpecialModels.HK_G3_HANDLE.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.pop();

        matrices.push();
        controller.applySpecialModelTransform(SpecialModels.HK_G3_BODY.getModel(), HK_G3AnimationController.INDEX_BULLET, transformType, matrices);
        RenderUtil.renderModel(SpecialModels.HK_G3_BULLET.getModel(), stack, matrices, renderBuffer, light, overlay);
        matrices.pop();

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}