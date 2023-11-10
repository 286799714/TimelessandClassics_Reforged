package com.tac.guns.datagen;

import com.tac.guns.crafting.WorkbenchRecipeBuilder;
import com.tac.guns.init.ModBlocks;
import com.tac.guns.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

//@OnlyIn(Dist.DEDICATED_SERVER)
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        // Dye Item
        /*if(Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER)
            return;*/
        /*consumer.accept(new IFinishedRecipe() {
            @Override
            public void serialize(JsonObject json) {
            }

            @Override
            public IRecipeSerializer<?> getSerializer() {
                return ModRecipeSerializers.DYE_ITEM.get();
            }

            @Override
            public ResourceLocation getID() {
                return new ResourceLocation(Reference.MOD_ID, "dye_item");
            }

            @Override
            @Nullable
            public JsonObject getAdvancementJson() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementID() {
                return new ResourceLocation("");
            }
        });*/

        ShapedRecipeBuilder.shaped(ModBlocks.WORKBENCH.get())
                .pattern("###")
                .pattern("iIi")
                .pattern("i i")
                .define('#', ItemTags.LOGS)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.STORAGE_BLOCKS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.LIGHT_ARMOR_REPAIR_PLATE.get())
                .pattern("# #")
                .pattern("III")
                .pattern("iIi")
                .define('#', Tags.Items.LEATHER)
                .define('I', Tags.Items.NUGGETS_IRON)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.STORAGE_BLOCKS_IRON))
                .save(consumer);
/*
        ShapedRecipeBuilder.shaped(ModItems.LIGHT_ARMOR.get())
                .pattern("# #")
                .pattern("iIi")
                .pattern("iii")
                .define('#', Tags.Items.LEATHER)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.STORAGE_BLOCKS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.MEDIUM_STEEL_ARMOR.get())
                .pattern("i i")
                .pattern("III")
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.STORAGE_BLOCKS_IRON))
                .save(consumer);
 */

        ShapedRecipeBuilder.shaped(ModItems.MODULE.get(), 3)
                .pattern("#I#")
                .pattern("D#D")
                .pattern("GDG")
                .define('#', Tags.Items.GLASS)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(consumer);

        //Gun
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.AI_AWP.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 150)
                .addIngredient(Tags.Items.INGOTS_GOLD, 50)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 10)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M24.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 80)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 5)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.AK47.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 45)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.DEAGLE_357.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 48)
                .addIngredient(Tags.Items.INGOTS_GOLD, 12)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.DP28.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 62)
                .addIngredient(Tags.Items.LEATHER, 12)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.RPK.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 54)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.GLOCK_17.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 22)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.TTI_G34.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 38)
                .addIngredient(Tags.Items.INGOTS_GOLD, 4)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.CZ75_AUTO.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 36)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.CZ75.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 26)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.HK416_A5.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 80)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.HK_MP5A5.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 34)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.VECTOR45.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 50)
                .addIngredient(Tags.Items.INGOTS_GOLD, 6)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MICRO_UZI.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 30)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M4.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 60)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M60.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 84)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M870_CLASSIC.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 40)
                .addIngredient(Tags.Items.LEATHER, 8)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.DB_SHORT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 12)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M1014.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 64)
                .addIngredient(Tags.Items.INGOTS_GOLD, 18)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.AA_12.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 80)
                .addIngredient(Tags.Items.INGOTS_GOLD, 20)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M1911.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 20)
                .addIngredient(Tags.Items.LEATHER, 3)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MK14.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 128)
                .addIngredient(Tags.Items.INGOTS_GOLD, 20)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 5)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MK18_MOD1.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 76)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 3)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MK47.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 100)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.FN_FAL.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 92)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MP7.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 66)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.QBZ_95.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 62)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.M16A4.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 68)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SCAR_H.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 128)
                .addIngredient(Tags.Items.INGOTS_GOLD, 24)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 4)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SCAR_L.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 72)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SPR_15.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 90)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 2)
                .build(consumer);
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SKS_TACTICAL.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 70)
                .addIngredient(Tags.Items.INGOTS_GOLD, 10)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.STI2011.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 36)
                .addIngredient(Tags.Items.INGOTS_GOLD, 4)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.TYPE81_X.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 48)
                .build(consumer);

        //Ammo
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.RPG7_MISSILE.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 32)
                .addIngredient(Tags.Items.GUNPOWDER, 8)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_10g.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.NUGGETS_IRON, 12)
                .addIngredient(Tags.Items.GUNPOWDER, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_9.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_30_WIN.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 3)
                .addIngredient(Tags.Items.GUNPOWDER, 4)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_45.get(), 16)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_50_BMG.get(), 5)
                .addIngredient(Tags.Items.INGOTS_IRON, 5)
                .addIngredient(Tags.Items.GUNPOWDER, 10)
                .addIngredient(Tags.Items.GEMS_DIAMOND, 1)
                .build(consumer);

        /*WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MAGNUM_BULLET.get(), 6)
                .addIngredient(Tags.Items.NUGGETS_IRON, 12)
                .addIngredient(Tags.Items.GUNPOWDER, 2)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .build(consumer);*/

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_58x42.get(), 22)
                .addIngredient(Tags.Items.INGOTS_IRON, 2)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_68.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 2)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_308.get(), 16)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.GUNPOWDER, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_556.get(), 30)
                .addIngredient(Tags.Items.INGOTS_IRON, 2)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_762x25.get(), 24)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_762x39.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 10)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_762x54.get(), 10)
                .addIngredient(Tags.Items.INGOTS_IRON, 1)
                .addIngredient(Tags.Items.GUNPOWDER, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_50AE.get(), 20)
                .addIngredient(Tags.Items.INGOTS_IRON, 3)
                .addIngredient(Tags.Items.GUNPOWDER, 3)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BULLET_LAPUA338.get(), 10)
                .addIngredient(Tags.Items.INGOTS_IRON, 3)
                .addIngredient(Tags.Items.GUNPOWDER, 5)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.BASEBALL_GRENADE.get(), 1)
                .addIngredient(Tags.Items.INGOTS_IRON, 6)
                .addIngredient(Tags.Items.GUNPOWDER, 3)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.LIGHT_GRENADE.get(), 2)
                .addIngredient(Tags.Items.INGOTS_IRON, 4)
                .addIngredient(Tags.Items.GUNPOWDER, 2)
                .build(consumer);

        //Scope
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.STANDARD_6_10x_SCOPE.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 36)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 60)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.ACOG_4.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 28)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 24)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.AIMPOINT_T2_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 18)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 4)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.AIMPOINT_T1_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 10)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 4)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.COYOTE_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 20)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 6)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.ELCAN_DR_14X.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 30)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 26)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.EOTECH_N_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 16)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 10)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.EOTECH_SHORT_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 20)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 14)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

/*        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MICRO_HOLO_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 7)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 4)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);*/

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.VORTEX_LPVO_3_6.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 32)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 24)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MINI_DOT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 8)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 4)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SRO_DOT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 16)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 12)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.OLD_LONGRANGE_4x_SCOPE.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 20)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 32)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.OLD_LONGRANGE_8x_SCOPE.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 24)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 48)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.QMK152.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 24)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 20)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 2)
                .build(consumer);

        /*WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SLX_2X.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 15)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 10)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);*/

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SRS_RED_DOT_SIGHT.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 16)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 2)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.VORTEX_UH_1.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 18)
                .addIngredient(Tags.Items.GLASS_PANES_COLORLESS, 12)
                .addIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 1)
                .build(consumer);

        //Grip
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.LIGHT_GRIP.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 6)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SPECIALISED_GRIP.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 12)
                .addIngredient(Tags.Items.LEATHER, 2)
                .build(consumer);

        //Stock
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.LIGHT_STOCK.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 12)
                .addIngredient(Tags.Items.LEATHER, 6)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.TACTICAL_STOCK.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 26)
                .addIngredient(Tags.Items.LEATHER, 8)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.WEIGHTED_STOCK.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 36)
                .addIngredient(Tags.Items.LEATHER, 16)
                .build(consumer);

        //Barrel
        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MUZZLE_BRAKE.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 10)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.MUZZLE_COMPENSATOR.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 8)
                .build(consumer);

        WorkbenchRecipeBuilder.workbenchRecipe(ModItems.SILENCER.get())
                .addIngredient(Tags.Items.INGOTS_IRON, 18)
                .addIngredient(Tags.Items.LEATHER, 6)
                .build(consumer);

    }
}