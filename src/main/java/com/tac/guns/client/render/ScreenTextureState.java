package com.tac.guns.client.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tac.guns.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ScreenTextureState extends RenderStateShard.TexturingStateShard
{
    private static ScreenTextureState instance = null;

    public static ScreenTextureState instance()
    {
        return instance == null ? instance = new ScreenTextureState() : instance;
    }

    private int textureId;
    private int lastWindowWidth;
    private int lastWindowHeight;

    private ScreenTextureState()
    {
        super("screen_texture", () ->
        {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableTexture();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, instance().getTextureId());
        }, () ->
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
        });
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRenderWorldLast);
    }

    private int getTextureId()
    {
        if(this.textureId == 0)
        {
            this.textureId = TextureUtil.generateTextureId();
            // Texture params only need to be set once, not once per frame
            RenderSystem.bindTexture(this.textureId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        return this.textureId;
    }

    private void onRenderWorldLast(RenderLevelStageEvent event)
    {
        if(event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            Window mainWindow = Minecraft.getInstance().getWindow();

            // OpenGL will spit out an error (GL_INVALID_VALUE) if the window is minimised (or draw calls stop)
            // It seems just testing the width or height if it's zero is enough to prevent it
            if (mainWindow.getScreenWidth() <= 0 || mainWindow.getScreenHeight() <= 0)
                return;

            RenderSystem.bindTexture(this.getTextureId());
            if (mainWindow.getScreenWidth() != this.lastWindowWidth || mainWindow.getScreenHeight() != this.lastWindowHeight) {
                // When window resizes the texture needs to be re-initialized and copied, so both are done in the same call
                this.lastWindowWidth = mainWindow.getScreenWidth();
                this.lastWindowHeight = mainWindow.getScreenHeight();
                glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 0, 0, mainWindow.getWidth(), mainWindow.getHeight(), 0);
            } else {
                // Copy sub-image is faster than copy because the texture does not need to be initialized
                glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mainWindow.getWidth(), mainWindow.getHeight());
            }
        }
    }
}
/*
package com.tac.guns.client.render;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.net.optifine.shaders.TACOptifineShadersHelper;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.render.gun.model.scope.scopeUtil.ScopeGlobal;
import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.OptifineHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ScreenTextureState extends RenderStateShard.TexturingStateShard
{
    private static ScreenTextureState instance = null;

    public static ScreenTextureState instance()
    {
        return instance == null ? instance = new ScreenTextureState() : instance;
    }

    public int textureId;
    private int lastWindowWidth;
    private int lastWindowHeight;
    private ScopeGlobal scopeRenderGlobal;
    private ScreenTextureState()
    {
        super("screen_texture", () ->
        {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableTexture();
            //RenderSystem.
            RenderSystem.bindTexture(instance().getTextureId());
        }, () ->
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
        });
        */
/*if(Config.CLIENT.quality.worldRerenderPiPAlpha.get()) {
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this.scopeRenderGlobal);
            scopeRenderGlobal = new ScopeGlobal(mc);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onRenderHUD);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::renderTick);
            //MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onWorldLoad);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRenderWorldLast);
        }
        else*//*

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onRenderWorldLastLegacy);
    }

    private Field renderEndNanoTime;


    */
/*@SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote()) {
            mc.worldRenderer.setWorldAndLoadRenderers((ClientWorld) event.getWorld());
        }
    }*//*

    private int getTextureId()
    {
        if(this.textureId == 0)
        {
            this.textureId = TextureUtil.generateTextureId();
            // Texture params only need to be set once, not once per frame
            RenderSystem.bindTexture(this.textureId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        return this.textureId;
    }

    public int regen()
    {
        this.textureId = TextureUtil.generateTextureId();
        RenderSystem.bindTexture(this.textureId);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, 9728);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, 9728); // This final number may be useful
        return this.textureId;
    }

    public int MIRROR_TEX;
    public int OVERLAY_TEX;
    public int INSIDE_GUN_TEX;
    public int DEPTH_TEX;
    public int DEPTH_ERASE_TEX;
    public int SCOPE_MASK_TEX;
    public int SCOPE_LIGHTMAP_TEX;
    public int lastWidth;
    public int lastHeight;

    */
/*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        //genMirror();
        isRenderHand0=false;
    }*//*



    // Next im guessing i'll have to check this world render, it says the render is incomplete, maybe this code is supposed to complete it for the specific image output?
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.player != null && mc.screen == null) {
                //If player has gun, update scope
                if (mc.player.getMainHandItem().getItem() instanceof GunItem) {
                    if(this.textureId==-1||(lastWidth!=mc.getWindow().getScreenWidth()||lastHeight!=mc.getWindow().getScreenHeight())) {
                        GL11.glPushMatrix();

                        */
/*GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);if(OVERLAY_TEX!=-1) {
                            GL11.glDeleteTextures(OVERLAY_TEX);
                        }
                        OVERLAY_TEX = GL11.glGenTextures();
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, OVERLAY_TEX);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                        if(INSIDE_GUN_TEX!=-1) {
                            GL11.glDeleteTextures(INSIDE_GUN_TEX);
                        }
                        INSIDE_GUN_TEX = GL11.glGenTextures();
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, INSIDE_GUN_TEX);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

                        if(SCOPE_MASK_TEX!=-1) {
                            GL11.glDeleteTextures(SCOPE_MASK_TEX);
                        }
                        SCOPE_MASK_TEX = GL11.glGenTextures();
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, SCOPE_MASK_TEX);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                        if(SCOPE_LIGHTMAP_TEX!=-1) {
                            GL11.glDeleteTextures(SCOPE_LIGHTMAP_TEX);
                        }
                        SCOPE_LIGHTMAP_TEX = GL11.glGenTextures();
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, SCOPE_LIGHTMAP_TEX);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);*//*


                        lastWidth=mc.getWindow().getScreenWidth();
                        lastHeight=mc.getWindow().getScreenHeight();
                        GL11.glPopMatrix();
                    }
                    if(Gun.getScope(mc.player.getMainHandItem()) != null)
                        renderWorld(mc, event.renderTickTime);

                }
            }

        }
    }

    public void onPreRenderHand0() {
        isRenderHand0=true;
    }

    public void onPreRenderHand1() {
        if(needRenderHand1) {
            needRenderHand1=false;
            //Shaders.setHandsRendered(false, true);
        }
    }
    public static boolean isRenderHand0=false;
    public static boolean needRenderHand1=false;
    public boolean isRenderGun=false;
    @SuppressWarnings("removal")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderLevelLastEvent event) {
        //genMirror();
        isRenderHand0=false;
        isRenderGun=false;
    }

    */
/*public static float getFov() {
        return (50.0f / ((ModelAttachment) itemAttachment.type.model).config.sight.fovZoom);
    }*//*

    private static Minecraft mc = Minecraft.getInstance();
    public void renderWorld(Minecraft mc, float partialTick)
    {
        float zoom = 10;
        GL11.glPushMatrix();
        GL43.glDeleteTextures(ScreenTextureState.instance().textureId);
        GL43.glDeleteBuffers(ScreenTextureState.instance().textureId);
        RenderSystem.bindTexture(ScreenTextureState.instance().regen());

        LevelRenderer renderBackup = mc.levelRenderer;
        //Save the current settings to be reset later
        long endTime = 0;
        boolean hide = mc.options.hideGui;
        int limit = mc.options.framerateLimit;
        HitResult mouseOver = mc.hitResult;
        boolean bobbingBackup = mc.options.bobView;
        double fovBackup = mc.options.fov;

        //TACOptifineShadersHelper.setGameRenderer(mc, this.scopeRenderGlobal);

        //Change game settings for the Scope
        mc.options.hideGui = true;
        mc.options.fov = zoom;
        mc.options.bobView = true;

        */
/*RenderSystem*//*

        endTime = Util.getNanos();

        //GL30.glBindFramebuffer(mc.getFramebuffer().getColorTextureId(), this.textureId);
        //GL30.glBindTexture(mc.getFramebuffer().getColorTextureId(), this.textureId);
        int tex = mc.getMainRenderTarget().getColorTextureId();
        //TACOptifineShadersHelper.setFramebufferTexture(mc.getMainRenderTarget(), this.textureId);// = MIRROR_TEX;
        //GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureId, 0);
        if(mc.levelRenderer != null && mc.gameRenderer != null) {
            mc.gameRenderer.render(partialTick, endTime, true);
            //mc.gameRenderer.renderWorld(partialTick, endTime, new MatrixStack());//gameRenderer.renderWorld(partialTick, endTime + (1000000000 / 30), matrixStackWrl);
        }
        else
            GunMod.LOGGER.log(Level.FATAL, "Warning gameRender doesn't exist");

        //GL20.glUseProgram(0);
        //TACOptifineShadersHelper.setFramebufferTexture(mc.getMainRenderTarget(), tex);
        //GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, tex, 0);
        //GL30.glCopyTexSubImage2D(Minecraft.getInstance().getFramebuffer().getColorTextureId(), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, lastWidth, lastHeight, 1);

        //Compat with system, frame clearing properly


        mc.hitResult = mouseOver;
        mc.options.framerateLimit = limit;
        mc.options.hideGui = hide;
        mc.options.bobView = bobbingBackup;
        mc.options.fov = fovBackup;

        //TACOptifineShadersHelper.setWorldRenderer(mc, renderBackup);

        GL11.glPopMatrix();
    }

    //@SubscribeEvent//(priority = EventPriority.HIGHEST)
    public void onRenderHUD(RenderGameOverlayEvent.Pre event) {
        if(event.getType()!= RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        ItemStack stack=Minecraft.getInstance().player.getMainHandItem();
        if (stack != null && stack.getItem() instanceof GunItem && Gun.getScope(stack) != null && Gun.getScope(stack).getAdditionalZoom().getDrCropZoom() > 0) {
            //RenderSystem.pushMatrix();
            //TODO: Figure out why this is never called, the copy doesn't even work when I force it, do I care getting this working?
            GL43.glDeleteTextures(ScreenTextureState.instance().textureId);
            GL43.glDeleteBuffers(ScreenTextureState.instance().textureId);
            RenderSystem.bindTexture(ScreenTextureState.instance().getTextureId());
            if(OptifineHelper.isShadersEnabled()) {
                    //TODO: Optifine and OpenGL 2.1 Compatibility ?
                    */
/*if(this.textureId == 0)
                        this.getTextureId();
                    RenderSystem.bindTexture(this.textureId);*//*

                */
/*
                    GL43.glCopyImageSubData(Shaders.activeProgram.getDrawBuffersCustom().get(0) TACOptifineShadersHelper.getFlipTextures().getA(0), 3553, 0,0,0, 0, this.textureId, 3553,0,0,0,0, mc.getWindow().getScreenWidth(),
                            mc.getWindow().getScreenHeight(),1);

                 *//*

                    GunMod.LOGGER.log(Level.INFO, "GL43 grab with optifine has functioned");
                } else {
                    //RenderSystem.bindTexture(this.textureId);
                    GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,0,0,mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight(),0);
                }
            //RenderSystem.popMatrix();
       }
    }

    @SubscribeEvent
    @SuppressWarnings("removal")
    public void onRenderWorldLastLegacy(RenderLevelLastEvent event)
    {
        Window mainWindow = Minecraft.getInstance().getWindow();
        if(mainWindow.getScreenWidth() <= 0 || mainWindow.getScreenHeight() <= 0)
            return;

        RenderSystem.bindTexture(this.getTextureId());
        if(mainWindow.getScreenWidth() != this.lastWindowWidth || mainWindow.getScreenHeight() != this.lastWindowHeight)
        {
            // When window resizes the texture needs to be re-initialized and copied, so both are done in the same call
            this.lastWindowWidth = mainWindow.getScreenWidth();
            this.lastWindowHeight = mainWindow.getScreenHeight();
            GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 0, 0, mainWindow.getWidth(), mainWindow.getHeight(), 0);
        }
        else
        {
            // Copy sub-image is faster than copy because the texture does not need to be initialized
            GL43.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mainWindow.getWidth(), mainWindow.getHeight());
        }
    }
}
*/
