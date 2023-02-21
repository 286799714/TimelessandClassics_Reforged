package com.tac.guns.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.net.optifine.shaders.TACOptifineShadersHelper;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.render.gun.model.scope.scopeUtil.ScopeGlobal;
import com.tac.guns.common.Gun;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.item.GunItem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.task.FarmTask;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.tac.guns.util.OptifineHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL30;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static net.minecraft.client.settings.PointOfView.FIRST_PERSON;
import static org.lwjgl.opengl.GL11.*;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ScreenTextureState extends RenderState.TexturingState
{
    private static ScreenTextureState instance = null;

    public static ScreenTextureState instance()
    {
        return instance == null ? instance = new ScreenTextureState() : instance;
    }

    private int textureId;
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
        ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).addReloadListener(this.scopeRenderGlobal);
        scopeRenderGlobal = new ScopeGlobal(mc);
        /*try {
            this.renderEndNanoTime = WorldRenderer.class.getDeclaredField("renderEndNanoTime");
        } catch (Exception ignored) {
        }
        if (this.renderEndNanoTime == null) try {
            this.renderEndNanoTime = WorldRenderer.class.getDeclaredField("field_78534_ac");
        } catch (Exception ignored) {
        }
        if (this.renderEndNanoTime != null) {
            this.renderEndNanoTime.setAccessible(true);
        }*/
        //this.renderEndNanoTime =
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onRenderHUD);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::renderTick);
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::onWorldLoad);
    }

    private Field renderEndNanoTime;
    /*@SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            scopeRenderGlobal.setWorldAndLoadRenderers((ClientWorld) event.getWorld());
        }
    }*/
    public int getTextureId()
    {
        if(this.textureId == 0)
        {
            this.textureId = TextureUtil.generateTextureId();
            // Texture params only need to be set once, not once per frame
            RenderSystem.bindTexture(this.textureId);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, 9729);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, 9729); // This final number may be useful
            glTexParameteri(3553 , GL_TEXTURE_MIN_FILTER, 9728);
            glTexParameteri(3553 , GL_TEXTURE_MAG_FILTER, 9728);


            // Can I make optifine Shaders compatible?
            /*int wrap = true ? 33071 : 10497;
            glTexParameteri(3553, 10242, wrap);
            glTexParameteri(3553, 10243, wrap);
            glTexParameteri(3553, 10240, 9728);
            glTexParameteri(3553, 10241, 9728);
*/
        }
        return this.textureId;
    }

    public static int MIRROR_TEX;
    public static int OVERLAY_TEX;
    public static int INSIDE_GUN_TEX;
    public static int DEPTH_TEX;
    public static int DEPTH_ERASE_TEX;
    public static int SCOPE_MASK_TEX;
    public static int SCOPE_LIGHTMAP_TEX;
    public static int lastWidth;
    public static int lastHeight;

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        //genMirror();
        isRenderHand0=false;
    }*/


    // Next im guessing i'll have to check this world render, it says the render is incomplete, maybe this code is supposed to complete it for the specific image output?
    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.player != null && mc.currentScreen == null) {
                //If player has gun, update scope
                if (mc.player.getHeldItemMainhand().getItem() instanceof GunItem) {
                    if(OVERLAY_TEX==-1||(lastWidth!=mc.getMainWindow().getWidth()||lastHeight!=mc.getMainWindow().getHeight())) {
                        GL11.glPushMatrix();
                        if(OVERLAY_TEX!=-1) {
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
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                        lastWidth=mc.getMainWindow().getWidth();
                        lastHeight=mc.getMainWindow().getHeight();
                        GL11.glPopMatrix();
                    }
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
            Shaders.setHandsRendered(false, true);
        }
    }
    public static boolean isRenderHand0=false;
    public static boolean needRenderHand1=false;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        //genMirror();
        isRenderHand0=false;
    }

    public void setupOverlayRendering()
    {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F); // TODO: Ig is the cam repos?
    }
    /*public static float getFov() {
        return (50.0f / ((ModelAttachment) itemAttachment.type.model).config.sight.fovZoom);
    }*/
    private static Minecraft mc = Minecraft.getInstance();
    public void renderWorld(Minecraft mc, float partialTick) {

        float zoom = 10;

        GL11.glPushMatrix();
        GlStateManager.color4f(1, 1, 1,1);

        WorldRenderer renderBackup = mc.worldRenderer;
        //Save the current settings to be reset later
        long endTime = 0;
        boolean hide = mc.gameSettings.hideGUI;
        int limit = mc.gameSettings.framerateLimit;
        RayTraceResult mouseOver = mc.objectMouseOver;
        boolean bobbingBackup = mc.gameSettings.viewBobbing;
        double fovBackup = mc.gameSettings.fov;
        //float mouseSensitivityBackup = mc.gameSettings.mouseSensitivity;

        TACOptifineShadersHelper.setGameRenderer(mc, this.scopeRenderGlobal);

        //Change game settings for the Scope
        mc.gameSettings.hideGUI = true;
        //mc.gameSettings.thirdPersonView = 0;
        //mc.gameSettings.fovSetting = zoom;
        mc.gameSettings.fov = zoom;
        mc.gameSettings.viewBobbing = false;
        //Make sure the FOV isn't less than 1
        //if (mc.gameSettings.fovSetting < 0) {
        //    mc.gameSettings.fovSetting = 1;
        //}

        /*if (limit != 0 && renderEndNanoTime != null) {
            try {
                endTime = renderEndNanoTime.getLong(mc.entityRenderer);
            } catch (Exception ignored) {
            }
        }

        int fps = Math.max(30, mc.gameSettings.limitFramerate);
        */
        //Minecraft.getMinecraft().getFramebuffer().framebufferClear();

        //GlStateManager.bindBuffer(, Minecraft.getInstance().getFramebuffer().framebufferObject);

        //Supplier<Integer> supplier = new AtomicInteger(this.textureId)::incrementAndGet;
        /*RenderSystem*/
        endTime = Util.nanoTime();

        GlStateManager.bindFramebuffer(GlStateManager.getActiveTextureId()/* Minecraft.getInstance().getFramebuffer().func_242996_f()*/, this.textureId);
        int tex = Minecraft.getInstance().getFramebuffer().func_242996_f();
        TACOptifineShadersHelper.setFramebufferTexture(Minecraft.getInstance().getFramebuffer(), this.textureId);// = MIRROR_TEX;
        GL30.glFramebufferTexture2D(/*GlStateManager.getActiveTextureId()*/GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureId, 0);

        if(mc.worldRenderer != null && mc.gameRenderer != null) {
            //TODO: need to find how to replace my rerender call without blowing up the game, next fix any debug issues with frame tex id mapping
            Entity entity = this.mc.getRenderViewEntity();
            double d0 = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double)partialTick;
            double d1 = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double)partialTick;
            double d2 = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double)partialTick;
            MatrixStack matrixStackWrl = new MatrixStack();
            matrixStackWrl.translate(d0, d1, d2);

            //mc.gameRenderer.renderWorld(partialTick, endTime, new MatrixStack());
            mc.gameRenderer.updateCameraAndRender(partialTick, endTime, true);//gameRenderer.renderWorld(partialTick, endTime + (1000000000 / 30), matrixStackWrl);
            try {
                //mc.worldRenderer.setDisplayListEntitiesDirty();
                //mc.getFirstPersonRenderer().

                //TODO DEBUG AND FIGURE OUT WHAT IS MISSING, WITHOUT THIS NEW RENDERER I AM GOING TO HAVE MASSIVE DIFFICULTY HAVING A CLEAN PiP OUTPUT
                //mc.gameRenderer.renderWorld(partialTick, endTime, new MatrixStack());// renderWorld(partialTick, endTime, matrixStackWrl);
            }
            catch (Exception e)
            {
                GunMod.LOGGER.log(Level.FATAL, "FATAL NPE DETECTED WITHIN RESET GAME RENDERER");

                // TODO RESET
                GL20.glUseProgram(0);

                TACOptifineShadersHelper.setFramebufferTexture(Minecraft.getInstance().getFramebuffer(), tex);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, tex, 0);
                //GL30.glCopyImageSubData(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, lastWidth, lastHeight, 1);

        /*if (limit != 0 && renderEndNanoTime != null) {
            try {
                renderEndNanoTime.setLong(mc.entityRenderer, endTime);
            } catch (Exception ignored) {
            }
        }*/


                //Go back to the original Settings
                mc.objectMouseOver = mouseOver;
                mc.gameSettings.framerateLimit = limit;
                mc.gameSettings.hideGUI = hide;
                mc.gameSettings.viewBobbing = bobbingBackup;
                mc.gameSettings.fov = fovBackup;

                TACOptifineShadersHelper.setWorldRenderer(mc, renderBackup);
                //mc.gameRenderer.renderWorld(partialTick, endTime, new MatrixStack());
                GL11.glPopMatrix();
                return;
            }
        }
        else
            GunMod.LOGGER.log(Level.FATAL, "Warning gameRender doesn't exist");

        GL20.glUseProgram(0);

        TACOptifineShadersHelper.setFramebufferTexture(Minecraft.getInstance().getFramebuffer(), tex);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, tex, 0);
        //GL30.glCopyImageSubData(Minecraft.getMinecraft().getFramebuffer().framebufferTexture, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, MIRROR_TEX, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, lastWidth, lastHeight, 1);

        /*if (limit != 0 && renderEndNanoTime != null) {
            try {
                renderEndNanoTime.setLong(mc.entityRenderer, endTime);
            } catch (Exception ignored) {
            }
        }*/


        //Go back to the original Settings
        mc.objectMouseOver = mouseOver;
        mc.gameSettings.framerateLimit = limit;
        mc.gameSettings.hideGUI = hide;
        mc.gameSettings.viewBobbing = bobbingBackup;
        mc.gameSettings.fov = fovBackup;

        TACOptifineShadersHelper.setWorldRenderer(mc, renderBackup);
        //mc.gameRenderer = renderBackup;


        GL11.glPopMatrix();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHUD(RenderGameOverlayEvent.Pre event) {
        if(event.getType()!= RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        ItemStack stack=Minecraft.getInstance().player.getHeldItemMainhand();
        if (stack != null && stack.getItem() instanceof GunItem) {
            GlStateManager.pushMatrix();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
            //GL11.glBindTexture(GL11.GL_TEXTURE_2D, MIRROR_TEX);
                if(OptifineHelper.isLoaded() && OptifineHelper.isShadersEnabled()) {//OptifineHelper.isRenderingDfb()/* && OptifineHelper.getDfb() != null && TACOptifineShadersHelper.getFlipTextures() != null*/) {
                    //TODO: Optifine and OpenGL 2.1 Compatibility ?

                    //OptifineHelper.getDfb().exists() ? flipTextures.getA(0) : 0
                    GL43.glCopyImageSubData(TACOptifineShadersHelper.getFlipTextures().getA(0), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, this.textureId, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(),
                            Minecraft.getInstance().getMainWindow().getHeight(), 1);
                } else {
                    /*if (Shaders.capabilities.OpenGL30) {
                        GL30.glCopyImageSubData(Minecraft.getInstance().getFramebuffer().func_242996_f(), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, this.textureId, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(), 1);

                    } else {*/
                        //GL11.glBindTexture(Minecraft.getInstance().getFramebuffer().func_242996_f(), this.textureId);
                        GL43.glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,0,0,Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight(),0);
                        //GL11.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0,  0, 0,0,  Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight());
                    //}
                }
                GlStateManager.popMatrix();
       }
    }
}
