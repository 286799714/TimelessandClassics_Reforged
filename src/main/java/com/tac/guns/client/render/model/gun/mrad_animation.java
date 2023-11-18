package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.Config;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.animation.MRADAnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

public class mrad_animation extends SkinnedGunModel {

    public mrad_animation() {
        extraOffset.put(LASER_BASIC, new Vector3d(0, 0, -0.3));
    }

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)  {
        MRADAnimationController controller = MRADAnimationController.getInstance();


        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), MRADAnimationController.INDEX_BODY, transformType, matrices);

            renderSight(stack, matrices, renderBuffer, light, overlay, skin);

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            matrices.translate(0, 0, -0.55);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(0, 0, 0.3);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BIPOD), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(0, 0, 0.25);

            renderLaserDevice(stack, matrices, renderBuffer, light, overlay, skin);

            if (transformType.firstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                renderLaser(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), MRADAnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT_TAIL), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), MRADAnimationController.INDEX_BOLT, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), MRADAnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            if (controller.isAnimationRunning()) {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY), MRADAnimationController.INDEX_BULLET, transformType, matrices);
                if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.PULL_BOLT).equals(controller.getPreviousAnimation())) {
                    RenderUtil.renderModel(getComponentModel(skin, BULLET_SHELL), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
                }
            }
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}