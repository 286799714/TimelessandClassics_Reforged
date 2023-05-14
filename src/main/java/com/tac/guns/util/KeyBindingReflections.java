package com.tac.guns.util;

import net.minecraft.client.KeyMapping;

import java.lang.reflect.Field;
import java.util.Map;

public class KeyBindingReflections
{

    /*//TODO: wtf why do I ahve to do this, can we not access registered keys in mc anymore?
    //Map<String, KeyMapping> keysInMC = null;
    public static Map<String, KeyMapping> GetKeyBindings()
    {
        try
        {
            Class<?> clazz = Class.forName("net.minecraft.client.KeyMapping");
            Field field = null;
            if(clazz != null)
            {
                field = clazz.getDeclaredField("ALL");
                field.setAccessible(true);
            }
            Map<String, KeyMapping> iWant = (Map<String, KeyMapping>) field.get(null);
            if(iWant != null)
            {

                return iWant;
            }
        }
        catch(Exception ignored) {}

        return null;
    }*/
}
