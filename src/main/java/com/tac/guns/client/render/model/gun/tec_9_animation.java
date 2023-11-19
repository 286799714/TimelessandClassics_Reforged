package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.TEC9AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class tec_9_animation extends SkinnedGunModel {
    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        TEC9AnimationController controller = TEC9AnimationController.getInstance();


        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), TEC9AnimationController.INDEX_BODY, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), TEC9AnimationController.INDEX_MAG, transformType, matrices);
        renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        matrices.popPose();

        //Always push
        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), TEC9AnimationController.INDEX_BOLT, transformType, matrices);
        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());
        if (transformType.firstPerson()) {
            AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
            boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();
            if (!shouldOffset && !Gun.hasAmmo(stack)) {
                matrices.translate(0, 0, -0.375);
            } else {
                matrices.translate(0, 0, -0.375 + Math.pow(cooldownOg - 0.5, 2));
            }
        }
        RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        //Always pop
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), TEC9AnimationController.INDEX_BULLET, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
