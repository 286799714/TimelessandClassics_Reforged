package com.tac.guns.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.tac.guns.common.ItemStackWrapper;
import com.tac.guns.common.network.RigItemStackDataSerializer;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.checkerframework.common.reflection.qual.Invoke;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements net.minecraftforge.common.extensions.IForgePlayer, PlayerWithSynData {
    private static final EntityDataAccessor<ItemStackWrapper> RIG_ID = SynchedEntityData.defineId(Player.class, RigItemStackDataSerializer.INSTANCE);

    @Shadow
    private Inventory inventory;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;FLcom/mojang/authlib/GameProfile;)V", at = @At("RETURN"))
    public void defineSynData(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_, CallbackInfo ci){
        this.entityData.define(RIG_ID, new ItemStackWrapper(ItemStack.EMPTY));
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    public void saveData(CompoundTag p_36265_, CallbackInfo ci){
        ItemStack rig = this.entityData.get(RIG_ID).itemStack();
        if(!rig.isEmpty()) {
            p_36265_.put("TacRig", rig.save(new CompoundTag()));
        }
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    public void readData(CompoundTag p_36215_, CallbackInfo ci){
        CompoundTag rigTag = p_36215_.getCompound("TacRig");
        if (!rigTag.isEmpty()) {
            this.entityData.set(RIG_ID, new ItemStackWrapper(ItemStack.of(rigTag)));
        }
    }

    @Inject(method = "dropEquipment()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    public void dropRig(CallbackInfo ci){
        ItemStack rig = this.entityData.get(RIG_ID).itemStack();
        if(!rig.isEmpty()) {
            this.inventory.player.drop(rig, true, false);
            setRig(ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack getRig(){
        return this.getEntityData().get(RIG_ID).itemStack();
    }

    @Override
    public void setRig(ItemStack newRig){
        this.getEntityData().set(RIG_ID, new ItemStackWrapper(newRig));
    }

    @Override
    public void updateRig(){
        ItemStack rig = getRig();
        if(rig.getItem() instanceof ArmorRigItem) {
            RigSlotsHandler itemHandler = (RigSlotsHandler) rig.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            if (rig.getTag() != null) rig.getTag().put("storage", itemHandler.serializeNBT());
            this.getEntityData().set(RIG_ID, new ItemStackWrapper(rig));
        }
    }
}
