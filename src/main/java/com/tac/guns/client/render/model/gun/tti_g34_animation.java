package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.Config;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.TtiG34AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

/**
 * Author: Timeless Development, and associates.
 */
public class tti_g34_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        TtiG34AnimationController controller = TtiG34AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), TtiG34AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC_DEVICE), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                if (transformType.firstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get()) {
                    matrices.translate(0, 0, -0.25);
                    RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                    matrices.translate(0, 0, 0.25);
                }
            }
            if (Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack).getItem() == ModItems.PISTOL_SILENCER.get()) {
                RenderUtil.renderModel(getComponentModel(skin, MUZZLE_SILENCER), stack, matrices, renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), TtiG34AnimationController.INDEX_MAG, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        // reload norm mag
        if (transformType.firstPerson() && controller.isAnimationRunning()) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), TtiG34AnimationController.INDEX_EXTRA_MAG, transformType, matrices);
                renderMag(stack, matrices, renderBuffer, light, overlay, skin);
            }
            matrices.popPose();
        }

        //Always push
        matrices.pushPose();
        if (transformType.firstPerson()) {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), TtiG34AnimationController.INDEX_SLIDE, transformType, matrices);
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
            boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
            if (Gun.hasAmmo(stack) || shouldOffset) {
                double v = -4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0;
                matrices.translate(0, 0, 0.185f * v);
                GunRenderingHandler.get().opticMovement = 0.185f * v;
            } else if (!Gun.hasAmmo(stack)) {
                double z = 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0);
                matrices.translate(0, 0, z);
                GunRenderingHandler.get().opticMovement = z;
            }
            matrices.translate(0, 0, 0.025F);
        }
        RenderUtil.renderModel(getComponentModel(skin, SLIDE), stack, matrices, renderBuffer, light, overlay);
        RenderUtil.renderModel(getComponentModel(skin, SLIDE_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);

        //Always pop
        matrices.popPose();
        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }


    //TODO comments
}