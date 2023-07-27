package com.tac.guns.client.handler;

import com.tac.guns.Reference;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.event.ClientSetSprintingEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Arcomit
 * @updateDate 2023/7/27
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID,value = Dist.CLIENT)
public class SprintingHandler {
    @SubscribeEvent
    public static void playerSetSprinting(ClientSetSprintingEvent event) {
        if (event.getSprinting()){
            if ((AimingHandler.get().isAiming()) || ShootingHandler.get().isShooting()){
                event.setSprinting(false);
                //阻止玩家进入疾跑状态（是阻止,而非在setSprinting(true)后再setSprinting(false)）
                //单击疾跑键进入疾跑只会触发一次setSprinting(true),所以能用setSprinting(false)取消,但是长按疾跑键时会一直触发setSprinting(true)
                //setSprinting()方法如果为True时就会给玩家加速度,长按疾跑键时在setSprinting(true)后setSprinting(false)的时间差会导致无法减速。
            }
        }
    }
}
