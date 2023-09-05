package com.tac.guns.client.render.armor.vestlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public abstract class ArmorBase extends Model
{
    public ArmorBase()
    {
        super(RenderType::entityCutoutNoCull);
    }

    public ArmorBase(Function<ResourceLocation, RenderType> renderType)
    {
        super(renderType);
    }

    protected static void setRotationAngle(ModelPart renderer, float x, float y, float z)
    {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    public void rotateToPlayerBody(ModelPart body)
    {
        ModelPart root = this.getModel();
        root.copyFrom(body);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        this.getModel().render(matrixStack, builder, p_225598_3_, OverlayTexture.NO_OVERLAY, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
    protected abstract ModelPart getModel();{}

    protected abstract ResourceLocation getTexture();{}
    protected abstract void setTexture(String modId, String path);{}
}