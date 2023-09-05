package com.tac.guns.client.render.armor.vestlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tac.guns.Config;
import com.tac.guns.client.render.armor.models.ModernArmor;
import com.tac.guns.util.WearableHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Inspiration from MrCrayfish backpack code, my biggest problem was understanding RenderTypes and why I should learn more about them. This file is still a test however, all vest items must still
 * have a way to change their textures, along with each vest being a separate render setup rather then render-er.
 *
 * TODO: Enable for eventual bot system, this is for Biped, not just player models...
 */
@OnlyIn(Dist.CLIENT)
public class VestLayerRender<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M>
{
    public VestLayerRender(RenderLayerParent<T, M> renderer)
    {
        super(renderer);
    }

    public static final Map<Item, ArmorBase> MODELS = new HashMap<>();

    public synchronized static <T extends ArmorBase> void registerModel(Item item, T model)
    {
        MODELS.putIfAbsent(item, model);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        if(!WearableHelper.PlayerWornRig(player).isEmpty() && Config.COMMON.gameplay.renderTaCArmor.get())
        {
            ItemStack armor = WearableHelper.PlayerWornRig(player);
            stack.pushPose();
            {
                Item modelName = armor.getItem();
                ArmorBase model = MODELS.get(modelName);
                if(model == null)
                {
                    model = new ModernArmor();
                }
                model.rotateToPlayerBody(this.getParentModel().body); // Default rotation, keep for now? It's a global, maybe force more work on the model implementation side rather then core?
                VertexConsumer builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, model.renderType(model.getTexture()), false, false);
                model.renderToBuffer(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            stack.popPose();
        }
    }
}

