package com.tac.guns.client.animation.interpolator;

import com.tac.guns.client.animation.gltf.AnimationModel;

public class InterpolatorUtil {
    public static Interpolator fromInterpolation(AnimationModel.Interpolation interpolation){
        switch (interpolation){
            case SPLINE -> {
                return new Spline();
            }
            case STEP -> {
                return new Step();
            }
            default -> {
                return new Linear();
            }
        }
    }
}
