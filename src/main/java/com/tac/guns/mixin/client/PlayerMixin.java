package com.tac.guns.mixin.client;

import com.mojang.authlib.GameProfile;
import com.tac.guns.duck.PlayerWithSynData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements net.minecraftforge.common.extensions.IForgePlayer, PlayerWithSynData {
    private static final EntityDataAccessor<ItemStack> RIG_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;FLcom/mojang/authlib/GameProfile;)V", at = @At("RETURN"))
    public void defineSynData(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_, CallbackInfo ci){
        this.entityData.define(RIG_ID, ItemStack.EMPTY);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    public void saveData(CompoundTag p_36265_, CallbackInfo ci){
        p_36265_.put("TacRig", this.entityData.get(RIG_ID).save(new CompoundTag()));
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    public void readData(CompoundTag p_36215_, CallbackInfo ci){
        CompoundTag rigTag = p_36215_.getCompound("TacRig");
        if (!rigTag.isEmpty()) {
            this.entityData.set(RIG_ID, ItemStack.of(rigTag));
        }
    }

    @Override
    public EntityDataAccessor<ItemStack> getRigDataAccessor() {
        return RIG_ID;
    }

    @Override
    public ItemStack getRig(){
        return this.getEntityData().get(RIG_ID);
    }

    @Override
    public void setRig(ItemStack itemStack){
        this.getEntityData().set(RIG_ID, itemStack);
    }
}
