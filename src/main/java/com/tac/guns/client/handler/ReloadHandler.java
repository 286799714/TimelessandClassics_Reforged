package com.tac.guns.client.handler;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PumpShotgunAnimationController;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.event.GunReloadEvent;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageReload;
import com.tac.guns.network.message.MessageUnload;
import com.tac.guns.network.message.MessageUpdateGunID;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.WearableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.simple.SimpleChannel;


/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
/*public class ReloadHandler
{
    private static ReloadHandler instance;

    public static ReloadHandler get()
    {
        if(instance == null)
        {
            instance = new ReloadHandler();
        }
        return instance;
    }

    private int startReloadTick;
    private int reloadTimer;
    private int prevReloadTimer;
    private int reloadingSlot;

    private ReloadHandler()
    {
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        this.prevReloadTimer = this.reloadTimer;

        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            if(SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING))
            {
                if(this.reloadingSlot != player.getInventory().currentItem)
                {
                    this.setReloading(false);
                }
            }

            this.updateReloadTimer(player);
        }
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event)
    {
        if(Minecraft.getInstance().player == null)
        {
            return;
        }

        if(KeyBinds.KEY_RELOAD.isKeyDown() && event.getAction() == GLFW.GLFW_PRESS)
        {
            if(!SyncedEntityData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.RELOADING))
            {
                this.setReloading(true);
            }
            else
            {
                this.setReloading(false);
            }
        }
        if(KeyBinds.KEY_UNLOAD.isPressed() && event.getAction() == GLFW.GLFW_PRESS)
        {
            this.setReloading(false);
            PacketHandler.getPlayChannel().sendToServer(new MessageUnload());
        }
    }

    public void setReloading(boolean reloading)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            if(reloading)
            {
                ItemStack stack = player.getHeldItemMainhand();
                if(stack.getItem() instanceof GunItem)
                {
                    CompoundNBT tag = stack.getTag();
                    if(tag != null && !tag.contains("IgnoreAmmo", Constants.NBT.TAG_BYTE))
                    {
                        Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
                        if(tag.getInt("AmmoCount") >= GunEnchantmentHelper.getAmmoCapacity(stack, gun))
                        {
                            return;
                        }
                        if(Gun.findAmmo(player, gun.getProjectile().getItem()).isEmpty())
                        {
                            return;
                        }
                        if(MinecraftForge.EVENT_BUS.post(new GunReloadEvent.Pre(player, stack)))
                            return;
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, true);
                        PacketHandler.getPlayChannel().sendToServer(new MessageReload(true));
                        this.reloadingSlot = player.getInventory().currentItem;
                        MinecraftForge.EVENT_BUS.post(new GunReloadEvent.Post(player, stack));
                    }
                }
            }
            else
            {
                SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, false);
                PacketHandler.getPlayChannel().sendToServer(new MessageReload(false));
                this.reloadingSlot = -1;
            }
        }
    }

    private void updateReloadTimer(PlayerEntity player)
    {
        if(SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING))
        {
            if(this.startReloadTick == -1)
            {
                this.startReloadTick = player.ticks + 5;
            }
            if(this.reloadTimer < 5)
            {
                this.reloadTimer++;
            }
        }
        else
        {
            if(this.startReloadTick != -1)
            {
                this.startReloadTick = -1;
            }
            if(this.reloadTimer > 0)
            {
                this.reloadTimer--;
            }
        }
    }

    public int getStartReloadTick()
    {
        return this.startReloadTick;
    }

    public int getReloadTimer()
    {
        return this.reloadTimer;
    }

    public float getRepairProgress(float partialTicks)
    {
        return (this.prevReloadTimer + (this.reloadTimer - this.prevReloadTimer) * partialTicks) / 5F;
    }
}*/
public class ReloadHandler {
    private static ReloadHandler instance;

    public static ReloadHandler get() {
        if (instance == null) {
            instance = new ReloadHandler();
        }
        return instance;
    }

    private int startReloadTick;
    private int reloadTimer;
    private int prevReloadTimer;
    private int reloadingSlot;

    private int startUpReloadTimer;
    private boolean empty;
    private boolean prevState = false;
    private ItemStack prevItemStack;

    public int rigAmmoCount = 0;

    private ReloadHandler() { }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        this.prevReloadTimer = this.reloadTimer;

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
                if (this.reloadingSlot != player.getInventory().selected) {
                    this.setReloading(false);
                }
            }
            this.updateReloadTimer(player);
            PacketHandler.getPlayChannel().sendToServer(new MessageUpdateGunID());
        }
    }

    private boolean isInGame() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getOverlay() != null)
            return false;
        if (mc.screen != null)
            return false;
        if (!mc.mouseHandler.isMouseGrabbed())
            return false;
        return mc.isWindowActive();
    }
    /*@SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event)
    {
        if(!isInGame())
            return;
        PlayerEntity player = event.player;
        if(player == null)
            return;

    }*/

    public void setReloading(boolean reloading) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (reloading) {
                ItemStack stack = player.getMainHandItem();
                prevItemStack = stack;
                if (stack.getItem() instanceof GunItem) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null && !tag.contains("IgnoreAmmo", Tag.TAG_BYTE)) {
                        Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
                        if (tag.getInt("AmmoCount") >= GunModifierHelper.getAmmoCapacity(stack, gun)) {
                            return;
                        }
                        ItemStack rig = WearableHelper.PlayerWornRig(player);
                        if (!player.isCreative() && !rig.isEmpty()) {
                            if (Gun.findAmmo(player, gun.getProjectile().getItem()).length < 1 && rigAmmoCount < 1) {
                                return;
                            }
                        } else if (!player.isCreative() && Gun.findAmmo(player, gun.getProjectile().getItem()).length < 1) {
                            return;
                        }
                        if (MinecraftForge.EVENT_BUS.post(new GunReloadEvent.Pre(player, stack)))
                            return;
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, true);
                        PacketHandler.getPlayChannel().sendToServer(new MessageReload(true));
                        AnimationHandler.INSTANCE.onGunReload(true, stack);
                        this.reloadingSlot = player.getInventory().selected;
                        MinecraftForge.EVENT_BUS.post(new GunReloadEvent.Post(player, stack));
                    }
                }
            } else {
                if (prevItemStack != null) AnimationHandler.INSTANCE.onGunReload(false, prevItemStack);
                SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, false);
                PacketHandler.getPlayChannel().sendToServer(new MessageReload(false));
                this.reloadingSlot = -1;
            }
        }
    }

    private void updateReloadTimer(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
            prevState = true;
            if (stack.getItem() instanceof GunItem) {
                CompoundTag tag = stack.getTag();
                if (tag != null) {
                    Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
                    if (this.startUpReloadTimer == -1)
                        this.startUpReloadTimer = gun.getReloads().getPreReloadPauseTicks();

                    if (gun.getReloads().isMagFed()) {
                        if (this.startUpReloadTimer == 0) {
                            if (this.startReloadTick == -1) {
                                this.startReloadTick = player.tickCount + 5;
                            }
                            if (tag.getInt("AmmoCount") <= 0) {
                                if (this.reloadTimer < gun.getReloads().getReloadMagTimer() + gun.getReloads().getAdditionalReloadEmptyMagTimer()) {
                                    this.reloadTimer++;
                                }
                            } else {
                                if (this.reloadTimer < gun.getReloads().getReloadMagTimer()) {
                                    this.reloadTimer++;
                                }
                            }
                        } else
                            this.startUpReloadTimer--;
                    } else {
                        if (this.startUpReloadTimer == 0) {
                            if (this.startReloadTick == -1) {
                                this.startReloadTick = player.tickCount + 5;
                            }
                            if (this.reloadTimer < gun.getReloads().getinterReloadPauseTicks()) {
                                if (!AnimationHandler.INSTANCE.isReloadingIntro(prevItemStack.getItem()))
                                    this.reloadTimer++;
                            }
                            if (this.reloadTimer == gun.getReloads().getinterReloadPauseTicks()) {
                                AnimationHandler.INSTANCE.onReloadLoop(prevItemStack.getItem());
                                this.reloadTimer = 0;
                            }
                        } else
                            this.startUpReloadTimer--;
                    }
                }
            }
        } else {
            if (prevState) {
                prevState = false;
                AnimationHandler.INSTANCE.onReloadEnd(prevItemStack.getItem());
            }
            if (stack.getItem() instanceof GunItem) {
                Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
                if (gun.getReloads().isMagFed()) {
                    if (this.startReloadTick != -1) {
                        this.startReloadTick = -1;
                    }
                    if (this.reloadTimer > 0) {
                        this.reloadTimer = 0;
                    }
                } else {
                    if (this.startReloadTick != -1) {
                        this.startReloadTick = -1;
                    }
                    if (this.reloadTimer > 0) {
                        this.reloadTimer--;
                    }
                }
            } else {
                if (this.startReloadTick != -1) {
                    this.startReloadTick = -1;
                }
                if (this.reloadTimer > 0) {
                    this.reloadTimer = 0;
                }
            }

        }
    }

    public int getStartReloadTick() {
        return this.startReloadTick;
    }

    public int getReloadTimer() {
        return this.reloadTimer;
    }

    public int getStartUpReloadTimer() {
        return this.startUpReloadTimer;
    }

    public boolean isReloading() {
        return this.startReloadTick != -1;
    }

    public float getReloadProgress(float partialTicks, ItemStack stack) {
        boolean isEmpty = false;
        GunItem gunItem = (GunItem) stack.getItem();
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            isEmpty = tag.getInt("AmmoCount") <= 0;
        }
        return this.startUpReloadTimer == 0 ? (gunItem.getGun().getReloads().isMagFed() ? (isEmpty ? ((this.prevReloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks) + this.startUpReloadTimer) / ((float) gunItem.getGun().getReloads().getReloadMagTimer() + gunItem.getGun().getReloads().getAdditionalReloadEmptyMagTimer())) : ((this.prevReloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks) + this.startUpReloadTimer) / (float) gunItem.getGun().getReloads().getReloadMagTimer())) : ((this.reloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks)) / ((float) gunItem.getGun().getReloads().getinterReloadPauseTicks()))
        )
                : 1F;
    }

    @SubscribeEvent
    public void onGunFire(GunFireEvent.Pre event) {
        Player player = event.getPlayer();
        if (player == null) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem))
            return; // Fails on server instances where all plays must be holding a gun
        Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
        if (GunAnimationController.fromItem(stack.getItem()) instanceof PumpShotgunAnimationController && isReloading())
            event.setCanceled(true);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt("AmmoCount") <= 0) {
            if (gun.getReloads().getReloadMagTimer() + gun.getReloads().getAdditionalReloadEmptyMagTimer() - reloadTimer > 5) {
                if (isReloading()) event.setCanceled(true);
            }
        } else {
            if (gun.getReloads().getReloadMagTimer() - reloadTimer > 5) {
                if (isReloading()) event.setCanceled(true);
            }
        }
    }

    /*public float getRepairProgress(float partialTicks, ItemStack stack)
    {
        boolean isEmpty = false;
        GunItem gunItem = (GunItem)stack.getItem();
        CompoundNBT tag = stack.getTag();
        if(tag != null)
        {
            isEmpty=tag.getInt("AmmoCount")<=0;
        }
        return this.startUpReloadTimer == 0 ?
                (
                        gunItem.getGun().getReloads().isMagFed() ?
                                (isEmpty ? ((this.prevReloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks) + this.startUpReloadTimer) / ((float) gunItem.getGun().getReloads().getReloadMagTimer() + gunItem.getGun().getReloads().getAdditionalReloadEmptyMagTimer())) : ((this.prevReloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks) + this.startUpReloadTimer) / (float) gunItem.getGun().getReloads().getReloadMagTimer()))
                                : ((this.prevReloadTimer + ((this.reloadTimer - this.prevReloadTimer) * partialTicks) + this.startUpReloadTimer) / ((float) gunItem.getGun().getReloads().getinterReloadPauseTicks()) )
                )
                : 1F;
    }*/

    //public boolean isReloading()
    //{
    //    return this.startReloadTick != -1;
    //}
}