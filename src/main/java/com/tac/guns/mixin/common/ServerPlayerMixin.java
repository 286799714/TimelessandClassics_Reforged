package com.tac.guns.mixin.common;

import com.mojang.authlib.GameProfile;
import com.tac.guns.duck.PlayerWithSynData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) {
        super(p_36114_, p_36115_, p_36116_, p_36117_);
    }

    @Inject(method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;replaceWith(Lnet/minecraft/world/entity/player/Inventory;)V"))
    public void restoreRig(ServerPlayer p_9016_, boolean p_9017_, CallbackInfo ci){
        ((PlayerWithSynData)this).setRig(((PlayerWithSynData)p_9016_).getRig());
        ((PlayerWithSynData)p_9016_).setRig(ItemStack.EMPTY);
    }
}
