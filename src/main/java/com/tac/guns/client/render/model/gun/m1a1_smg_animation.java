package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.gunskin.SkinManager;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.M1A1AnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.DeconstructedGunModel;
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
public class m1a1_smg_animation extends DeconstructedGunModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        M1A1AnimationController controller = M1A1AnimationController.getInstance();
        GunSkin skin = SkinManager.getSkin(stack);

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getModelComponent(skin, BODY), M1A1AnimationController.INDEX_BODY, transformType, matrices);

            matrices.pushPose();
            if (transformType.firstPerson()) {
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                matrices.translate(0, 0, -0.085f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
            }
            RenderUtil.renderModel(getModelComponent(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
            matrices.popPose();

            RenderUtil.renderModel(getModelComponent(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getModelComponent(skin, BODY), M1A1AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderDrumMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        if (!controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT_EMPTY).equals(controller.getPreviousAnimation())) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getModelComponent(skin, BODY), M1A1AnimationController.INDEX_MAGAZINE, transformType, matrices);
                RenderUtil.renderModel(getModelComponent(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.popPose();
        }

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}