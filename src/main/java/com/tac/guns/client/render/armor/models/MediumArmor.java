package com.tac.guns.client.render.armor.models;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tac.guns.Reference;
import com.tac.guns.client.render.armor.vestlayer.ArmorBase;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;


public class MediumArmor extends ArmorBase {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "medium_steel_armor"), "main");
	private final ModelPart medium_steel_armor;

	public MediumArmor() {
		this.medium_steel_armor = createBodyLayer().bakeRoot();
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition medium_steel_armor = partdefinition.addOrReplaceChild("medium_steel_armor", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -20.0F, -3.5F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -24.0F, 2.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 11).addBox(4.0F, -18.0F, -2.5F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(16, 20).addBox(-5.0F, -18.0F, -2.5F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = medium_steel_armor.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, -1.5F, -0.5F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -21.4033F, -2.7715F, -0.1571F, 0.0F, 0.0F));

		PartDefinition cube_r2 = medium_steel_armor.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(51, 11).addBox(-0.0799F, -1.0F, 1.0427F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, -23.8125F, 3.6309F, 0.9055F, 0.9798F, 0.8137F));

		PartDefinition cube_r3 = medium_steel_armor.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(42, 23).addBox(-2.0F, -1.0F, -0.2403F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, -23.8125F, 3.6309F, 0.4538F, 0.0F, 0.0F));

		PartDefinition cube_r4 = medium_steel_armor.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(18, 50).addBox(-2.9201F, -1.0F, 1.0427F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, -23.8125F, 3.6309F, 0.9055F, -0.9798F, -0.8137F));

		PartDefinition bone8 = medium_steel_armor.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(29, 23).addBox(0.75F, -18.875F, -5.4375F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.375F))
		.texOffs(26, 40).addBox(0.75F, -17.6875F, -4.875F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 31).addBox(0.75F, -18.6875F, -3.875F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone3 = medium_steel_armor.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(35, 38).addBox(-0.9477F, -1.6202F, -0.5636F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(33, 45).addBox(-0.9477F, -0.6202F, -1.4386F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-2.75F, -15.0673F, -3.3711F, 0.0F, 0.1047F, 0.0F));

		PartDefinition cube_r5 = bone3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(36, 51).addBox(-1.8227F, -0.0062F, -1.4941F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(0.875F, -1.4505F, -0.1565F, -0.1134F, 0.0F, 0.0F));

		PartDefinition cube_r6 = bone3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(51, 25).addBox(-1.8227F, -0.4422F, -1.4825F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(0.875F, -1.1202F, -0.1914F, -0.2443F, 0.0F, 0.0F));

		PartDefinition bone4 = medium_steel_armor.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(37, 31).addBox(-0.9826F, -1.6202F, -0.566F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(44, 27).addBox(-0.9826F, -0.6202F, -1.441F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-1.0F, -15.0673F, -3.4961F, 0.0F, 0.0349F, 0.0F));

		PartDefinition cube_r7 = bone4.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(51, 40).addBox(-0.1076F, -0.0059F, -1.4965F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-0.875F, -1.4505F, -0.1565F, -0.1134F, 0.0F, 0.0F));

		PartDefinition cube_r8 = bone4.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(27, 51).addBox(-0.1076F, -0.4416F, -1.4849F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-0.875F, -1.1202F, -0.1914F, -0.2443F, 0.0F, 0.0F));

		PartDefinition bone = medium_steel_armor.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 25).addBox(4.1875F, -17.6875F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(-0.1875F))
		.texOffs(19, 0).addBox(4.875F, -17.375F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(-0.125F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone5 = medium_steel_armor.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(44, 38).addBox(-0.875F, -3.5F, -2.375F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.1875F))
		.texOffs(44, 13).addBox(-0.875F, -3.5F, -0.125F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.1875F))
		.texOffs(49, 50).addBox(-0.875F, 1.875F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(37, 14).addBox(0.75F, -3.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(34, 5).addBox(-1.5F, -3.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.1875F)), PartPose.offsetAndRotation(-5.8125F, -14.5F, -1.5F, 0.164F, -0.1496F, -0.1577F));

		PartDefinition bone6 = medium_steel_armor.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(9, 41).addBox(-0.875F, -3.5F, -2.375F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.1875F))
		.texOffs(41, 5).addBox(-0.875F, -3.5F, -0.125F, 2.0F, 6.0F, 1.0F, new CubeDeformation(-0.1875F))
		.texOffs(49, 19).addBox(-0.875F, 1.875F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(20, 32).addBox(0.75F, -3.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.1875F))
		.texOffs(13, 32).addBox(-1.5F, -3.5F, -1.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.1875F)), PartPose.offsetAndRotation(-5.8125F, -14.5F, 1.8125F, 0.0944F, -0.1391F, -0.2807F));

		PartDefinition bone2 = medium_steel_armor.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(3.6727F, -23.6804F, -0.1056F, 0.0F, 0.0F, -0.0175F));

		PartDefinition cube_r9 = bone2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 41).addBox(-1.5F, -3.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1601F, 0.3865F, -1.8659F, 3.0369F, 0.0F, 0.3927F));

		PartDefinition cube_r10 = bone2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(51, 36).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3079F, -0.7434F, -1.5763F, 1.885F, 0.0F, 0.3927F));

		PartDefinition cube_r11 = bone2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(48, 6).addBox(-1.5F, -2.875F, 0.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.125F)), PartPose.offsetAndRotation(0.2488F, -0.6006F, 1.4433F, 1.5533F, 0.0F, 0.3927F));

		PartDefinition cube_r12 = bone2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(9, 50).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2488F, -0.6006F, 1.4433F, 1.2566F, 0.0F, 0.3927F));

		PartDefinition cube_r13 = bone2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(42, 46).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4618F, 1.1149F, 2.1543F, 0.0524F, 0.0F, 0.3927F));

		PartDefinition bone7 = medium_steel_armor.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.6727F, -23.6804F, -0.1056F, 0.0F, 0.0F, 0.0175F));

		PartDefinition cube_r14 = bone7.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, -3.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1601F, 0.3865F, -1.8659F, 3.0369F, 0.0F, -0.3927F));

		PartDefinition cube_r15 = bone7.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(51, 15).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3079F, -0.7434F, -1.5763F, 1.885F, 0.0F, -0.3927F));

		PartDefinition cube_r16 = bone7.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(19, 11).addBox(-1.5F, -2.875F, 0.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.125F)), PartPose.offsetAndRotation(-0.2488F, -0.6006F, 1.4433F, 1.5533F, 0.0F, -0.3927F));

		PartDefinition cube_r17 = bone7.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(0, 48).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2488F, -0.6006F, 1.4433F, 1.2566F, 0.0F, -0.3927F));

		PartDefinition cube_r18 = bone7.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(16, 41).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4618F, 1.1149F, 2.1543F, 0.0524F, 0.0F, -0.3927F));

		PartDefinition bone9 = medium_steel_armor.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(51, 45).addBox(-0.2579F, -0.4644F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offsetAndRotation(-6.5745F, -15.507F, -3.6935F, 0.5342F, -0.1943F, 1.4105F));

		PartDefinition cube_r19 = bone9.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(27, 47).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-1.0741F, -0.0553F, 0.0F, 0.0F, 0.0F, -0.0349F));

		PartDefinition cube_r20 = bone9.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(47, 3).addBox(-2.0F, -0.5F, -0.4375F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.4006F, 0.0779F, -0.0625F, 0.0F, 0.0F, 0.2967F));

		PartDefinition cube_r21 = bone9.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(16, 47).addBox(-3.0625F, -0.75F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(1.523F, -0.0537F, 0.0F, 0.0F, 0.0F, -0.2269F));

		PartDefinition bone10 = medium_steel_armor.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(11, 25).addBox(-0.2579F, -0.4644F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offsetAndRotation(-7.0745F, -15.882F, -0.3185F, 0.812F, -0.138F, 1.4072F));

		PartDefinition cube_r22 = bone10.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(19, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-1.0741F, -0.0553F, 0.0F, 0.0F, 0.0F, -0.0349F));

		PartDefinition cube_r23 = bone10.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, -0.5F, -0.4375F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.4006F, 0.0779F, -0.0625F, 0.0F, 0.0F, 0.2967F));

		PartDefinition cube_r24 = bone10.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(46, 33).addBox(-3.0625F, -0.75F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(1.523F, -0.0537F, 0.0F, 0.0F, 0.0F, -0.2269F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		medium_steel_armor.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected ModelPart getModel() {
		return medium_steel_armor;
	}

	private static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/armor/bulletproof_vest_2.png");

	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

	@Override
	protected void setTexture(String modId, String path) {
		TEXTURE = new ResourceLocation(modId, path);
	}
}