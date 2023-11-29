package com.tac.guns.client.animation;

import com.mojang.logging.LogUtils;
import com.tac.guns.client.animation.interpolator.Interpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectAnimationChannel {
    /**name of node*/
    public String node;
    /**
     * The key frame times, in seconds
     */
    public float[] keyframeTimeS;
    /**
     * The values. Each element of this array corresponds to one key frame time,
     * can be value of translation, rotation or scale
     */
    public float[][] values;

    public Interpolator interpolator;

    public final ChannelType type;

    private final List<AnimationListener> listeners = new ArrayList<>();

    public ObjectAnimationChannel(ChannelType type){
        this.type = type;
    }

    public void addListener(AnimationListener listener){
        if(listener.getType().equals(type))
            listeners.add(listener);
        else
            LogUtils.getLogger().warn("trying to add wrong type of listener to channel.");
    }

    public void removeListener(AnimationListener listener){
        listeners.remove(listener);
    }

    public void clearListeners(){
        listeners.clear();
    }

    public float getEndTimeS()
    {
        return keyframeTimeS[keyframeTimeS.length-1];
    }

    /**
     * Perform a calculation based on the input time and notify all AnimationListener of the result
     * @param timeS absolute time in seconds
     * */
    public void update(float timeS){
        int indexFrom = computeIndex(timeS);
        int indexTo = Math.min(keyframeTimeS.length - 1, indexFrom + 1);
        float alpha = computeAlpha(timeS, indexFrom);

        float[] result = new float[values[indexFrom].length];
        interpolator.interpolate(indexFrom, indexTo, alpha, result);

        for (AnimationListener listener : listeners)
        {
            listener.update(result);
        }
    }

    private int computeIndex(float timeS)
    {
        int index = Arrays.binarySearch(keyframeTimeS, timeS);
        if (index >= 0)
        {
            return index;
        }
        return Math.max(0, -index - 2);
    }

    private float computeAlpha(float timeS, int indexFrom)
    {
        if (timeS <= keyframeTimeS[0])
        {
            return 0.0f;
        }
        if (timeS >= keyframeTimeS[keyframeTimeS.length-1])
        {
            return 1.0f;
        }
        float local = timeS - keyframeTimeS[indexFrom];
        float delta = keyframeTimeS[indexFrom+1] - keyframeTimeS[indexFrom];
        return local / delta;
    }

    public enum ChannelType{
        TRANSLATION,
        ROTATION,
        SCALE
    }
}
