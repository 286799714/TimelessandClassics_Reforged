package com.tac.guns.client.render.armor.VestLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tac.guns.client.render.armor.models.ModernArmor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import static com.tac.guns.client.render.armor.VestLayer.VestLayerRender.MODELS;

public class ArmorCurioRenderer implements ICurioRenderer {
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack armor, SlotContext slotContext, PoseStack stack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        stack.pushPose();
        {
            Item modelName = armor.getItem();
            ArmorBase model = MODELS.get(modelName);
            if(model == null)
            {
                model = new ModernArmor();
            }
            stack.translate(0, -1.5, 0);
            if (renderLayerParent.getModel() instanceof HumanoidModel<?> parentModel) {
                model.rotateToPlayerBody(parentModel.body); // Default rotation, keep for now? It's a global, maybe force more work on the model implementation side rather then core?
                if (parentModel.crouching) {
                    stack.translate(0, 0.2, 0); //TODO: Rebuild all armor files to a more proper Y: position to line up with player body, this will allow the rotate sync above to work properly.
                }
            }
            VertexConsumer builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, model.renderType(model.getTexture()), false, false);
            model.renderToBuffer(stack, builder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        stack.popPose();
    }
}
