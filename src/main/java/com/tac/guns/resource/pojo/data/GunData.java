package com.tac.guns.resource.pojo.data;

import com.google.gson.annotations.SerializedName;
import com.tac.guns.api.gun.FireMode;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GunData {
    @SerializedName("ammo")
    private ResourceLocation ammoId;

    @SerializedName("rpm")
    private int roundsPerMinute;

    @SerializedName("draw_time")
    private float drawTime;

    @SerializedName("aim_time")
    private float aimTime;

    @SerializedName("reload")
    private GunReloadData reloadData;

    @SerializedName("fire_mode")
    private List<FireMode> fireModeSet;

    @SerializedName("recoil")
    private GunRecoil recoil;

    public ResourceLocation getAmmoId() {
        return ammoId;
    }

    public int getRoundsPerMinute() {
        return roundsPerMinute;
    }

    public float getDrawTime() {
        return drawTime;
    }

    public float getAimTime() {
        return aimTime;
    }

    public GunReloadData getReloadData() {
        return reloadData;
    }

    public List<FireMode> getFireModeSet() {
        return fireModeSet;
    }

    public GunRecoil getRecoil() {
        return recoil;
    }

    /**
     * @return 枪械开火的间隔，单位为 ms 。
     */
    public long getShootInterval() {
        // 为避免非法运算，随意返回一个默认值。
        if (roundsPerMinute == 0) {
            return 300;
        }
        return 60_000L / roundsPerMinute;
    }
}
