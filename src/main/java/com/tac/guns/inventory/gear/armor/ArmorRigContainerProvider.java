package com.tac.guns.inventory.gear.armor;

import com.tac.guns.GunMod;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.inventory.gear.armor.implementations.*;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public class ArmorRigContainerProvider implements MenuProvider {

    private ItemStack item;

    private IRigContainer container;
    public ArmorRigContainerProvider(ItemStack item) {
        this.item = item;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("AmmoPack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        ItemStack rig = ((PlayerWithSynData) player).getRig().isEmpty() ? player.getMainHandItem() : ((PlayerWithSynData) player).getRig();
        if(rig.getItem() instanceof ArmorRigItem) {
            int rows = Math.max(item.getOrCreateTag().getInt("rig_rows"), player.getMainHandItem().getOrCreateTag().getInt("rig_rows"));
            switch (rows) {
                case 1:
                    this.container = new R1_RigContainer(windowId, inv, this.item);
                    break;
                case 2:
                    this.container = new R2_RigContainer(windowId, inv, this.item);
                    break;
                case 3:
                    this.container = new R3_RigContainer(windowId, inv, this.item);
                    break;
                case 4:
                    this.container = new R4_RigContainer(windowId, inv, this.item);
                    break;
                case 5:
                    this.container = new R5_RigContainer(windowId, inv, this.item);
                    break;
                default: {
                    this.container = new R1_RigContainer(windowId, inv, this.item);
                    GunMod.LOGGER.log(Level.ERROR, item.getDisplayName().getString()+" | Row could is out of bounds 1-5");
                    break;
                }
            }
        } else return null;
        return container.getSelf();
    }

    public IRigContainer getContainer() {
        return this.container;
    }
}
