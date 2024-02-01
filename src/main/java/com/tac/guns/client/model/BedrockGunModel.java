package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.client.render.item.IOverrideModel;
import com.tac.guns.client.resource.model.bedrock.BedrockVersion;
import com.tac.guns.client.resource.model.bedrock.pojo.BedrockModelPOJO;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.sql.CallableStatement;

public class BedrockGunModel extends BedrockAnimatedModel implements IOverrideModel {
    protected Gun currentModifiedGun;
    protected ItemStack currentItem;
    protected ItemStack currentParent;
    protected LivingEntity currentEntity;
    public BedrockGunModel(BedrockModelPOJO pojo, BedrockVersion version, RenderType renderType) {
        super(pojo, version, renderType);
        this.setFunctionalRenderer("LeftHand", bedrockPart -> (poseStack, transformType, consumer, light, overlay) -> {
            if(transformType.firstPerson()){
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
                Matrix3f normal = poseStack.last().normal().copy();
                Matrix4f pose = poseStack.last().pose().copy();
                //和枪械模型共用缓冲区的都需要代理到渲染结束后渲染
                this.delegateRender((poseStack1, transformType1, consumer1, light1, overlay1) -> {
                    PoseStack poseStack2 = new PoseStack();
                    poseStack2.last().normal().mul(normal);
                    poseStack2.last().pose().multiply(pose);
                    RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.LEFT, poseStack2, Minecraft.getInstance().renderBuffers().bufferSource(), light1);
                });
            }
        });
        this.setFunctionalRenderer("RightHand", bedrockPart -> (poseStack, transformType, consumer, light, overlay) -> {
            if(transformType.firstPerson()){
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
                Matrix3f normal = poseStack.last().normal().copy();
                Matrix4f pose = poseStack.last().pose().copy();
                //和枪械模型共用缓冲区的都需要代理到渲染结束后渲染
                this.delegateRender((poseStack1, transformType1, consumer1, light1, overlay1) -> {
                    PoseStack poseStack2 = new PoseStack();
                    poseStack2.last().normal().mul(normal);
                    poseStack2.last().pose().multiply(pose);
                    RenderUtil.renderFirstPersonArm(Minecraft.getInstance().player, HumanoidArm.RIGHT, poseStack2, Minecraft.getInstance().renderBuffers().bufferSource(), light1);
                });
            }
        });
        this.setFunctionalRenderer("bullet_in_barrel", bedrockPart -> {
            CompoundTag tag = currentItem.getOrCreateTag();
            int ammoCount = tag.getInt("AmmoCount");
            bedrockPart.visible = ammoCount != 0;
            return null;
        });
        this.setFunctionalRenderer("bullet_in_mag", bedrockPart -> {
            CompoundTag tag = currentItem.getOrCreateTag();
            int ammoCount = tag.getInt("AmmoCount");
            bedrockPart.visible = ammoCount > 1;
            return null;
        });
        this.setFunctionalRenderer("muzzle_brake", bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.MUZZLE_BRAKE.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer("muzzle_compensator", bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.MUZZLE_COMPENSATOR.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer("muzzle_silencer", bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.SILENCER.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer("muzzle_default", bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ItemStack.EMPTY.getItem();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer("mount", bedrockPart -> {
            bedrockPart.visible = currentModifiedGun.canAttachType(IAttachment.Type.SCOPE) && Gun.getScope(currentItem) != null;
            return null;
        });
    }

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        currentItem = stack;
        currentParent = parent;
        currentEntity = entity;
        if(currentItem != null){
            if(!(currentItem.getItem() instanceof GunItem))
                throw new ClassCastException("The Item type of the item stack in the formal parameter must be GunItem when render BedrockGunModel");
            currentModifiedGun = ((GunItem) currentItem.getItem()).getModifiedGun(currentItem);
        }
        render(partialTicks, transformType, matrixStack, buffer, light, overlay);
    }
}
