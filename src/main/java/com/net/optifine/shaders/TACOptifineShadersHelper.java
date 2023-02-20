package com.net.optifine.shaders;

import com.tac.guns.GunMod;
import com.tac.guns.util.OptifineHelper;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersFramebuffer;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;

/**
 * @author Hueihuea
 * Of MFW contributors, this example is to get a new research baseline for better PiP scopes, thank you to the original designer.
 * */
public class TACOptifineShadersHelper
{
    public static Field fliptCheck;
    public static FlipTextures flipt;
    // TODO: Learn how to obtain private field from instance object
    public static FlipTextures getFlipTextures() {
        try
        {
            if(OptifineHelper.getDfb() == null)
                return null;

            fliptCheck = ShadersFramebuffer.class.getDeclaredField("colorTexturesFlip");
            fliptCheck.setAccessible(true);
            flipt  = (FlipTextures) fliptCheck.get(OptifineHelper.getDfb());
            if(flipt != null)
            {
                return flipt;
            }
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get FlipTextures");
        }
        return null;
    }

    /*
    public static int getDFB() {
        return Shaders. dfb;
    }
      */

    /*public static Program getDFBDrawBuffers() {
        return Shaders.activeProgram;
    }*/
    /*
    public static int getUsedColorBuffers() {
        return Shaders.usedColorBuffers;
    }

    public static IntBuffer getDFBDepthTextures() {
        return Shaders.dfbDepthTextures;
    }*/
}
