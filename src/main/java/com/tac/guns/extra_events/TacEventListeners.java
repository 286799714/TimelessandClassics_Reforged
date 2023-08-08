package com.tac.guns.extra_events;

import java.util.Locale;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.GunMod;
import com.tac.guns.entity.DamageSourceProjectile;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.event.LevelUpEvent;
import com.tac.guns.init.ModSounds;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.inventory.gear.GearSlotsHandler;
import com.tac.guns.item.TransitionalTypes.M1GunItem;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.tileentity.UpgradeBenchTileEntity;
import com.tac.guns.util.WearableHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class will be used for all shooting events that I will utilise.
 * The gun mod provides 3 events for firing guns check out {@link com.mrcrayfish.guns.event.GunFireEvent} for what they are
 */


import com.tac.guns.Config;
import com.tac.guns.Reference;
import com.tac.guns.common.Gun;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.item.GunItem;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;

import static com.tac.guns.inventory.gear.InventoryListener.ITEM_HANDLER_CAPABILITY;


/**
 * Author: ClumsyAlien
 */


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TacEventListeners {

    /*
        A bit decent bit of extra code will be locked in external methods such as this, separating some of the standard and advanced
        Functions, especially in order to keep it all clean and allow easy backtracking, however both functions may receive changes
        For now as much of the work I can do will be kept externally such as with fire selection, and burst fire.
        (In short this serves as a temporary test bed to keep development on new functions on course)
    */

    private static boolean checked = true;
    private static boolean confirmed = false;
    private static VersionChecker.CheckResult status;
    @SubscribeEvent
    public static void InformPlayerOfUpdate(EntityJoinWorldEvent e)
    {
        try {
            if(!(e.getEntity() instanceof PlayerEntity))
                return;

            if (checked) {
                if (GunMod.modInfo != null) {
                    status = VersionChecker.getResult(GunMod.modInfo);
                    checked = false;
                }
            }
            if (!confirmed) {
                if (status.status == VersionChecker.Status.OUTDATED || status.status == VersionChecker.Status.BETA_OUTDATED) {
                    ((PlayerEntity) e.getEntity()).sendStatusMessage(new TranslationTextComponent("updateCheck.tac", status.target, status.url), false);
                    confirmed = true;
                }
            }
        }
        catch(Exception ev)
        {
            GunMod.LOGGER.log(Level.ERROR, ev.getMessage());
            return;
        }
        GunMod.LOGGER.log(Level.INFO, status.status);
    }

    @SubscribeEvent
    public void onPartialLevel(LevelUpEvent.Post event)
    {
        PlayerEntity player = event.getPlayer();
        event.getPlayer().getEntityWorld().playSound(player, player.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.experience_orb.pickup")), SoundCategory.PLAYERS,4.0F, 1.0F);
    }

    // TODO: remaster method to play empty fire sound on most-all guns
    /* BTW this was by bomb787 as a Timeless Contributor */
    @SubscribeEvent
    public static void postShoot(GunFireEvent.Post event) {
        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        if(!(heldItem.getItem() instanceof M1GunItem))
            return;
        CompoundNBT tag = heldItem.getTag();
        if(tag != null)
        {
            if(tag.getInt("AmmoCount") == 1)
                event.getPlayer().getEntityWorld().playSound(player, player.getPosition(), ModSounds.M1_PING.get()/*.GARAND_PING.get()*/, SoundCategory.MASTER, 3.0F, 1.0F);
        }
    }

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handleDeathWithArmor(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if(WearableHelper.PlayerWornRig(player) != null)
            {
                GearSlotsHandler ammoItemHandler = (GearSlotsHandler) player.getCapability(ITEM_HANDLER_CAPABILITY).resolve().get();
                Block.spawnAsEntity(player.world, player.getPosition(), (ammoItemHandler.getStackInSlot(0)));
                Block.spawnAsEntity(player.world, player.getPosition(), (ammoItemHandler.getStackInSlot(1)));
            }
        }
        // TODO: Continue for dropping armor on a bot's death
    }*/

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Vector3d p0 = ProjectileEntity.cachePlayerPosition.getOrDefault(event.player, event.player.getPositionVec());
            Vector3d p1 = event.player.getPositionVec();
            ProjectileEntity.cachePlayerPosition.put(event.player, p1);
            Vector3d v = p1.subtract(p0);
            if (v.x * v.x + v.y * v.y + v.z * v.z >= 0.0625) {
                v = v.mul(5,5,5);
            } else {
                v = new Vector3d(0,0,0);
            }
            ProjectileEntity.cachePlayerVelocity.put(event.player, v);
        }
    }

}
