package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
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
public class mosberg590_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        
        if(Gun.getScope(stack) == null)
        {
            RenderUtil.renderModel(SpecialModels.MOSBERG590_SIGHTS.getModel(), stack, matrices, renderBuffer, light, overlay);
        }

        if(GunModifierHelper.getAmmoCapacity(stack) > -1)
        {
            RenderUtil.renderModel(SpecialModels.MOSBERG590_BULLETS.getModel(), stack, matrices, renderBuffer, light, overlay);
        }

        RenderUtil.renderModel(SpecialModels.MOSBERG590_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);

        //Always push
        matrices.pushPose();

        Gun gun = ((GunItem) stack.getItem()).getGun();
        float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

        if (cooldownOg != 0 && cooldownOg < 0.66 || !Gun.hasAmmo(stack)) {
            double cooldownOgTmp = cooldownOg * 1.46;
            if (Gun.hasAmmo(stack)) {
                // Math provided by Bomb787 on GitHub and Curseforge!!!
                matrices.translate(0, 0, 0.200f * (-4.5 * Math.pow(cooldownOgTmp - 0.5, 2) + 1.0));
            } else if (!Gun.hasAmmo(stack)) {
                {
                    matrices.translate(0, 0, 0.200f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                }
            }
        }
        matrices.translate(0.00, 0.0, 0.04);
        RenderUtil.renderModel(SpecialModels.MOSBERG590_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);

        if(Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem()))
        {
            RenderUtil.renderModel(SpecialModels.MOSBERG590_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
        }

        //Always pop
        matrices.popPose();
    }
}
