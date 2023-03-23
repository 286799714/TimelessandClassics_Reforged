package com.tac.guns.client.render.armor.models;// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tac.guns.Reference;
import com.tac.guns.client.render.armor.VestLayer.ArmorBase;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModernArmor extends ArmorBase {
	private final ModelRenderer Light_Armor;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer ammo;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bone3;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;

	public ModernArmor() {
		//super(0f);
		texWidth = 64;
		texHeight = 64;

		Light_Armor = new ModelRenderer(this);
		Light_Armor.setPos(0.0F, 24.0F, 0.0F);
		Light_Armor.texOffs(0, 12).addBox(-4.0F, -22.0F, -3.0F, 8.0F, 10.0F, 1.0F, 0.0F, false);
		Light_Armor.texOffs(15, 41).addBox(3.3125F, -17.1875F, -2.9375F, 1.0F, 5.0F, 1.0F, -0.125F, false);
		Light_Armor.texOffs(15, 41).addBox(-4.3125F, -17.1875F, -2.9375F, 1.0F, 5.0F, 1.0F, -0.125F, true);
		Light_Armor.texOffs(15, 41).addBox(-4.3125F, -17.1875F, 1.6875F, 1.0F, 5.0F, 1.0F, -0.125F, true);
		Light_Armor.texOffs(15, 41).addBox(3.3125F, -17.1875F, 1.6875F, 1.0F, 5.0F, 1.0F, -0.125F, false);
		Light_Armor.texOffs(0, 0).addBox(-4.0F, -22.0625F, 1.6875F, 8.0F, 10.0F, 1.0F, 0.0625F, false);
		Light_Armor.texOffs(0, 24).addBox(-3.0F, -24.1875F, 1.6875F, 6.0F, 2.0F, 1.0F, 0.0625F, false);
		Light_Armor.texOffs(19, 12).addBox(-5.0F, -18.0F, -2.625F, 1.0F, 6.0F, 5.0F, 0.0F, false);
		Light_Armor.texOffs(19, 0).addBox(4.0F, -18.0F, -2.625F, 1.0F, 6.0F, 5.0F, 0.0F, false);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(0.0F, -23.8125F, 3.6309F);
		Light_Armor.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.9055F, -0.9798F, -0.8137F);
		cube_r1.texOffs(16, 37).addBox(-2.9201F, -1.0F, 1.0427F, 3.0F, 2.0F, 1.0F, -0.3125F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(0.0F, -23.8125F, 3.6309F);
		Light_Armor.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.4538F, 0.0F, 0.0F);
		cube_r2.texOffs(27, 0).addBox(-2.0F, -1.0F, -0.2403F, 4.0F, 2.0F, 1.0F, -0.3125F, false);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setPos(0.0F, -23.8125F, 3.6309F);
		Light_Armor.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.9055F, 0.9798F, 0.8137F);
		cube_r3.texOffs(25, 37).addBox(-0.0799F, -1.0F, 1.0427F, 3.0F, 2.0F, 1.0F, -0.3125F, false);

		ammo = new ModelRenderer(this);
		ammo.setPos(-2.8828F, -14.125F, -2.6978F);
		Light_Armor.addChild(ammo);
		ammo.texOffs(9, 28).addBox(2.8828F, -1.125F, -1.4272F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		ammo.texOffs(24, 24).addBox(2.3828F, -4.0F, -1.8022F, 2.0F, 4.0F, 2.0F, -0.25F, false);
		ammo.texOffs(0, 34).addBox(2.3828F, -3.5F, -1.3022F, 2.0F, 5.0F, 1.0F, 0.0F, false);
		ammo.texOffs(0, 28).addBox(0.4669F, -1.5F, -1.5056F, 2.0F, 3.0F, 2.0F, -0.25F, false);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setPos(0.0F, 0.0F, 0.125F);
		ammo.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.2618F, 0.0F);
		cube_r4.texOffs(18, 31).addBox(-1.0F, -1.5F, -1.375F, 2.0F, 3.0F, 2.0F, -0.25F, false);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setPos(5.5703F, -1.0F, -0.6147F);
		ammo.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, -0.2269F, 0.0F);
		cube_r5.texOffs(36, 29).addBox(-1.0F, -2.5F, -0.375F, 2.0F, 5.0F, 1.0F, 0.0F, false);
		cube_r5.texOffs(15, 24).addBox(-1.0F, -3.0F, -0.875F, 2.0F, 4.0F, 2.0F, -0.25F, false);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setPos(-0.0459F, -1.0625F, -0.5622F);
		ammo.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.2269F, 0.0F);
		cube_r6.texOffs(19, 0).addBox(-1.0313F, 0.9375F, -0.125F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setPos(5.8116F, -1.0625F, -0.5622F);
		ammo.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, -0.2269F, 0.0F);
		cube_r7.texOffs(19, 12).addBox(0.0313F, -1.0625F, -0.125F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		cube_r7.texOffs(40, 13).addBox(-0.7188F, -0.0625F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		bone = new ModelRenderer(this);
		bone.setPos(3.6727F, -23.6804F, -0.1056F);
		Light_Armor.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0F, -0.0175F);
		

		cube_r8 = new ModelRenderer(this);
		cube_r8.setPos(-0.2303F, 0.556F, -1.9133F);
		bone.addChild(cube_r8);
		setRotationAngle(cube_r8, 2.9671F, 0.0F, 0.3927F);
		cube_r8.texOffs(27, 31).addBox(-1.5F, -2.5F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setPos(0.2669F, -0.6443F, -1.4897F);
		bone.addChild(cube_r9);
		setRotationAngle(cube_r9, 1.9722F, 0.0F, 0.3927F);
		cube_r9.texOffs(40, 9).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, false);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setPos(0.2488F, -0.6006F, 1.4433F);
		bone.addChild(cube_r10);
		setRotationAngle(cube_r10, 1.2566F, 0.0F, 0.3927F);
		cube_r10.texOffs(7, 37).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, false);

		cube_r11 = new ModelRenderer(this);
		cube_r11.setPos(-0.4618F, 1.1149F, 2.1543F);
		bone.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0524F, 0.0F, 0.3927F);
		cube_r11.texOffs(32, 4).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);

		bone2 = new ModelRenderer(this);
		bone2.setPos(-3.6727F, -23.6804F, -0.1056F);
		Light_Armor.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 0.0175F);
		

		cube_r12 = new ModelRenderer(this);
		cube_r12.setPos(0.2303F, 0.556F, -1.9133F);
		bone2.addChild(cube_r12);
		setRotationAngle(cube_r12, 2.9671F, 0.0F, -0.3927F);
		cube_r12.texOffs(9, 31).addBox(-1.5F, -2.5F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);

		cube_r13 = new ModelRenderer(this);
		cube_r13.setPos(-0.2669F, -0.6443F, -1.4897F);
		bone2.addChild(cube_r13);
		setRotationAngle(cube_r13, 1.9722F, 0.0F, -0.3927F);
		cube_r13.texOffs(38, 0).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, false);

		cube_r14 = new ModelRenderer(this);
		cube_r14.setPos(-0.2488F, -0.6006F, 1.4433F);
		bone2.addChild(cube_r14);
		setRotationAngle(cube_r14, 1.2566F, 0.0F, -0.3927F);
		cube_r14.texOffs(35, 36).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, false);

		cube_r15 = new ModelRenderer(this);
		cube_r15.setPos(0.4618F, 1.1149F, 2.1543F);
		bone2.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0524F, 0.0F, -0.3927F);
		cube_r15.texOffs(31, 11).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, false);

		bone3 = new ModelRenderer(this);
		bone3.setPos(-2.0546F, -16.0395F, -3.5F);
		Light_Armor.addChild(bone3);
		setRotationAngle(bone3, 0.0064F, -0.122F, 3.0016F);
		bone3.texOffs(30, 41).addBox(-0.2579F, -0.4448F, -0.5F, 1.0F, 1.0F, 1.0F, -0.125F, false);

		cube_r16 = new ModelRenderer(this);
		cube_r16.setPos(-1.0741F, -0.0358F, 0.0F);
		bone3.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, 0.0F, -0.0349F);
		cube_r16.texOffs(25, 41).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, -0.3125F, false);

		cube_r17 = new ModelRenderer(this);
		cube_r17.setPos(0.4006F, 0.0975F, -0.0625F);
		bone3.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, 0.0F, 0.2967F);
		cube_r17.texOffs(32, 20).addBox(-2.0F, -0.5F, -0.4375F, 4.0F, 1.0F, 1.0F, -0.3125F, false);

		cube_r18 = new ModelRenderer(this);
		cube_r18.setPos(1.523F, -0.0342F, 0.0F);
		bone3.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0F, 0.0F, -0.2269F);
		cube_r18.texOffs(33, 26).addBox(-3.0625F, -0.75F, -0.5F, 4.0F, 1.0F, 1.0F, -0.3125F, false);

		bone4 = new ModelRenderer(this);
		bone4.setPos(-2.0546F, -16.852F, -3.375F);
		Light_Armor.addChild(bone4);
		setRotationAngle(bone4, 0.0266F, -0.2253F, 2.9114F);
		bone4.texOffs(20, 41).addBox(-0.2579F, -0.4448F, -0.5F, 1.0F, 1.0F, 1.0F, -0.125F, false);

		cube_r19 = new ModelRenderer(this);
		cube_r19.setPos(-1.0741F, -0.0358F, 0.0F);
		bone4.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.0F, 0.0F, -0.0349F);
		cube_r19.texOffs(41, 4).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, -0.3125F, false);

		cube_r20 = new ModelRenderer(this);
		cube_r20.setPos(0.4006F, 0.0975F, -0.0625F);
		bone4.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.0F, 0.0F, 0.2967F);
		cube_r20.texOffs(31, 23).addBox(-2.0F, -0.5F, -0.4375F, 4.0F, 1.0F, 1.0F, -0.3125F, false);

		cube_r21 = new ModelRenderer(this);
		cube_r21.setPos(1.523F, -0.0342F, 0.0F);
		bone4.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.0F, 0.0F, -0.2269F);
		cube_r21.texOffs(32, 17).addBox(-3.0625F, -0.75F, -0.5F, 4.0F, 1.0F, 1.0F, -0.3125F, false);
	}

	//@Override
	public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		matrixStack.translate(0, 1.5, 0);
		Light_Armor.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected ModelRenderer getModel() {
		return Light_Armor;
	}

	// Updatable in order to allow for damaged textures as well to be applied
	private static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/armor/light_armor_1.png");

	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

	@Override
	protected void setTexture(String modId, String path) {
		TEXTURE = new ResourceLocation(modId, path);
	}
}