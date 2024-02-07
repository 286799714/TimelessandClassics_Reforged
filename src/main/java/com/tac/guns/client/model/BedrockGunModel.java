package com.tac.guns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.client.animation.AnimationController;
import com.tac.guns.client.animation.ObjectAnimationRunner;
import com.tac.guns.client.handler.AnimationHandler;
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

import static com.tac.guns.client.model.CommonComponents.*;

public class BedrockGunModel extends BedrockAnimatedModel implements IOverrideModel {
    protected Gun currentModifiedGun;
    protected ItemStack currentItem;
    protected ItemStack currentParent;
    protected LivingEntity currentEntity;
    public static final String IRON_VIEW_NODE = "iron_view";
    public final Vector3f ironViewOffset = new Vector3f(0, 0, 0);
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
        this.setFunctionalRenderer(BULLET_IN_BARREL, bedrockPart -> {
            CompoundTag tag = currentItem.getOrCreateTag();
            int ammoCount = tag.getInt("AmmoCount");
            bedrockPart.visible = ammoCount != 0;
            return null;
        });
        this.setFunctionalRenderer(BULLET_IN_MAG, bedrockPart -> {
            CompoundTag tag = currentItem.getOrCreateTag();
            int ammoCount = tag.getInt("AmmoCount");
            bedrockPart.visible = ammoCount > 1;
            return null;
        });
        this.setFunctionalRenderer(BULLET_CHAIN, bedrockPart -> {
            CompoundTag tag = currentItem.getOrCreateTag();
            int ammoCount = tag.getInt("AmmoCount");
            bedrockPart.visible = ammoCount != 0;
            return null;
        });
        this.setFunctionalRenderer(MUZZLE_BRAKE, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.MUZZLE_BRAKE.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MUZZLE_COMPENSATOR, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.MUZZLE_COMPENSATOR.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MUZZLE_SILENCER, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ModItems.SILENCER.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MUZZLE_DEFAULT, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.BARREL)) {
                ItemStack muzzle = Gun.getAttachment(IAttachment.Type.BARREL, currentItem);
                bedrockPart.visible = muzzle.getItem() == ItemStack.EMPTY.getItem();
            }else
                bedrockPart.visible = true;
            return null;
        });
        this.setFunctionalRenderer(MOUNT, bedrockPart -> {
            bedrockPart.visible = currentModifiedGun.canAttachType(IAttachment.Type.SCOPE) && Gun.getScope(currentItem) != null;
            return null;
        });
        this.setFunctionalRenderer(CARRY, bedrockPart -> {
            bedrockPart.visible = currentModifiedGun.canAttachType(IAttachment.Type.SCOPE) && Gun.getScope(currentItem) == null;
            return null;
        });
        this.setFunctionalRenderer(SIGHT_FOLDED, bedrockPart -> {
            bedrockPart.visible = currentModifiedGun.canAttachType(IAttachment.Type.SCOPE) && Gun.getScope(currentItem) != null;
            return null;
        });
        this.setFunctionalRenderer(SIGHT, bedrockPart -> {
            bedrockPart.visible = currentModifiedGun.canAttachType(IAttachment.Type.SCOPE) && Gun.getScope(currentItem) == null;
            return null;
        });
        this.setFunctionalRenderer(STOCK_LIGHT, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.STOCK)) {
                ItemStack stock = Gun.getAttachment(IAttachment.Type.STOCK, currentItem);
                bedrockPart.visible = stock.getItem() == ModItems.LIGHT_STOCK.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(STOCK_TACTICAL, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.STOCK)) {
                ItemStack stock = Gun.getAttachment(IAttachment.Type.STOCK, currentItem);
                bedrockPart.visible = stock.getItem() == ModItems.TACTICAL_STOCK.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(STOCK_HEAVY, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.STOCK)) {
                ItemStack stock = Gun.getAttachment(IAttachment.Type.STOCK, currentItem);
                bedrockPart.visible = stock.getItem() == ModItems.WEIGHTED_STOCK.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(STOCK_DEFAULT, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.STOCK)) {
                ItemStack stock = Gun.getAttachment(IAttachment.Type.STOCK, currentItem);
                bedrockPart.visible = stock.getItem() == ItemStack.EMPTY.getItem();
            }else
                bedrockPart.visible = true;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_1, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                bedrockPart.visible = mag.getItem() == ModItems.SMALL_EXTENDED_MAG.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_2, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                bedrockPart.visible = mag.getItem() == ModItems.MEDIUM_EXTENDED_MAG.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_3, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                bedrockPart.visible = mag.getItem() == ModItems.LARGE_EXTENDED_MAG.get();
            }else
                bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_STANDARD, bedrockPart -> {
            if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                bedrockPart.visible = mag.getItem() == ItemStack.EMPTY.getItem();
            }else
                bedrockPart.visible = true;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_1_A, bedrockPart -> {
            AnimationController controller = AnimationHandler.controllers.get(currentItem.getItem().getRegistryName());
            if(controller != null) {
                ObjectAnimationRunner runner = controller.getAnimation(AnimationHandler.MAIN_TRACK);
                if(runner != null){
                    if(runner.getAnimation().name.contains("reload")) {
                        if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                            ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                            bedrockPart.visible = mag.getItem() == ModItems.SMALL_EXTENDED_MAG.get();
                            return null;
                        }
                    }
                }
            }
            bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_2_A, bedrockPart -> {
            AnimationController controller = AnimationHandler.controllers.get(currentItem.getItem().getRegistryName());
            if(controller != null) {
                ObjectAnimationRunner runner = controller.getAnimation(AnimationHandler.MAIN_TRACK);
                if(runner != null){
                    if(runner.getAnimation().name.contains("reload")) {
                        if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                            ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                            bedrockPart.visible = mag.getItem() == ModItems.MEDIUM_EXTENDED_MAG.get();
                            return null;
                        }
                    }
                }
            }
            bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_EXTENDED_3_A, bedrockPart -> {
            AnimationController controller = AnimationHandler.controllers.get(currentItem.getItem().getRegistryName());
            if(controller != null) {
                ObjectAnimationRunner runner = controller.getAnimation(AnimationHandler.MAIN_TRACK);
                if(runner != null){
                    if(runner.getAnimation().name.contains("reload")) {
                        if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                            ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                            bedrockPart.visible = mag.getItem() == ModItems.LARGE_EXTENDED_MAG.get();
                            return null;
                        }
                    }
                }
            }
            bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(MAG_STANDARD_A, bedrockPart -> {
            AnimationController controller = AnimationHandler.controllers.get(currentItem.getItem().getRegistryName());
            if(controller != null) {
                ObjectAnimationRunner runner = controller.getAnimation(AnimationHandler.MAIN_TRACK);
                if(runner != null){
                    if(runner.getAnimation().name.contains("reload")) {
                        if (currentModifiedGun.canAttachType(IAttachment.Type.EXTENDED_MAG)) {
                            ItemStack mag = Gun.getAttachment(IAttachment.Type.EXTENDED_MAG, currentItem);
                            bedrockPart.visible = mag.getItem() == ItemStack.EMPTY.getItem();
                            return null;
                        }
                    }
                }
            }
            bedrockPart.visible = false;
            return null;
        });
        this.setFunctionalRenderer(IRON_VIEW_NODE, bedrockPart->{
            //基岩模型上下颠倒，因此坐标轴的x、y轴相反。
            ironViewOffset.setX(-bedrockPart.x / 16f);
            ironViewOffset.setY(-bedrockPart.y / 16f);
            ironViewOffset.setZ(bedrockPart.z / 16f);
            bedrockPart.visible = false;
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
