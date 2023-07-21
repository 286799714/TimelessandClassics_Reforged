package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.Config;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.TtiG34AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.render.gun.ModelOverrides;
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
import net.minecraft.util.math.vector.Vector3f;
import com.tac.guns.util.GunModifierHelper;

/**
 * Author: Timeless Development, and associates.
 */
public class tti_g34_animation implements IOverrideModel {

    @Override
    public void render(float partialTicks, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay) {
        TtiG34AnimationController controller = TtiG34AnimationController.getInstance();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.TTI_G34.getModel(),TtiG34AnimationController.INDEX_BODY,transformType,matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.GLOCK_17_B_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, -0.25);
                RenderUtil.renderLaserModuleModel(SpecialModels.GLOCK_17_B_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                matrices.translate(0, 0, 0.25);
            }
            if (Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack).getItem() == ModItems.PISTOL_SILENCER.get()) {
                RenderUtil.renderModel(SpecialModels.TTI_G34_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(SpecialModels.TTI_G34.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        {
            controller.applySpecialModelTransform(SpecialModels.TTI_G34.getModel(),TtiG34AnimationController.INDEX_MAG,transformType,matrices);
            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.TTI_G34_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.TTI_G34_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        // reload norm mag
        if(transformType.isFirstPerson() && controller.isAnimationRunning()) {
            matrices.push();
            {

                controller.applySpecialModelTransform(SpecialModels.TTI_G34.getModel(), TtiG34AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.TTI_G34_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.TTI_G34_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }

            }
            matrices.pop();
        }

        //Always push
        matrices.push();
        if(transformType.isFirstPerson()) {
            controller.applySpecialModelTransform(SpecialModels.TTI_G34.getModel(), TtiG34AnimationController.INDEX_SLIDE, transformType, matrices);
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
            boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
            if (Gun.hasAmmo(stack) || shouldOffset) {
                matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                GunRenderingHandler.get().opticMovement = 0.185f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0);
            } else if (!Gun.hasAmmo(stack)) {
                matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                GunRenderingHandler.get().opticMovement = 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0);
            }
            matrices.translate(0, 0, 0.025F);
        }
        RenderUtil.renderModel(SpecialModels.TTI_G34_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);

        //Always pop
        matrices.pop();
        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
     

    //TODO comments
}
