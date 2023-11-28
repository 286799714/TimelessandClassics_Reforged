package com.tac.guns.util;

import com.tac.guns.GunMod;
import com.tac.guns.common.Rig;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageGunSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Set;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class WearableHelper
{
    // Helpers, to maintain speed and efficency, we need to check if the tag is populated BEFORE running the helper methods

    @Nonnull
    public static ItemStack PlayerWornRig(Player player)
    {
        return ((PlayerWithSynData)player).getRig();
    }

    public static void FillDefaults(ItemStack item, Rig rig)
    {
        item.getOrCreateTag().putFloat("RigDurability", RigEnchantmentHelper.getModifiedDurability(item, rig));
    }

    /**
     * @param rig The ItemStack for armor
     * @return true if the armor is at full durability
     */
    public static boolean isFullDurability(ItemStack rig)
    {
        Rig modifiedRig = ((ArmorRigItem)rig.getItem()).getModifiedRig(rig);
        float max = RigEnchantmentHelper.getModifiedDurability(rig, modifiedRig);

        return rig.getOrCreateTag().getFloat("RigDurability") >= max;
    }

    /**
     * @param player The player hit
     * @param proj The projectile hitting {player}
     * @return True if the bullet goes straight through armor
     */
    public static boolean tickFromCurrentDurability(Player player, ProjectileEntity proj)
    {
        ItemStack rig = PlayerWornRig(player);
        float og = rig.getOrCreateTag().getFloat("RigDurability");
        rig.getOrCreateTag().remove("RigDurability");

        if(og == 0) {
            ((PlayerWithSynData) player).updateRig();
            return true;
        }
        if(og - proj.getDamage() > 0) {
            rig.getOrCreateTag().putFloat("RigDurability", og - proj.getDamage());
            ((PlayerWithSynData) player).updateRig();
        }
        else if (og - proj.getDamage() < 0) {
            ResourceLocation brokenSound = ((ArmorRigItem)rig.getItem()).getRig().getSounds().getBroken();
            if (brokenSound != null) {
                MessageGunSound messageSound = new MessageGunSound(brokenSound, SoundSource.PLAYERS, (float) player.getX(), (float) (player.getY() + 1.0), (float) player.getZ(), 1.5F, 1F, player.getId(), false, false);
                PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), messageSound);
            }
            rig.getOrCreateTag().putFloat("RigDurability", 0);
            ((PlayerWithSynData) player).updateRig();
            return false;
        }
        ((PlayerWithSynData) player).updateRig();
        return false;
    }

    public static float currentDurabilityPercentage(ItemStack rig) {
        return rig.getOrCreateTag().getFloat("RigDurability")/((ArmorRigItem) rig.getItem()).getRig().getRepair().getDurability();
    }

    /**
     * @param rig The ItemStack for armor
     * @return true if the armor is fully repaired, false if armor only got ticked and not at max
     */
    public static boolean tickRepairCurrentDurability(ItemStack rig)
    {
        Rig modifiedRig = ((ArmorRigItem)rig.getItem()).getModifiedRig(rig);
        float og = rig.getOrCreateTag().getFloat("RigDurability"),
                max = RigEnchantmentHelper.getModifiedDurability(rig, modifiedRig),
                ofDurability = modifiedRig.getRepair().getQuickRepairability();

        rig.getOrCreateTag().remove("RigDurability");
        float totalAfterRepair = og + (max * ofDurability);
        if(totalAfterRepair >= max) {
            rig.getOrCreateTag().putFloat("RigDurability", max);
        }
        else{
            rig.getOrCreateTag().putFloat("RigDurability", totalAfterRepair);
            return true;
        }
        return false;
    }
    /**
     * @param rig The Itemstack for armor, I don't want helpers to view through static capability's
     * @param repair The percentage to repair off the armor, can be used for custom methods, healing stations, ETC.
     * @return true if the armor is fully repaired, false if armor only got ticked and not at max
     * @IMPORTANT ||| ((PlayerWithSynData) player).updateRig(); //update the rig so clientside gets updated values
     */
    public static boolean tickRepairCurrentDurability(ItemStack rig, float repair)
    {
        Rig modifiedRig = ((ArmorRigItem)rig.getItem()).getModifiedRig(rig);
        float og = rig.getOrCreateTag().getFloat("RigDurability"),
                max = RigEnchantmentHelper.getModifiedDurability(rig, modifiedRig),
                ofDurability = repair;

        rig.getOrCreateTag().remove("RigDurability");
        float totalAfterRepair = og + (max * ofDurability);
        if(og >= max) {
            rig.getOrCreateTag().putFloat("RigDurability", max);
        }
        else{
            rig.getOrCreateTag().putFloat("RigDurability", totalAfterRepair);
            return true;
        }
        return false;
    }

    public static boolean consumeRepairItem(Player player, ItemStack rig) {
        Item repairItem = ForgeRegistries.ITEMS.getValue(((ArmorRigItem) rig.getItem()).getRig().getRepair().getItem());
        if(repairItem == null) {
            GunMod.LOGGER.log(Level.ERROR, ((ArmorRigItem) rig.getItem()).getRig().getRepair().getItem()+" | Is not a real / registered item.");
            return false;
        }
        int loc = -1;
        for(int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(!stack.isEmpty() && stack.getItem().getRegistryName().equals(((ArmorRigItem) rig.getItem()).getRig().getRepair().getItem())) {
                loc = i;
            }
        }
        if(loc > -1) {
            player.getInventory().removeItem(loc, 1);
            return true;
        }
        else {
            GunMod.LOGGER.log(Level.WARN, ((ArmorRigItem) rig.getItem()).getRig().getRepair().getItem()+" | Not found anymore in {" + player.getDisplayName().getString() + "} inventory");
            return false;
        }

    }

    public static boolean consumeRepairItem(Player player) {
        ItemStack rig = WearableHelper.PlayerWornRig(player);
        if(rig.isEmpty())
            return false;
        return consumeRepairItem(player, rig);
    }

    public static float GetCurrentDurability(ItemStack item) {
        return item.getOrCreateTag().getFloat("RigDurability");
    }
}
