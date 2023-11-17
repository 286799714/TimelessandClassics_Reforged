package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.gunskin.SkinManager;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.M82A2AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.AbstractSkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
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
public class m82a2_animation extends AbstractSkinnedGunModel {

    public m82a2_animation() {
        //extraOffset.put(BARREL, new Vector3d(0, 0, -1.5));
    }

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {

        M82A2AnimationController controller = M82A2AnimationController.getInstance();


        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

        matrices.pushPose();
        double v1 = -4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0;
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M82A2AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
                RenderUtil.renderModel(getComponentModel(skin, SIGHT), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(getComponentModel(skin, SIGHT_FOLDED), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);

            //matrices.pushPose();
            if (transformType.firstPerson()) {
                matrices.translate(0, 0, 0.375f * v1);
                matrices.translate(0, 0, 0.025F);
            }
            matrices.translate(0, 0, -1.5F);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.BARREL), stack, matrices, renderBuffer, light, overlay);
            matrices.translate(0, 0, 1.5F);
            //matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M82A2AnimationController.INDEX_BOLT, transformType, matrices);
            if (transformType.firstPerson()) {
                matrices.translate(0, 0, 0.375f * v1);
                matrices.translate(0, 0, 0.025F);
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M82A2AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M82A2AnimationController.INDEX_BULLET, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), M82A2AnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, HANDLE), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}