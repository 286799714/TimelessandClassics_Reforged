package com.tac.guns.item.transition.grenades;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.entity.specifics.BaseballGrenadeEntity;
import com.tac.guns.item.GrenadeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class BaseballGrenadeItem extends GrenadeItem
{
    private float power;
    private float radius;
    public BaseballGrenadeItem(Properties properties, int maxCookTime, float power, float radius, float speed)
    {
        super(properties, maxCookTime, power, radius, speed);
        this.power = power;
        this.radius = radius;
    }

    public ThrowableGrenadeEntity create(Level world, LivingEntity entity, int timeLeft)
    {
        return new BaseballGrenadeEntity(world, entity, timeLeft, this.power, this.radius); // Current ThrowableGrenadeEntity is perfect for impact 1/31/2022
    }

    public boolean canCook()
    {
        return true;
    }

    protected void onThrown(Level world, ThrowableGrenadeEntity entity)
    {
    }
}
