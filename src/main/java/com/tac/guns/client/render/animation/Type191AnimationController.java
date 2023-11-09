package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Type191AnimationController extends GunAnimationController {
    public static int INDEX_BODY = 6;
    public static int INDEX_LEFT_HAND = 10;
    public static int INDEX_RIGHT_HAND = 7;
    public static int INDEX_MAG = 2;
    public static int INDEX_EXTRA_MAG = 4;
    public static int INDEX_BULLET1 = 1;
    public static int INDEX_BULLET2 = 3;
    public static int INDEX_RELEASE = 5;
    public static int INDEX_BOLT = 0;

    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_reload_empty.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_inspect.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_draw.gltf"));
    public static final AnimationMeta STATIC = new AnimationMeta(new ResourceLocation("tac", "animations/qbz_191_static.gltf"));
    private static final Type191AnimationController instance = new Type191AnimationController();

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label) {
            case RELOAD_EMPTY: return RELOAD_EMPTY;
            case RELOAD_NORMAL: return RELOAD_NORM;
            case DRAW: return DRAW;
            case INSPECT: return INSPECT;
            case INSPECT_EMPTY: return INSPECT_EMPTY;
            case STATIC: return STATIC;
            default: return null;
        }
    }

    private Type191AnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(DRAW);
            Animations.load(RELOAD_EMPTY);
            Animations.load(STATIC);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.QBZ_191.getId(), this);
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label) {
        return super.getSoundFromLabel(ModItems.QBZ_191.get(), label);
    }

    public static Type191AnimationController getInstance(){
        return instance;
    }

    @Override
    protected int getAttachmentsNodeIndex() {
        return INDEX_BODY;
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