package com.tac.guns.common;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.annotation.Validator;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CustomRigLoader extends SimpleJsonResourceReloadListener
{
    private static final Gson GSON_INSTANCE = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        return builder.create();
    });

    private static CustomRigLoader instance;

    private Map<ResourceLocation, CustomRig> customRigMap = new HashMap<>();
    public CustomRigLoader()
    {
        super(GSON_INSTANCE, "rigs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler)
    {
        ImmutableMap.Builder<ResourceLocation, CustomRig> builder = ImmutableMap.builder();
        objects.forEach((resourceLocation, object) ->
        {
            try
            {
                CustomRig customRig = GSON_INSTANCE.fromJson(object, CustomRig.class);
                if(customRig != null && Validator.isValidObject(customRig))
                {
                    builder.put(resourceLocation, customRig);
                }
                else
                {
                    GunMod.LOGGER.error("Couldn't load data file {} as it is missing or malformed", resourceLocation);
                }
            }
            catch(InvalidObjectException e)
            {
                GunMod.LOGGER.error("Missing required properties for {}", resourceLocation);
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });
        this.customRigMap = builder.build();
    }

    /**
     * Writes all custom guns into the provided packet buffer
     *
     * @param buffer a packet buffer get
     */
    public void writeCustomRigs(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(this.customRigMap.size());
        this.customRigMap.forEach((id, rig) -> {
            buffer.writeResourceLocation(id);
            buffer.writeNbt(rig.serializeNBT());
        });
    }

    /**
     * Reads all registered guns from the provided packet buffer
     *
     * @param buffer a packet buffer get
     * @return a map of registered guns from the server
     */
    public static ImmutableMap<ResourceLocation, CustomRig> readCustomRigs(FriendlyByteBuf buffer)
    {
        int size = buffer.readVarInt();
        if(size > 0)
        {
            ImmutableMap.Builder<ResourceLocation, CustomRig> builder = ImmutableMap.builder();
            for(int i = 0; i < size; i++)
            {
                ResourceLocation id = buffer.readResourceLocation();
                CustomRig customRig = new CustomRig();
                customRig.deserializeNBT(buffer.readNbt());
                builder.put(id, customRig);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event)
    {
        CustomRigLoader customGunLoader = new CustomRigLoader();
        event.addListener(customGunLoader);
        CustomRigLoader.instance = customGunLoader;
    }

    @Nullable
    public static CustomRigLoader get()
    {
        return instance;
    }
}
