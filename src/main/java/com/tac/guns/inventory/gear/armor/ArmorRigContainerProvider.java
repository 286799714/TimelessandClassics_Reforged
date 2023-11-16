package com.tac.guns.inventory.gear.armor;

import com.tac.guns.GunMod;
import com.tac.guns.inventory.gear.armor.implementations.R2_RigContainer;
import com.tac.guns.inventory.gear.armor.implementations.R1_RigContainer;
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
        if(player.getMainHandItem().getItem() instanceof ArmorRigItem) {
            int rows = Math.max(item.getOrCreateTag().getInt("rig_rows"), player.getMainHandItem().getOrCreateTag().getInt("rig_rows"));
            switch (rows) {
                case 1:
                    this.container = new R1_RigContainer(windowId, inv, this.item);
                    break;
                case 2:
                    this.container = new R2_RigContainer(windowId, inv, this.item);
                    break;
                default: {
                    this.container = new R1_RigContainer(windowId, inv, this.item);
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
