package com.tac.guns.client.animation;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ObjectAnimationRunner {
    @Nonnull public final ObjectAnimation animation;
    private boolean running = false;
    private long lastUpdateNs;
    /**current animation playback progress*/
    private long progressNs;

    public ObjectAnimationRunner(@Nonnull ObjectAnimation animation){
        this.animation = Objects.requireNonNull(animation);
    }

    public void run(){
        if(!running) {
            running = true;
            lastUpdateNs = System.nanoTime();
        }
    }

    public void pause(){
        running = false;
    }

    public void reset(){
        progressNs = 0;
    }

    public void setProgressNs(long progressNs){
        this.progressNs = progressNs;
    }

    public void update(){
        if(running){
            long currentNs = System.nanoTime();
            progressNs += currentNs - lastUpdateNs;
            lastUpdateNs = currentNs;
        }

        switch (animation.playType){
            case PLAY_ONCE_RESET -> {
                if(progressNs / 1e9f > animation.getMaxEndTimeS()) {
                    reset();
                    pause();
                }
            }
            case PLAY_ONCE_HOLD -> {
                if(progressNs / 1e9f > animation.getMaxEndTimeS()) {
                    progressNs = (long)(animation.getMaxEndTimeS() * 1e9f);
                    pause();
                }
            }
            case LOOP -> {
                if(progressNs / 1e9f > animation.getMaxEndTimeS()) {
                    progressNs = progressNs % (long)(animation.getMaxEndTimeS() * 1e9f);
                }
            }
        }

        animation.timeNs = progressNs;
        animation.update();
    }

    public boolean isRunning(){
        return running;
    }
}
