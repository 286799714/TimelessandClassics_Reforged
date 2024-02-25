package com.tac.guns.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.animation.AnimationController;
import com.tac.guns.client.animation.ObjectAnimation;
import com.tac.guns.client.event.BeforeRenderHandEvent;
import com.tac.guns.client.handler.AnimationHandler;
import com.tac.guns.item.GunItem;
import com.tac.guns.network.CommonStateBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class FirstPersonRendererMixin {
    @Shadow
    private ItemStack mainHandItem;
    @Unique
    private ItemStack tac$prevItemStack = ItemStack.EMPTY;
    @Unique
    private int tac$prevSlot = 0;

    @Shadow private float mainHandHeight;

    @Shadow private float oMainHandHeight;

    @Inject(method = "tick",at = @At("HEAD"))
    public void applyDrawAndHolster(CallbackInfo ci){
        if(Minecraft.getInstance().player == null) return;
        ItemStack mainHandItemStack = Minecraft.getInstance().player.getMainHandItem();
        if(tac$prevItemStack.sameItem(mainHandItemStack)
                && (tac$prevSlot == Minecraft.getInstance().player.getInventory().selected && !CommonStateBox.isSwapped ) )
            return;
        tac$prevItemStack = mainHandItemStack;
        tac$prevSlot = Minecraft.getInstance().player.getInventory().selected;
        CommonStateBox.isSwapped = false;
        AnimationController controller = AnimationHandler.controllers.get(mainHandItemStack.getItem().getRegistryName());
        if(controller != null){
            controller.runAnimation(AnimationHandler.MAIN_TRACK, "draw", ObjectAnimation.PlayType.PLAY_ONCE_HOLD, 0);
            mainHandItem = mainHandItemStack;
        }
    }

    @Inject(method = "tick",at = @At("RETURN"))
    public void cancelEquippedProgress(CallbackInfo ci){
        if(Minecraft.getInstance().player == null) return;
        ItemStack mainHandItemStack = Minecraft.getInstance().player.getMainHandItem();
        if(!(mainHandItemStack.getItem() instanceof GunItem)) return;
        mainHandHeight = 1.0f;
        oMainHandHeight = 1.0f;
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    public void beforeHandRender(float p_109315_, PoseStack p_109316_, MultiBufferSource.BufferSource p_109317_, LocalPlayer p_109318_, int p_109319_, CallbackInfo ci){
        MinecraftForge.EVENT_BUS.post(new BeforeRenderHandEvent(p_109316_));
    }
}
