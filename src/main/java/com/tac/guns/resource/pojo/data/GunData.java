package com.tac.guns.resource.pojo.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class GunData {
    @SerializedName("ammo")
    private ResourceLocation ammoId;

    @SerializedName("rpm")
    private int roundsPerMinute;

    @SerializedName("draw_time")
    private float drawTime;

    @SerializedName("reload")
    private GunReloadData reloadData;

    public ResourceLocation getAmmoId() {
        return ammoId;
    }

    public int getRoundsPerMinute() {
        return roundsPerMinute;
    }

    public float getDrawTime() {
        return drawTime;
    }

    public GunReloadData getReloadData() {
        return reloadData;
    }

    /**
     * @return 枪械开火的间隔，单位为 ms 。
     */
    public long getShootInterval(){
        // 为避免非法运算，随意返回一个默认值。
        if (roundsPerMinute == 0) {
            return 300;
        }
        return 60_000L / roundsPerMinute;
    }
}
