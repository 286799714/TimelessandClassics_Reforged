package com.tac.guns.client.animation;

import com.mojang.logging.LogUtils;
import com.tac.guns.client.animation.gltf.AnimationStructure;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationController {
    private final AnimationStructure animationStructure;
    private final AnimationListenerSupplier listenerSupplier;
    private List<ObjectAnimation> prototypes;
    private final ArrayList<ObjectAnimationRunner> currentRunners = new ArrayList<>();

    public AnimationController(AnimationStructure animationStructure, AnimationListenerSupplier model){
        this.animationStructure = animationStructure;
        this.listenerSupplier = model;
        prototypes = null;
    }

    public AnimationStructure getAnimationStructure(){
        return animationStructure;
    }

    public AnimationListenerSupplier getListenerSupplier(){
        return listenerSupplier;
    }

    @Nullable
    public List<ObjectAnimation> getAnimationsPrototype(){
        return prototypes;
    }

    public void refreshPrototypes(){
        prototypes = AnimationResources.getInstance().createAnimations(animationStructure, (AnimationListenerSupplier[]) null);
    }

    /**Animations on different tracks must ensure that no nodes overlap,
     * otherwise the animation data will overwrite each other.*/
    public void runAnimation(int track, String animationName, float transitionTimeS){
        if(prototypes == null){
            prototypes = AnimationResources.getInstance().createAnimations(animationStructure, (AnimationListenerSupplier[]) null);
        }
        for(ObjectAnimation prototype : prototypes){
            if(prototype.name.equals(animationName)){

                //ensure the capability
                for(int i = currentRunners.size(); i <= track; i++){
                    currentRunners.add(null);
                }

                ObjectAnimation animation = new ObjectAnimation(prototype);
                animation.applyAnimationListeners(listenerSupplier);
                ObjectAnimationRunner runner = new ObjectAnimationRunner(animation);
                runner.setProgressNs(0);
                runner.run();

                ObjectAnimationRunner oldRunner = currentRunners.get(track);
                if(transitionTimeS > 0) {
                    if (oldRunner != null) {
                        if(oldRunner.isTransitioning()){
                            LogUtils.getLogger().warn("try to run animation '" + animationName + "' while old animation is transitioning");
                            return;
                        }
                        oldRunner.pause();
                        oldRunner.transition(runner, (long) (transitionTimeS * 1e9));
                    }else{
                        currentRunners.set(track, runner);
                    }
                }else {
                    if (oldRunner != null) {
                        oldRunner.pause();
                        currentRunners.set(track, runner);
                    }else {
                        currentRunners.set(track, runner);
                    }
                }

                return;
            }
        }
        LogUtils.getLogger().warn("An animation named '" + animationName + "' does not exist in the animation prototype.");
    }

    public ObjectAnimationRunner getAnimation(int track){
        if(track >= currentRunners.size()) return null;
        return currentRunners.get(track);
    }

    public @UnmodifiableView List<ObjectAnimation> getPrototypes(){
        if(prototypes == null) return null;
        return Collections.unmodifiableList(prototypes);
    }

    public void update(){
        for (int i = 0; i < currentRunners.size(); i++){
            ObjectAnimationRunner runner = currentRunners.get(i);
            runner.update();
            if(runner.isTransitioning() && runner.getTransitionTo() != null) {
                runner.getTransitionTo().update();
            }
            if(!runner.isTransitioning() && runner.getTransitionTo() != null){
                currentRunners.set(i, runner.getTransitionTo());
            }
        }
    }
}
