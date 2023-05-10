package com.tac.guns.common.container.slot;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.common.Gun;
import com.tac.guns.common.ReloadTracker;
import com.tac.guns.common.container.AttachmentContainer;
import com.tac.guns.init.ModSounds;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.IrDeviceItem;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.SideRailItem;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentSlot extends Slot
{
    private AttachmentContainer container;
    private ItemStack weapon;
    private IAttachment.Type type;
    private PlayerEntity player;
    private IAttachment.Type[] types;
    private int index;

    public AttachmentSlot(AttachmentContainer container, IInventory weaponInventory, ItemStack weapon, IAttachment.Type type, PlayerEntity player, int index, int x, int y)
    {
        super(weaponInventory, index, x, y);
        this.container = container;
        this.weapon = weapon;
        this.type = type;
        this.player = player;
        this.index = index;
    }

    public AttachmentSlot(AttachmentContainer container, IInventory weaponInventory, ItemStack weapon, IAttachment.Type[] types, PlayerEntity player, int index, int x, int y)
    {
        super(weaponInventory, index, x, y);
        this.container = container;
        this.weapon = weapon;
        this.types = types;
        this.player = player;
        this.index = index;
    }

    @Override
    public boolean isEnabled()
    {
        this.weapon.inventoryTick(player.world, player, index, true);
        if((this.type == IAttachment.Type.EXTENDED_MAG && this.weapon.getOrCreateTag().getInt("AmmoCount") > ((TimelessGunItem)this.weapon.getItem()).getGun().getReloads().getMaxAmmo()) || SyncedPlayerData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
            return false;
        }
        if(this.player.getHeldItemMainhand().getItem() instanceof ScopeItem || this.player.getHeldItemMainhand().getItem() instanceof SideRailItem || this.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem)
        {
            return true;
        }
        else
        {
            GunItem item = (GunItem) this.weapon.getItem();
            Gun modifiedGun = item.getModifiedGun(this.weapon);
            if(modifiedGun.canAttachType(this.type))
                return true;
            else if(types != null)
            {
                for (IAttachment.Type x : types) {
                    if(modifiedGun.canAttachType(x))
                        return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if((this.type == IAttachment.Type.EXTENDED_MAG && this.weapon.getOrCreateTag().getInt("AmmoCount") > ((TimelessGunItem)this.weapon.getItem()).getGun().getReloads().getMaxAmmo()) || SyncedPlayerData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
            return false;
        }
        if(this.player.getHeldItemMainhand().getItem() instanceof ScopeItem || this.player.getHeldItemMainhand().getItem() instanceof SideRailItem || this.player.getHeldItemMainhand().getItem() instanceof IrDeviceItem)
            if(stack.getItem() instanceof DyeItem)
                return true;
            else
                return false;
        else
        {
            GunItem item = (GunItem) this.weapon.getItem();
            Gun modifiedGun = item.getModifiedGun(this.weapon);
            if (stack.getItem() instanceof IAttachment && ((IAttachment) stack.getItem()).getType() == this.type && modifiedGun.canAttachType(this.type))
                return true;
            else if (types != null && stack.getItem() instanceof IAttachment) {
                for (IAttachment.Type x : types) {
                    if (((IAttachment) stack.getItem()).getType() == x)
                        return true;
                }
            }
            return false;
        }
    }

    @Override
    public void onSlotChanged()
    {
        if(this.container.isLoaded())
        {
            this.player.world.playSound(null, this.player.getPosX(), this.player.getPosY() + 1.0, this.player.getPosZ(), ModSounds.UI_WEAPON_ATTACH.get(), SoundCategory.PLAYERS, 0.5F, this.getHasStack() ? 1.0F : 0.75F);
        }
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public boolean canTakeStack(PlayerEntity player)
    {
        if(this.weapon.getOrCreateTag().getInt("AmmoCount") > ((TimelessGunItem)this.weapon.getItem()).getGun().getReloads().getMaxAmmo() || SyncedPlayerData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
            return false;
        } else
            return true;
    }
}