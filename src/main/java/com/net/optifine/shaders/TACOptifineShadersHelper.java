package com.net.optifine.shaders;

import com.tac.guns.GunMod;
import com.tac.guns.util.OptifineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersFramebuffer;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    public static Field FramebufferTextureCheck;
    public static int FramebufferTexturet;
    // TODO: Learn how to obtain private field from instance object
    public static void setFramebufferTexture(Framebuffer bufferToPull, int txtRef) {
        try
        {
            FramebufferTextureCheck = bufferToPull.getClass().getDeclaredField("framebufferTexture");
            FramebufferTextureCheck.setAccessible(true);
            int modifiers = FramebufferTextureCheck.getModifiers();
            Field modifierField = FramebufferTextureCheck.getClass().getDeclaredField("modifiers");
            modifiers = modifiers & ~Modifier.FINAL;
            modifierField.setAccessible(true);
            modifierField.setInt(FramebufferTextureCheck, modifiers);

            FramebufferTextureCheck.set(bufferToPull, txtRef);

            return;
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get or set frameBufferTexture");
        }
        GunMod.LOGGER.log(Level.FATAL, "could not get or set frameBufferTexture (-1)");
        return;
    }

    public static Field GameRendererCheck;
    public static int GameRenderert;
    // TODO: What the fuck am I doing... Is there a better way to do this without profanity?
    public static void setGameRenderer(Minecraft mc, GameRenderer newRenderer) {
        try
        {
            GameRendererCheck = mc.getClass().getDeclaredField("gameRenderer");
            GameRendererCheck.setAccessible(true);
            int modifiers = GameRendererCheck.getModifiers();
            Field modifierField = GameRendererCheck.getClass().getDeclaredField("modifiers");
            modifiers = modifiers & ~Modifier.FINAL;
            modifierField.setAccessible(true);
            modifierField.setInt(GameRendererCheck, modifiers);

            GameRendererCheck.set(mc, newRenderer);

            return;
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get or set worldRenderer");
        }
        GunMod.LOGGER.log(Level.FATAL, "could not get or set worldRenderer (-1)");
        try {
            if (GameRendererCheck.get(mc) == null)
                GunMod.LOGGER.log(Level.FATAL, "FATAL ERROR WORLD RENDER WAS NOT SET");
        }
        catch (Exception e)
        {
            GunMod.LOGGER.log(Level.FATAL, "FATAL ERROR WORLD RENDER WAS NOT SET");
        }
        return;
    }

    public static Field WorldRendererCheck;
    public static int WorldRenderert;
    // TODO: What the fuck am I doing... Is there a better way to do this without profanity?
    public static void setWorldRenderer(Minecraft mc, WorldRenderer newRenderer) {
        try
        {
            WorldRendererCheck = mc.getClass().getDeclaredField("worldRenderer");
            WorldRendererCheck.setAccessible(true);
            int modifiers = WorldRendererCheck.getModifiers();
            Field modifierField = WorldRendererCheck.getClass().getDeclaredField("modifiers");
            modifiers = modifiers & ~Modifier.FINAL;
            modifierField.setAccessible(true);
            modifierField.setInt(WorldRendererCheck, modifiers);

            WorldRendererCheck.set(mc, newRenderer);

            return;
        }
        catch(Exception ignored) {
            GunMod.LOGGER.log(Level.FATAL, "could not get or set worldRenderer");
        }
        GunMod.LOGGER.log(Level.FATAL, "could not get or set worldRenderer (-1)");
        try {
            if (WorldRendererCheck.get(mc) == null)
                GunMod.LOGGER.log(Level.FATAL, "FATAL ERROR WORLD RENDER WAS NOT SET");
        }
        catch (Exception e)
        {
            GunMod.LOGGER.log(Level.FATAL, "FATAL ERROR WORLD RENDER WAS NOT SET");
        }
        return;
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
