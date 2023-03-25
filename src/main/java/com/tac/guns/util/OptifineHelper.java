package com.tac.guns.util;

//import org.apache.commons.beanutils.PropertyUtils;

import com.tac.guns.GunMod;
//import net.optifine.shaders.DrawBuffers;
//import net.optifine.shaders.Program;
//import net.optifine.shaders.Shaders;
//import net.optifine.shaders.ShadersFramebuffer;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class OptifineHelper
{
    public static boolean isShadersEnabled() {
        return false;
    }
    /*public static FlipTextures getFlipTextures() {
        return Shaders.ProgramTerrain.getDrawBuffers();
    }*/
/*
    private static Boolean loaded = null;
    private static Field programIdField;

    public static boolean isLoaded()
    {
        if(loaded == null)
        {
            try
            {
                Class.forName("optifine.Installer");
                loaded = true;
            }
            catch(ClassNotFoundException e)
            {
                loaded = false;
            }
        }
        return loaded;
    }
    public static boolean isShadersEnabled()
    {
        if(isLoaded())
        {
            try
            {
                Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");
                if(clazz != null && programIdField == null)
                {
                    programIdField = clazz.getDeclaredField("activeProgramID");
                }
                if(programIdField != null)
                {
                    int activeProgramID = (int) programIdField.get(null);
                    return activeProgramID != 0;
                }
            }
            catch(Exception ignored) {}
        }
        return false;
    }

    public static Field dfbCheck;
    public static ShadersFramebuffer dfb;
    public static ShadersFramebuffer getDfb()
    {

        try
        {
            Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");
            if(clazz != null && dfbCheck == null)
            {
                dfbCheck = clazz.getDeclaredField("dfb");
            }
            if(dfbCheck != null)
            {
                dfbCheck.setAccessible(true);
                dfb = (ShadersFramebuffer) dfbCheck.get(null);
                return dfb;
            }
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get DFB");
        }

        return null;
    }

    public static Field dfbBufferCheck;
    public static DrawBuffers dfbBuffer;
    public static DrawBuffers getDfbBuffer()
    {
        try
        {
            Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");
            if(clazz != null && dfbBufferCheck == null)
            {
                dfbBufferCheck = clazz.getDeclaredField("dfbDrawBuffers");
            }
            if(dfbBufferCheck != null)
            {
                dfbBufferCheck.setAccessible(true);
                dfbBuffer = (DrawBuffers) dfbBufferCheck.get(null);
                return dfbBuffer;
            }
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get DFB");
        }

        return null;
    }

    private static Field shaderName;
    private static Field gbuffersFormat;

    public static boolean isRenderingDfb() {
        if(isShadersEnabled()) {
            return Shaders.isRenderingDfb;
        }
        return false;
    }

    public static void checkBufferFlip(Program program) {
        if(isLoaded()) {
            if(isShadersEnabled()) {
                Class<?> clazz;
                try {
                    clazz = Class.forName("net.optifine.shaders.Shaders");
                    Method m=clazz.getDeclaredMethod("checkBufferFlip",Program.class);
                    m.setAccessible(true);
                    m.invoke(null,program);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void bindGbuffersTextures() {
        if(isLoaded()) {
            if(isShadersEnabled()) {
                Class<?> clazz;
                try {
                    clazz = Class.forName("net.optifine.shaders.Shaders");
                    Method m=clazz.getDeclaredMethod("bindGbuffersTextures");
                    m.setAccessible(true);
                    m.invoke(null);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /*public static int getDrawFrameBuffer() {
        if(isLoaded()) {
            if(isShadersEnabled()) {
                if(Shaders.isRenderingDfb) {
                    return TACOptifineShadersHelper.getDFB();
                }
            }
        }
        return Minecraft.getInstance().getFramebuffer().framebufferObject;
    }*/
/*
    public static int getPixelFormat(int internalFormat) {
        switch (internalFormat) {
            case 33333:
            case 33334:
            case 33339:
            case 33340:
            case 36208:
            case 36209:
            case 36226:
            case 36227:
                return 36251;
        }
        return 32993;
    }

    public static int[] getGbuffersFormat() {
        if (isLoaded()) {
            try {
                Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");
                if (clazz != null && gbuffersFormat == null) {
                    gbuffersFormat = clazz.getDeclaredField("gbuffersFormat");
                    gbuffersFormat.setAccessible(true);
                }
                if (gbuffersFormat != null) {
                    int[] format = (int[]) gbuffersFormat.get(null);
                    return format;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        return null;
    }

 */


}
