package com.tac.guns.client;

import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpecialModel {
    private ResourceLocation modelLocation;
    private BakedModel cachedModel;

    public SpecialModel(String modelName)
    {
        this.modelLocation = new ResourceLocation(Reference.MOD_ID, "special/" + modelName);
    }
    @OnlyIn(Dist.CLIENT)
    public BakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if(model == Minecraft.getInstance().getModelManager().getMissingModel())
            {
                ForgeModelBakery.addSpecialModel(this.modelLocation);
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    // A method that allows a minecraft entity bot to hide from a player

    /*public static void findCoverFromPlayer(EntityBot bot, EntityPlayer player, int distance) {
        // Get the position of the player
        BlockPos playerPos = player.getPosition();
        // Get the position of the bot
        BlockPos botPos = bot.getPosition();
        // Get the distance between the bot and the player
        double distanceBetween = botPos.getDistance(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        // If the distance is less than the distance we want to hide
        if (distanceBetween < distance) {
            // Get the direction from the bot to the player
            Vec3d direction = new Vec3d(playerPos.getX() - botPos.getX(), playerPos.getY() - botPos.getY(), playerPos.getZ() - botPos.getZ());
            // Normalize the direction
            direction = direction.normalize();
            // Get the opposite direction
            direction = direction.scale(-1);
            // Get the position of the block in the opposite direction
            BlockPos blockPos = new BlockPos(botPos.getX() + direction.x, botPos.getY() + direction.y, botPos.getZ() + direction.z);
            // Get the block at the position
            Block block = bot.world.getBlockState(blockPos).getBlock();
            // If the block is not air
            if (block != Blocks.AIR) {
                // Set the position of the bot to the block position
                bot.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
        }
    }*/


   /* // A method that renders green night vision effect if true
    public static void nightVisionRender(boolean nightVision) {
        if (nightVision) {
            // Get the minecraft instance
            Minecraft mc = Minecraft.getInstance();
            // Get the player
            PlayerEntity player = mc.player;
            // Get the game settings
            GameSettings gameSettings = mc.gameSettings;
            // If the player is not null
            if (player != null) {
                // If the night vision is enabled
                if (nightVision) {
                    // If the night vision is not enabled
                    if (!gameSettings.gammaSetting.equals(1000.0F)) {
                        // Set the gamma to 1000
                        gameSettings.gammaSetting = 1000.0F;
                    }
                }
            }
        }
    }*/


}
