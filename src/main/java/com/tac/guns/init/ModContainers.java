package com.tac.guns.init;

import com.tac.guns.Reference;
import com.tac.guns.common.container.*;
import com.tac.guns.inventory.gear.armor.implementations.*;
import com.tac.guns.tileentity.UpgradeBenchTileEntity;
import com.tac.guns.tileentity.WorkbenchTileEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

    public static final RegistryObject<MenuType<WorkbenchContainer>> WORKBENCH = register("workbench", (IContainerFactory<WorkbenchContainer>) (windowId, playerInventory, data) -> {
        WorkbenchTileEntity workstation = (WorkbenchTileEntity) playerInventory.player.level.getBlockEntity(data.readBlockPos());
        return new WorkbenchContainer(windowId, playerInventory, workstation);
    });

    public static final RegistryObject<MenuType<AttachmentContainer>> ATTACHMENTS = register("attachments", AttachmentContainer::new);
    
    public static final RegistryObject<MenuType<InspectionContainer>> INSPECTION = register("inspection", InspectionContainer::new);

    public static final RegistryObject<MenuType<ColorBenchContainer>> COLOR_BENCH = register("color_bench", ColorBenchContainer::new);
    public static final RegistryObject<MenuType<UpgradeBenchContainer>> UPGRADE_BENCH = register("upgrade_bench", (IContainerFactory<UpgradeBenchContainer>) (windowId, playerInventory, data) -> {
        UpgradeBenchTileEntity workstation = (UpgradeBenchTileEntity) playerInventory.player.level.getBlockEntity(data.readBlockPos());
        return new UpgradeBenchContainer(windowId, playerInventory, workstation);
    });

    public static final RegistryObject<MenuType<R1_RigContainer>> ARMOR_R1 = register("armor_r1", (IContainerFactory<R1_RigContainer>) (windowId, playerInventory, data) -> {
        return new R1_RigContainer(windowId, playerInventory);
    });
    public static final RegistryObject<MenuType<R2_RigContainer>> ARMOR_R2 = register("armor_r2", (IContainerFactory<R2_RigContainer>) (windowId, playerInventory, data) -> {
        return new R2_RigContainer(windowId, playerInventory);
    });
    public static final RegistryObject<MenuType<R3_RigContainer>> ARMOR_R3 = register("armor_r3", (IContainerFactory<R3_RigContainer>) (windowId, playerInventory, data) -> {
        return new R3_RigContainer(windowId, playerInventory);
    });
    public static final RegistryObject<MenuType<R4_RigContainer>> ARMOR_R4 = register("armor_r4", (IContainerFactory<R4_RigContainer>) (windowId, playerInventory, data) -> {
        return new R4_RigContainer(windowId, playerInventory);
    });
    public static final RegistryObject<MenuType<R5_RigContainer>> ARMOR_R5 = register("armor_r5", (IContainerFactory<R5_RigContainer>) (windowId, playerInventory, data) -> {
        return new R5_RigContainer(windowId, playerInventory);
    });
    // ITEM -> CONTAINER
    /*public static final HashMap<RegistryObject, RegistryObject> containerVitem = new HashMap()
    {{
        put(ARMOR_TEST.get(), ModItems.ARMOR_TEST.get());
    }};*/

    public static void init(){}

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory)
    {
        return REGISTER.register(id, () -> new MenuType<>(factory));
    }
}
