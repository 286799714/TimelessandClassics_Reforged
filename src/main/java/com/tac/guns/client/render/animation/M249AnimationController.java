package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.*;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class M249AnimationController extends MachineGunAnimationController {
    public static int INDEX_BODY = 13;
    public static int INDEX_LEFT_HAND = 0;
    public static int INDEX_RIGHT_HAND = 2;
    public static int INDEX_MAGAZINE = 5;
    public static int INDEX_CHAIN = 4;
    public static int INDEX_CAPS = 7;
    public static int INDEX_HANDLE = 6;
    public static int INDEX_ROTATE = 9;
    public static int INDEX_IRON = 8;

    public static final AnimationMeta STATIC = new AnimationMeta(new ResourceLocation("tac","animations/m249_static.gltf"));
    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(new ResourceLocation("tac","animations/m249_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/m249_reload_empty.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(new ResourceLocation("tac","animations/m249_draw.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(new ResourceLocation("tac","animations/m249_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(new ResourceLocation("tac","animations/m249_inspect.gltf"));
    private static final M249AnimationController instance = new M249AnimationController();

    private M249AnimationController(){
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(RELOAD_EMPTY);
            Animations.load(DRAW);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
            Animations.load(STATIC);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.M249.getId(),this);
    }

    public static M249AnimationController getInstance(){
        return instance;
    }

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label){
            case INSPECT: return INSPECT;
            case INSPECT_EMPTY: return INSPECT_EMPTY;
            case RELOAD_NORMAL: return RELOAD_NORM;
            case DRAW: return DRAW;
            case STATIC: return STATIC;
            case RELOAD_EMPTY: return RELOAD_EMPTY;
            default: return null;
        }
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label){
        return super.getSoundFromLabel(ModItems.M249.get(), label);
    }

    @Override
    protected int getAttachmentsNodeIndex() {
        return INDEX_CAPS;
    }

    @Override
    protected int getRightHandNodeIndex() {
        return INDEX_RIGHT_HAND;
    }

    @Override
    protected int getLeftHandNodeIndex() {
        return INDEX_LEFT_HAND;
    }
}

