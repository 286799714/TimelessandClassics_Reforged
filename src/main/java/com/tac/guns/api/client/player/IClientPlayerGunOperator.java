package com.tac.guns.api.client.player;

import com.tac.guns.api.gun.ShootResult;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IClientPlayerGunOperator {
    /**
     * LocalPlayer 通过 Mixin 的方式实现了这个接口
     */
    static IClientPlayerGunOperator fromLocalPlayer(LocalPlayer player) {
        return (IClientPlayerGunOperator) player;
    }

    /**
     * 自动检查玩家能否开火，并执行客户端开火逻辑。
     *
     * @return 返回开火的结果，成功或失败。
     */
    ShootResult shoot();

    /**
     * 客户端弹药消耗检查
     *
     * @return 如果为 false，不检查弹药消耗，也不会向服务端发包
     */
    boolean checkAmmo();

    /**
     * 执行客户端切枪逻辑。
     */
    void draw();

    void reload();

    void inspect();

    void fireSelect();

    void aim(boolean isAim);

    float getClientAimingProgress();
}
