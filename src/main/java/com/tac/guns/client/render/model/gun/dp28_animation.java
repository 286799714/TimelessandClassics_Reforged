package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.Dp28AnimationController;
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
public class dp28_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        Dp28AnimationController controller = Dp28AnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), Dp28AnimationController.INDEX_BODY, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
            //Always push
            matrices.pushPose();
            if (transformType.firstPerson()) {
                //We're getting the cooldown tracker for the item - items like the sword, ender pearl, and chorus fruit all have this too.
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                if (Gun.hasAmmo(stack)) {
                    // Math provided by Bomb787 on GitHub and Curseforge!!!
                    matrices.translate(0, 0, 0.198f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                } else if (!Gun.hasAmmo(stack)) {
                    matrices.translate(0, 0, 0.198f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);

            //Always pop
            matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), Dp28AnimationController.INDEX_MAGAZINE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, MAG), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}