package com.tac.guns.client.render.armor.VestLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
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
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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

    public static boolean isBackpackVisible(Player player)
    {
        AtomicReference<Boolean> visible = new AtomicReference<>(true);
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        optional.ifPresent(itemHandler -> {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(GunMod.curiosRigSlotId);
            stacksOptional.ifPresent(stacksHandler -> {
                visible.set(stacksHandler.getRenders().get(0));
            });
        });
        return visible.get();
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        // Could this be too slow? The amount of checks is looks a bit, insane for high performance?
        if(GunMod.curiosLoaded && !isBackpackVisible(player))
            return;

        if(WearableHelper.PlayerWornRig(player) != null && Config.COMMON.gameplay.renderTaCArmor.get())
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
                if(this.getParentModel().crouching)
                {
                    stack.translate(0,-0.15,0.7475); //TODO: Rebuild all armor files to a more proper Y: position to line up with player body, this will allow the rotate sync above to work properly.
                }
                VertexConsumer builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, model.renderType(model.getTexture()), false, false);
                model.renderToBuffer(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            stack.popPose();
        }
    }
}

