package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.COLTPYTHONAnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.tac.guns.client.render.model.CommonComponents.*;

public class colt_python_animation extends SkinnedGunModel {
    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        COLTPYTHONAnimationController controller = COLTPYTHONAnimationController.getInstance();

        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_BODY, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer, light, overlay);
        RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices, renderBuffer, 15728880, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_MAG, transformType, matrices);
        RenderUtil.renderModel(getComponentModel(skin, MAG), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_BODY, transformType, matrices);
//        if ((!(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) &&
//                controller.isAnimationRunning()) ||
//                !(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation()) &&
//                        controller.isAnimationRunning())) && Gun.hasAmmo(stack)) {
        final Player player = Minecraft.getInstance().player;
        if (!SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING) && Gun.hasAmmo(stack)) {
            matrices.mulPose(Vector3f.XN.rotationDegrees(-35F * (0.74F * 1.74F)));
            matrices.translate(0, (0.74F * 1.74F) * 0.135, -0.0625F * (0.74F * 1.74F));
            if (cooldownOg < 0.74) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(-35F * (cooldownOg * 1.74F)));
                matrices.translate(0, -(cooldownOg * 1.74F) * 0.135, 0.0625F * (cooldownOg * 1.74F));
            }
        }
        RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.HAMMER), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_MAG, transformType, matrices);
        if (cooldownOg < 0.74) {
            matrices.mulPose(Vector3f.ZN.rotationDegrees(-45F * (cooldownOg * 1.74F)));
            matrices.translate(1.45 * (cooldownOg * 1.74F) * 0.0625, -0.625 * (cooldownOg * 1.74F) * 0.0625, 0);
        }
        RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.ROTATE), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        if ((controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) ||
                controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation())) &&
                transformType.firstPerson() && controller.isAnimationRunning()) {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_LOADER, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.LOADER), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_BULLET1, transformType, matrices);
        if (cooldownOg < 0.74) {
            matrices.mulPose(Vector3f.ZN.rotationDegrees(-45F * (cooldownOg * 1.74F)));
            matrices.translate(1.45 * (cooldownOg * 1.74F) * 0.0625, -0.625 * (cooldownOg * 1.74F) * 0.0625, 0);
        }
        RenderUtil.renderModel(getComponentModel(skin, BULLET1), stack, matrices, renderBuffer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        if ((controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL).equals(controller.getPreviousAnimation()) ||
                controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY).equals(controller.getPreviousAnimation())) &&
                transformType.firstPerson() && controller.isAnimationRunning()) {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY), COLTPYTHONAnimationController.INDEX_BULLET2, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET2), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}