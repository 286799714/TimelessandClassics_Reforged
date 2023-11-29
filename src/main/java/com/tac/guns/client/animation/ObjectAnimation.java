package com.tac.guns.client.animation;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Create a {@link ObjectAnimationRunner} instance to run a {@link ObjectAnimation}
 * */
public class ObjectAnimation {
    public @Nonnull PlayType playType = PlayType.PLAY_ONCE_HOLD;

    public final String name;
    /**
     * The current time, in nanoseconds
     */
    public long timeNs = 0;

    /**
     * key of this map is node name.
     * */
    private final Map<String, List<ObjectAnimationChannel>> channels = new HashMap<>();

    /**
     * The maximum {@link ObjectAnimationChannel#getEndTimeS()} of all channels
     */
    private float maxEndTimeS;

    public ObjectAnimation(@Nonnull String name){
        this.name = Objects.requireNonNull(name);
    }

    public void addChannel(ObjectAnimationChannel channel){
        channels.compute(channel.node, (node, list)->{
            if(list == null) list = new ArrayList<>();
            list.add(channel);
            return list;
        });
        updateMaxEndTime();
    }

    public void removeChannel(ObjectAnimationChannel channel){
        channels.compute(channel.node, (node, list)->{
            if(list == null) return null;
            list.remove(channel);
            return list;
        });
        updateMaxEndTime();
    }

    public List<ObjectAnimationChannel> getChannels(){
        List<ObjectAnimationChannel> list = new ArrayList<>();
        for(List<ObjectAnimationChannel> channelList : channels.values()){
            list.addAll(channelList);
        }
        return list;
    }

    public void addAnimationListener(String nodeName, AnimationListener listener){
        List<ObjectAnimationChannel> channelList = channels.get(nodeName);
        if(channelList != null){
            for(ObjectAnimationChannel channel : channelList){
                if(channel.type.equals(listener.getType()))
                    channel.addListener(listener);
            }
        }
    }

    public void removeAnimationListener(String nodeName, AnimationListener listener){
        List<ObjectAnimationChannel> channelList = channels.get(nodeName);
        if(channelList != null){
            for(ObjectAnimationChannel channel : channelList){
                channel.removeListener(listener);
            }
        }
    }

    /**
     * Trigger all listeners to notify them of the updated value.
     * */
    public void update(){
        for(List<ObjectAnimationChannel> channels : channels.values()){
            for(ObjectAnimationChannel channel : channels) {
                channel.update(timeNs / 1e9f);
            }
        }
    }

    public float getMaxEndTimeS(){
        return maxEndTimeS;
    }

    private void updateMaxEndTime()
    {
        maxEndTimeS = 0.0f;
        for(List<ObjectAnimationChannel> channels : channels.values()){
            for(ObjectAnimationChannel channel : channels) {
                maxEndTimeS = Math.max(maxEndTimeS, channel.getEndTimeS());
            }
        }
    }

    public enum PlayType{
        PLAY_ONCE_RESET,
        PLAY_ONCE_HOLD,
        LOOP
    }
}
