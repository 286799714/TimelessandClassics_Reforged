package com.tac.guns.client.render.armor.models;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tac.guns.Reference;
import com.tac.guns.client.render.armor.vestlayer.ArmorBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


@OnlyIn(Dist.CLIENT)
public class CardboardArmor extends ArmorBase {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "cardboardarmor"), "main");
	private final ModelPart Cardboard_armor;
	private final ModelPart bone6;

	public CardboardArmor(ModelPart root) {
		this.Cardboard_armor = root.getChild("Cardboard_armor");
		this.bone6 = root.getChild("bone6");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Cardboard_armor = partdefinition.addOrReplaceChild("Cardboard_armor", CubeListBuilder.create().texOffs(124, 174).addBox(-4.0F, -22.0F, -3.25F, 8.0F, 10.0F, 1.0F, new CubeDeformation(-0.3125F))
				.texOffs(105, 174).addBox(-4.0F, -24.0F, 2.4375F, 8.0F, 12.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = Cardboard_armor.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(178, 178).addBox(-0.5F, -9.0F, -0.5F, 2.0F, 12.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-4.7129F, -15.0F, 2.4973F, 0.0F, -0.3927F, 0.0F));

		PartDefinition cube_r2 = Cardboard_armor.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(171, 178).addBox(-1.5F, -9.0F, -0.5F, 2.0F, 12.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(4.7129F, -15.0F, 2.4973F, 0.0F, 0.3927F, 0.0F));

		PartDefinition cube_r3 = Cardboard_armor.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(152, 182).addBox(-1.5F, -6.0F, -0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(4.7129F, -15.0F, -2.3098F, 0.0F, -0.3927F, 0.0F));

		PartDefinition cube_r4 = Cardboard_armor.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(185, 178).addBox(-0.5F, -6.0F, -0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-4.7129F, -15.0F, -2.3098F, 0.0F, 0.3927F, 0.0F));

		PartDefinition bone = Cardboard_armor.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(4.2928F, -23.7479F, -2.1132F));

		PartDefinition cube_r5 = bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(110, 188).addBox(-1.5F, -1.2295F, -0.7156F, 3.0F, 5.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.48F));

		PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(168, 192).addBox(-1.5F, -2.0509F, -1.2459F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.0F, 0.48F));

		PartDefinition cube_r7 = bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(78, 190).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-0.1755F, 0.3372F, 4.2767F, -2.906F, 0.0F, 0.48F));

		PartDefinition cube_r8 = bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(51, 190).addBox(-1.5F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.711F, -1.3657F, 1.3814F, -1.7279F, 0.0F, 0.48F));

		PartDefinition bone2 = Cardboard_armor.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(-4.2928F, -23.7479F, -2.1132F));

		PartDefinition cube_r9 = bone2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(186, 160).addBox(-1.5F, -1.2295F, -0.7156F, 3.0F, 5.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, -0.48F));

		PartDefinition cube_r10 = bone2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(137, 192).addBox(-1.5F, -2.0509F, -1.2459F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.0F, -0.48F));

		PartDefinition cube_r11 = bone2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(69, 190).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(0.1755F, 0.3372F, 4.2767F, -2.906F, 0.0F, -0.48F));

		PartDefinition cube_r12 = bone2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(42, 190).addBox(-1.5F, -3.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-0.711F, -1.3657F, 1.3814F, -1.7279F, 0.0F, -0.48F));

		PartDefinition bone4 = Cardboard_armor.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(164, 174).addBox(-8.5172F, -1.0F, 6.0179F, 9.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(4.0172F, -13.375F, -2.7693F));

		PartDefinition cube_r13 = bone4.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(192, 182).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.5451F, 0.0F, 0.9078F, 0.0F, -1.2305F, 0.0F));

		PartDefinition cube_r14 = bone4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(9, 190).addBox(-2.5F, -1.0F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.7811F, 0.0F, 3.7813F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r15 = bone4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(87, 190).addBox(-2.0F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5986F, 0.0F, 6.0671F, 0.0F, -2.3736F, 0.0F));

		PartDefinition cube_r16 = bone4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(191, 167).addBox(-1.0F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-8.6331F, 0.0F, 6.0671F, 0.0F, 2.3736F, 0.0F));

		PartDefinition cube_r17 = bone4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(31, 190).addBox(-1.5F, -1.0F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-9.8156F, 0.0F, 3.7813F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r18 = bone4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(129, 193).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-9.5796F, 0.0F, 0.9078F, 0.0F, 1.2305F, 0.0F));

		PartDefinition cube_r19 = bone4.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(182, 138).addBox(-5.0F, -1.0F, -0.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-0.5629F, 0.0F, -0.4032F, 0.0F, -0.0873F, 0.0F));

		PartDefinition cube_r20 = bone4.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(20, 190).addBox(-1.0F, -1.0F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-7.4715F, 0.0F, -0.4032F, 0.0F, 0.0873F, 0.0F));

		PartDefinition cube_r21 = bone4.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(192, 178).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.7508F, 0.0F, -0.014F, 0.0F, -0.4887F, 0.0F));

		PartDefinition cube_r22 = bone4.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(193, 142).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-8.7853F, 0.0F, -0.014F, 0.0F, 0.4887F, 0.0F));

		PartDefinition bone3 = Cardboard_armor.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(160, 178).addBox(5.0266F, -17.625F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(-0.3125F))
				.texOffs(143, 174).addBox(-4.5F, -17.625F, 2.8194F, 9.0F, 6.0F, 1.0F, new CubeDeformation(-0.3125F))
				.texOffs(175, 149).addBox(-6.0266F, -17.625F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(-0.3125F)), PartPose.offset(0.0F, 0.0F, 0.1875F));

		PartDefinition cube_r23 = bone3.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(143, 182).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 3.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-4.4858F, -14.625F, 2.8597F, 0.0F, 0.6981F, 0.0F));

		PartDefinition cube_r24 = bone3.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(186, 142).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-4.8951F, -14.625F, -3.1748F, 0.0F, -0.3927F, 0.0F));

		PartDefinition cube_r25 = bone3.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(122, 186).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-4.526F, -14.625F, -3.2218F, 0.0F, -0.925F, 0.0F));

		PartDefinition cube_r26 = bone3.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(175, 138).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(-2.4605F, -14.625F, -3.3076F, 0.0F, -1.7453F, 0.0F));

		PartDefinition cube_r27 = bone3.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(175, 160).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(2.4605F, -14.625F, -3.3076F, 0.0F, 1.7453F, 0.0F));

		PartDefinition cube_r28 = bone3.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(186, 151).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(4.526F, -14.625F, -3.2218F, 0.0F, 0.925F, 0.0F));

		PartDefinition cube_r29 = bone3.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(185, 168).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 3.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(4.4858F, -14.625F, 2.8597F, 0.0F, -0.6981F, 0.0F));

		PartDefinition cube_r30 = bone3.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(103, 188).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.3125F)), PartPose.offsetAndRotation(4.8951F, -14.625F, -3.1748F, 0.0F, 0.3927F, 0.0F));

		PartDefinition bone5 = Cardboard_armor.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r31 = bone5.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(185, 189).addBox(-2.0F, -0.8125F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(0.0793F, -21.4629F, -2.8786F, -0.1571F, -0.0027F, -0.0172F));

		PartDefinition cube_r32 = bone5.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(159, 189).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(3.9466F, -22.408F, 2.1129F, 0.1147F, 0.0399F, 0.2112F));

		PartDefinition cube_r33 = bone5.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(60, 190).addBox(-1.5F, -3.0F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-3.3918F, -21.4629F, 2.2308F, 0.1182F, -0.0277F, -0.3153F));

		PartDefinition cube_r34 = bone5.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(96, 190).addBox(-1.8125F, -1.4375F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(2.9755F, -21.1954F, -2.9377F, -0.0715F, 0.0517F, 1.2193F));

		PartDefinition cube_r35 = bone5.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(177, 192).addBox(-0.8281F, -1.4688F, -0.375F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-1.1332F, -20.544F, -2.9703F, -0.0767F, 0.0174F, 0.0749F));

		PartDefinition cube_r36 = bone5.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(146, 192).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-2.9655F, -21.3541F, -2.9016F, -0.0852F, -0.0227F, -0.8537F));

		PartDefinition cube_r37 = bone5.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(129, 186).addBox(-1.5F, -3.0F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(-3.5793F, -22.4629F, -2.8942F, -0.1055F, 0.0017F, -0.5579F));

		PartDefinition cube_r38 = bone5.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(0, 190).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(-0.375F)), PartPose.offsetAndRotation(3.7668F, -22.6504F, -2.9099F, -0.1055F, 0.0039F, 0.61F));

		PartDefinition bone6 = partdefinition.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.4064F, 4.6354F, 1.5F));

		PartDefinition cube_r39 = bone6.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(35, 172).addBox(-8.0F, -8.0F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3438F)), PartPose.offsetAndRotation(-9.167F, 4.4855F, -10.167F, 0.1173F, 0.6365F, 0.1958F));

		PartDefinition cube_r40 = bone6.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(169, 120).addBox(-5.9375F, -6.875F, -0.5156F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.4375F)), PartPose.offsetAndRotation(-7.3232F, -1.0672F, -10.609F, 0.3557F, 0.1701F, 1.1433F));

		PartDefinition cube_r41 = bone6.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(140, 137).addBox(-8.1094F, -4.9688F, -14.2891F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(6.48F, -3.7604F, -10.6738F, 0.0F, 2.7489F, 0.0F));

		PartDefinition cube_r42 = bone6.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(35, 154).addBox(-7.7031F, -6.25F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(6.5352F, -0.6354F, -10.4972F, -0.0594F, -0.3884F, 0.1557F));

		PartDefinition cube_r43 = bone6.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(0, 172).addBox(-7.9531F, -7.875F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(-7.3481F, -0.6354F, -10.4972F, 0.0842F, 0.384F, 0.2214F));

		PartDefinition cube_r44 = bone6.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(70, 154).addBox(-8.0F, -8.0F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(-0.3458F, 4.2547F, -11.2813F, 0.0F, 0.0F, -0.1396F));

		PartDefinition cube_r45 = bone6.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(105, 156).addBox(-7.9375F, -8.0F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(-0.4064F, 2.5521F, -11.2813F, 0.0F, 0.0F, 0.2443F));

		PartDefinition cube_r46 = bone6.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(140, 156).addBox(-9.0F, -6.6875F, -0.5156F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.5F)), PartPose.offsetAndRotation(8.8735F, 3.0214F, -9.9713F, 0.2048F, -0.6169F, -0.3447F));

		PartDefinition cube_r47 = bone6.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(169, 102).addBox(-8.0F, -8.25F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(8.3417F, 3.1771F, -10.1373F, -0.1554F, -0.6294F, 0.2601F));

		PartDefinition cube_r48 = bone6.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(70, 172).addBox(-8.0F, -8.25F, -0.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(-7.3125F)), PartPose.offsetAndRotation(-9.1546F, 3.1771F, -10.1373F, -0.1554F, 0.6294F, -0.2601F));

		PartDefinition cube_r49 = bone6.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(0, 34).addBox(-16.0F, -15.6875F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(-1.5039F, 3.5165F, 15.71F, 0.0F, 0.0F, 0.0524F));

		PartDefinition cube_r50 = bone6.addOrReplaceChild("cube_r50", CubeListBuilder.create().texOffs(67, 34).addBox(-16.0F, -16.0F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(0.5743F, 3.8867F, 15.71F, 0.0F, 0.0F, 0.1571F));

		PartDefinition cube_r51 = bone6.addOrReplaceChild("cube_r51", CubeListBuilder.create().texOffs(0, 0).addBox(-15.9375F, -13.1875F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(1.7277F, -2.5947F, 15.1719F, 0.0F, 0.0F, 0.1222F));

		PartDefinition cube_r52 = bone6.addOrReplaceChild("cube_r52", CubeListBuilder.create().texOffs(0, 68).addBox(-16.25F, -12.8125F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(-2.5405F, -2.1259F, 15.1719F, 0.0F, 0.0F, -0.2793F));

		PartDefinition cube_r53 = bone6.addOrReplaceChild("cube_r53", CubeListBuilder.create().texOffs(67, 68).addBox(-15.0625F, -15.3125F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(-2.8851F, -3.3275F, 15.1719F, 0.0F, 0.0F, -0.1222F));

		PartDefinition cube_r54 = bone6.addOrReplaceChild("cube_r54", CubeListBuilder.create().texOffs(0, 102).addBox(-16.6875F, -17.25F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(1.7277F, -2.1259F, 15.1719F, 0.0F, 0.0F, 0.2793F));

		PartDefinition cube_r55 = bone6.addOrReplaceChild("cube_r55", CubeListBuilder.create().texOffs(134, 68).addBox(-14.6563F, -15.0156F, 19.4531F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(-0.9688F, 0.0F, 2.0F, 3.1416F, 0.0F, -3.002F));

		PartDefinition cube_r56 = bone6.addOrReplaceChild("cube_r56", CubeListBuilder.create().texOffs(67, 102).addBox(-16.0F, -16.0F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.5F)), PartPose.offsetAndRotation(-0.3131F, 3.6449F, -18.5918F, 3.1247F, 0.1737F, 3.0441F));

		PartDefinition cube_r57 = bone6.addOrReplaceChild("cube_r57", CubeListBuilder.create().texOffs(134, 0).addBox(-16.0F, -16.0F, -0.5F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.5F)), PartPose.offsetAndRotation(-0.4998F, 3.6449F, -18.5918F, 3.1247F, -0.1737F, -3.0441F));

		PartDefinition cube_r58 = bone6.addOrReplaceChild("cube_r58", CubeListBuilder.create().texOffs(134, 34).addBox(-14.6563F, -15.7656F, 19.4531F, 32.0F, 32.0F, 1.0F, new CubeDeformation(-14.0F)), PartPose.offsetAndRotation(2.375F, 0.0F, 2.0F, 3.1416F, 0.0F, 2.8885F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		matrixStack.translate(0, 1.5, 0);
		Cardboard_armor.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected ModelPart getModel() {
		return Cardboard_armor;
	}

	// Updatable in order to allow for damaged textures as well to be applied
	private static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/armor/cardboard_armor_1.png");

	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

	@Override
	protected void setTexture(String modId, String path) {
		TEXTURE = new ResourceLocation(modId, path);
	}
}