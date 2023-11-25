package com.tac.guns.client.handler;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.crosshair.Crosshair;
import com.tac.guns.common.Rig;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageArmorRepair;
import com.tac.guns.network.message.MessageArmorUpdate;
import com.tac.guns.util.WearableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.util.Collections;
import java.util.Set;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ArmorInteractionHandler {
    private static ArmorInteractionHandler instance;

    public static ArmorInteractionHandler get() {
        if (instance == null) {
            instance = new ArmorInteractionHandler();
        }
        return instance;
    }

    private static final double MAX_AIM_PROGRESS = 4;
    // TODO: Only commented, since we may need to track players per client for future third person animation ... private final Map<PlayerEntity, AimTracker> aimingMap = new WeakHashMap<>();
    private double normalisedRepairProgress;
    private int totalPlatesToRepair;
    private boolean repairing = false;
    public boolean getRepairing() {
        return repairing;
    }
    private int repairTime = -1;
    private int prevRepairTime = 0;

    private ArmorInteractionHandler() {
        Keys.ARMOR_REPAIRING.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.ARMOR_REPAIRING))
                return;
            final Minecraft mc = Minecraft.getInstance();
            if(mc.player != null) {
                ItemStack rigStack = WearableHelper.PlayerWornRig(mc.player);
                if (!rigStack.isEmpty() && !WearableHelper.isFullDurability(rigStack)) {
                    Rig rig = ((ArmorRigItem) rigStack.getItem()).getRig();
                    if (rig.getRepair().isQuickRepairable()) {
                        Item repairItem = ForgeRegistries.ITEMS.getValue(rig.getRepair().getItem());
                        if (repairItem == null) {
                            GunMod.LOGGER.log(Level.ERROR, rig.getRepair().getItem() + " | Is not a real / registered item.");
                            return;
                        }
                        var setOfRepairItems = Set.of(repairItem);
                        if (mc.player.getInventory().hasAnyOf(setOfRepairItems)) {
                            this.repairing = true;
                            this.repairTime = rig.getRepair().getTicksToRepair();
                            float rawPlates = (rig.getRepair().getDurability() - WearableHelper.GetCurrentDurability(rigStack)) / (rig.getRepair().getDurability() * rig.getRepair().getQuickRepairability());
                            this.totalPlatesToRepair = rawPlates > (int)rawPlates ? (int)(rawPlates+1) : (int)rawPlates;
                            SyncedEntityData.instance().set(mc.player, ModSyncedDataKeys.QREPAIRING, true);
                        }
                    }
                }
            }
        });
    }

    public float getRepairProgress(Player player) {
        if(WearableHelper.PlayerWornRig(player).isEmpty())
            return 0;
        return this.repairTime > 0 ? ((float)this.repairTime) / (float) ((ArmorRigItem) WearableHelper.PlayerWornRig(player).getItem()).getRig().getRepair().getTicksToRepair() : 1F;
    }
    // Made public so interrupts can simply reset the armor repairing proccess
    public void resetRepairProgress(boolean isAnotherPlateRepairing) {
        if(isAnotherPlateRepairing) {
            this.repairing = true;
            Player player = Minecraft.getInstance().player;
            this.repairTime = ((ArmorRigItem) WearableHelper.PlayerWornRig(player).getItem()).getRig().getRepair().getTicksToRepair();
            SyncedEntityData.instance().set(player, ModSyncedDataKeys.QREPAIRING, true);
            return;
        }
        else
            totalPlatesToRepair = 0;
        this.repairing = false;
        this.repairTime = 0;
        this.prevRepairTime = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        if(WearableHelper.PlayerWornRig(player).isEmpty()) {
            this.repairing = false;
            return;
        }

        if(this.repairing) {
            if(this.repairTime == 0) {
                PacketHandler.getPlayChannel().sendToServer(new MessageArmorRepair());
                totalPlatesToRepair--;
                resetRepairProgress(totalPlatesToRepair > 0);
            } else {
                this.prevRepairTime = this.repairTime;
                this.repairTime--;
            }
            GunMod.LOGGER.log(Level.WARN,  this.repairTime + " | " + totalPlatesToRepair);
        }

    }
}