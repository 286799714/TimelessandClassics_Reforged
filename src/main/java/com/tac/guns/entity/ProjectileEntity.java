package com.tac.guns.entity;

//import com.sun.tools.jdi.Packet;

import com.mojang.logging.LogUtils;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Config;
import com.tac.guns.common.AimingManager;
import com.tac.guns.common.BoundingBoxManager;
import com.tac.guns.common.Gun;
import com.tac.guns.common.Gun.Projectile;
import com.tac.guns.common.SpreadTracker;
import com.tac.guns.event.GunProjectileHitEvent;
import com.tac.guns.event.LevelUpEvent;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.interfaces.IDamageable;
import com.tac.guns.interfaces.IExplosionDamageable;
import com.tac.guns.interfaces.IHeadshotBox;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.transition.TimelessGunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.*;
import com.tac.guns.util.BufferUtil;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.util.math.ExtendedEntityRayTraceResult;
import com.tac.guns.world.ProjectileExplosion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jline.utils.Log;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProjectileEntity extends Entity implements IEntityAdditionalSpawnData
{
    private static final Predicate<Entity> PROJECTILE_TARGETS = input -> input != null && input.isPickable() && !input.isSpectator();
    private static final Predicate<BlockState> IGNORE_LEAVES = input -> input != null && Config.COMMON.gameplay.ignoreLeaves.get() && input.getBlock() instanceof LeavesBlock;

    protected int shooterId;
    protected LivingEntity shooter;
    protected Gun modifiedGun;
    protected Gun.General general;
    protected Gun.Projectile projectile;
    private ItemStack weapon = ItemStack.EMPTY;
    private ItemStack item = ItemStack.EMPTY;
    protected float additionalDamage = 0.0F;
    protected EntityDimensions entitySize;
    public double modifiedGravity;
    public int life;

    protected Vec3 startPos;

    public ProjectileEntity(EntityType<? extends Entity> entityType, Level worldIn)
    {
        super(entityType, worldIn);
    }

    public ProjectileEntity(EntityType<? extends Entity> entityType, Level worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun, float randP, float randY)
    {
        this(entityType, worldIn);
        this.shooterId = shooter.getId();
        this.shooter = shooter;
        this.modifiedGun = modifiedGun;
        this.general = modifiedGun.getGeneral();
        this.projectile = modifiedGun.getProjectile();
        this.entitySize = new EntityDimensions(this.projectile.getSize(), this.projectile.getSize(), false);
        this.modifiedGravity = modifiedGun.getProjectile().isGravity() ? GunModifierHelper.getModifiedProjectileGravity(weapon, -0.0285) : 0.0; // -0.0285 Default upcoming new -0.0125
        this.life = GunModifierHelper.getModifiedProjectileLife(weapon, this.projectile.getLife());

        /* Get speed and set motion */
        Vec3 dir = this.getDirection(shooter, weapon, item, modifiedGun);
        double speedModifier = GunEnchantmentHelper.getProjectileSpeedModifier(weapon);
        double speed = GunModifierHelper.getModifiedProjectileSpeed(weapon, this.projectile.getSpeed() * speedModifier);
        this.setDeltaMovement(dir.x * speed, dir.y * speed, dir.z * speed);
        this.updateHeading();

        /* Spawn the projectile half way between the previous and current position */
        double posX = shooter.xOld + (shooter.getX() - shooter.xOld) / 2.0;
        double posY = shooter.yOld + (shooter.getY() - shooter.yOld) / 2.0 + shooter.getEyeHeight();
        double posZ = shooter.zOld + (shooter.getZ() - shooter.zOld) / 2.0;
        this.setPos(posX, posY, posZ);

        Item ammo = ForgeRegistries.ITEMS.getValue(this.projectile.getItem());
        if(ammo != null)
        {
            int customModelData = -1;
            if(weapon.getTag() != null)
            {
                if(weapon.getTag().contains("Model", Tag.TAG_COMPOUND))
                {
                    ItemStack model = ItemStack.of(weapon.getTag().getCompound("Model"));
                    if(model.getTag() != null && model.getTag().contains("CustomModelData"))
                    {
                        customModelData = model.getTag().getInt("CustomModelData");
                    }
                }
            }
            ItemStack ammoStack = new ItemStack(ammo);
            if(customModelData != -1)
            {
                ammoStack.getOrCreateTag().putInt("CustomModelData", customModelData);
            }
            this.item = ammoStack;
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return this.entitySize;
    }

    private Vec3 getDirection(LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun)
    {
        float gunSpread = GunModifierHelper.getModifiedSpread(weapon, modifiedGun.getGeneral().getSpread()) * GunEnchantmentHelper.getSpreadModifier(weapon);

        if(gunSpread == 0F)
        {
            return this.getVectorFromRotation(shooter.getXRot(), shooter.getYRot());
        }

        if(shooter instanceof Player)
        {
            if(!modifiedGun.getGeneral().isAlwaysSpread())
            {
                float modSpread = SpreadTracker.get((Player) shooter).getSpread(item);
                if(modSpread != 0)
                    gunSpread *= SpreadTracker.get((Player) shooter).getSpread(item);
                else {
                    gunSpread = modifiedGun.getGeneral().getFirstShotSpread();
                    gunSpread = GunModifierHelper.getModifiedFirstShotSpread(weapon, gunSpread);
                }
            }

            AimingManager.AimTracker tracker = AimingManager.get().getAimTracker((Player) shooter);
            if(tracker == null || tracker.getLerpProgress() < 0.95f)
            {
                if(gunSpread < 0.5)
                    gunSpread+=0.5f;
                gunSpread *= (modifiedGun.getGeneral().getHipFireInaccuracy());
                gunSpread = GunModifierHelper.getModifiedHipFireSpread(weapon, gunSpread);
                if(SyncedEntityData.instance().get((Player) shooter, ModSyncedDataKeys.MOVING) != 0)
                {
                    gunSpread *= Math.max(1 , (2F * ( 1 + SyncedEntityData.instance().get((Player) shooter, ModSyncedDataKeys.MOVING))) * modifiedGun.getGeneral().getMovementInaccuracy());
                }
            }
            if(((Player) shooter).isCrouching() && modifiedGun.getGeneral().getProjectileAmount() == 1)
            {
                gunSpread *= 0.75F;
            }
        }

        return this.getVectorFromRotation(shooter.getXRot() - (gunSpread / 2.0F) + random.nextFloat() * gunSpread, shooter.getYRot() - (gunSpread / 2.0F) + random.nextFloat() * gunSpread);
    }

    public void setWeapon(ItemStack weapon)
    {
        this.weapon = weapon.copy();
    }

    public ItemStack getWeapon()
    {
        return this.weapon;
    }

    public void setItem(ItemStack item)
    {
        this.item = item;
    }

/*
    @Override
    protected Item getDefaultItem() {
        return null;
    }
*/

    public ItemStack getItem()
    {
        return this.item;
    }

    public void setAdditionalDamage(float additionalDamage)
    {
        this.additionalDamage = additionalDamage;
    }

    public double getModifiedGravity()
    {
        return this.modifiedGravity;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.updateHeading();
        this.onProjectileTick();

        if(!this.level.isClientSide())
        {
            Vec3 startVec = this.position();
            if (this.tickCount < 1) {
                startPos = startVec;
            }
            Vec3 endVec = startVec.add(this.getDeltaMovement());

            //TODO: Make RayTraceBlocks return the meta class, use end vec as a new start vec if a tracked block was the hit vec, pretty much re-running the raytrace

            HitResult result = rayTraceBlocks(this.level, new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this), IGNORE_LEAVES);
            BlockHitResult resultB = (BlockHitResult) result;
            BlockState blockState = level.getBlockState(resultB.getBlockPos());
            /*
            if(blockState.getMaterial() == Material.WOOL)
            {
                Predicate<BlockState> IGNORE_BLOCK = input -> input != null && input.getBlock().equals(blockState.getBlock());
                HitResult result2 = rayTraceBlocks(this.level, new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this), IGNORE_BLOCK);
                if(result2.getType() != HitResult.Type.MISS)
                {
                    endVec = result2.getLocation();
                }
            }
            */
            if(resultB.getType() != HitResult.Type.MISS)
            {
                endVec = resultB.getLocation();
            }

            /*RayTraceResult result = rayTraceBlocks(this.world, new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this), IGNORE_LEAVES);

            BlockRayTraceResult resultB = (BlockRayTraceResult) result;
            if(result.getType() != RayTraceResult.Type.MISS)
            {
                endVec = result.getHitVec();
            }
            BlockState blockState = world.getBlockState(resultB.getPos());*/
            // if(ignorePredicate.test(blockState)) return null;


            List<EntityResult> hitEntities = null;
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.COLLATERAL.get(), this.weapon);
            if(level == 0)
            {
                EntityResult entityResult = this.findEntityOnPath(startVec, endVec);
                if(entityResult != null)
                {
                    hitEntities = Collections.singletonList(entityResult);
                }
            }
            else
            {
                hitEntities = this.findEntitiesOnPath(startVec, endVec);
            }

            if(hitEntities != null && hitEntities.size() > 0)
            {
                for(EntityResult entityResult : hitEntities)
                {
                    result = new ExtendedEntityRayTraceResult(entityResult);
                    if(((EntityHitResult) result).getEntity() instanceof Player)
                    {
                        Player player = (Player) ((EntityHitResult) result).getEntity();

                        if(this.shooter instanceof Player && !((Player) this.shooter).canHarmPlayer(player) && !Config.COMMON.development.bulletSelfHarm.get())
                        {
                            result = null;
                        }
                    }
                    if(result != null)
                    {
                        this.onHit(result, startVec, endVec);
                    }
                }
            }
            else
            {
                this.onHit(result, startVec, endVec); // Issue
            }
        }

        double nextPosX = this.getX() + this.getDeltaMovement().x();
        double nextPosY = this.getY() + this.getDeltaMovement().y();
        double nextPosZ = this.getZ() + this.getDeltaMovement().z();
        this.setPos(nextPosX, nextPosY, nextPosZ);

        if(this.projectile.isGravity())
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, this.modifiedGravity, 0));
        }

        if(this.tickCount >= this.life)
        {
            if(this.isAlive())
            {
                this.onExpired();
            }
            this.remove(RemovalReason.DISCARDED);
        }
    }

    /**
     * A simple method to perform logic on each tick of the projectile. This method is appropriate
     * for spawning particles. Override {@link #tick()} to make changes to physics
     */
    protected void onProjectileTick()
    {
    }

    /**
     * Called when the projectile has run out of it's life. In other words, the projectile managed
     * to not hit any blocks and instead aged. The grenade uses this to explode in the air.
     */
    protected void onExpired()
    {
    }

    @Nullable
    protected EntityResult findEntityOnPath(Vec3 startVec, Vec3 endVec)
    {
        Vec3 hitVec = null;
        Entity hitEntity = null;
        boolean headshot = false;
        List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), PROJECTILE_TARGETS);
        double closestDistance = Double.MAX_VALUE;
        for(Entity entity : entities)
        {
            if(!entity.equals(this.shooter) || Config.COMMON.development.bulletSelfHarm.get())
            {
                EntityResult result = this.getHitResult(entity, startVec, endVec);
                if(result == null)
                    continue;
                Vec3 hitPos = result.getHitPos();
                double distanceToHit = startVec.distanceTo(hitPos);
                if(distanceToHit < closestDistance)
                {
                    hitVec = hitPos;
                    hitEntity = entity;
                    closestDistance = distanceToHit;
                    headshot = result.isHeadshot();
                }
            }
        }
        return hitEntity != null ? new EntityResult(hitEntity, hitVec, headshot) : null;
    }

    @Nullable
    protected List<EntityResult> findEntitiesOnPath(Vec3 startVec, Vec3 endVec)
    {
        List<EntityResult> hitEntities = new ArrayList<>();
        List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), PROJECTILE_TARGETS);
        for(Entity entity : entities)
        {
            if(!entity.equals(this.shooter) || Config.COMMON.development.bulletSelfHarm.get())
            {
                EntityResult result = this.getHitResult(entity, startVec, endVec);
                if(result == null)
                    continue;
                hitEntities.add(result);
            }
        }
        return hitEntities;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private EntityResult getHitResult(Entity entity, Vec3 startVec, Vec3 endVec)
    {
        double expandHeight = entity instanceof Player && !entity.isCrouching() ? 0.0625 : 0.0;
        AABB boundingBox = entity.getBoundingBox();
        if(Config.COMMON.gameplay.improvedHitboxes.get() && entity instanceof ServerPlayer && this.shooter != null)
        {
            int ping = (int) Math.floor((((ServerPlayer) this.shooter).latency / 1000.0) * 20.0 + 0.5);
            boundingBox = BoundingBoxManager.getBoundingBox((Player) entity, ping);
        }
        boundingBox = boundingBox.expandTowards(0, expandHeight, 0);

        Vec3 hitPos = boundingBox.clip(startVec, endVec).orElse(null);
        Vec3 grownHitPos = boundingBox.inflate(Config.COMMON.gameplay.growBoundingBoxAmountV2.get(), 0, Config.COMMON.gameplay.growBoundingBoxAmountV2.get()).clip(startVec, endVec).orElse(null);
        if(hitPos == null && grownHitPos != null)
        {
            HitResult raytraceresult = rayTraceBlocks(this.level, new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this), IGNORE_LEAVES);
            BlockHitResult resultB = (BlockHitResult) raytraceresult;
            BlockState blockState = level.getBlockState(resultB.getBlockPos());
            if(blockState.getMaterial() == Material.WOOL)
            {
                Predicate<BlockState> IGNORE_BLOCK = input -> input != null && input.getBlock().equals(blockState.getBlock());
                HitResult result2 = rayTraceBlocks(this.level, new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this), IGNORE_BLOCK);
                if(result2.getType() != HitResult.Type.MISS)
                {
                    endVec = result2.getLocation();
                }
                if(raytraceresult.getType() == HitResult.Type.BLOCK)
                {
                    return null;
                }
            }
            else if(raytraceresult.getType() == HitResult.Type.BLOCK)
            {
                return null;
            }
            hitPos = grownHitPos;
        }

        /* Check for headshot */
        boolean headshot = false;
        if(Config.COMMON.gameplay.enableHeadShots.get() && entity instanceof LivingEntity)
        {
            IHeadshotBox<LivingEntity> headshotBox = (IHeadshotBox<LivingEntity>) BoundingBoxManager.getHeadshotBoxes(entity.getType());
            if(headshotBox != null)
            {
                AABB box = headshotBox.getHeadshotBox((LivingEntity) entity);
                if(box != null)
                {
                    box = box.move(boundingBox.getCenter().x, boundingBox.minY, boundingBox.getCenter().z);
                    Optional<Vec3> headshotHitPos = box.clip(startVec, endVec);
                    if(!headshotHitPos.isPresent())
                    {
                        box = box.inflate(Config.COMMON.gameplay.growBoundingBoxAmountV2.get(), 0, Config.COMMON.gameplay.growBoundingBoxAmountV2.get());
                        headshotHitPos = box.clip(startVec, endVec);
                    }
                    if(headshotHitPos.isPresent() && (hitPos == null || headshotHitPos.get().distanceTo(hitPos) < 0.5))
                    {
                        hitPos = headshotHitPos.get();
                        headshot = true;
                    }
                }
            }
        }

        if(hitPos == null)
        {
            return null;
        }

        return new EntityResult(entity, hitPos, headshot);
    }

    private void onHit(HitResult result, Vec3 startVec, Vec3 endVec)
    {
        if(modifiedGun == null)
            return;

//        if (MinecraftForge.EVENT_BUS.post(new GunProjectileHitEvent(result, this)))
//            return;

        if(result instanceof BlockHitResult)
        {
            BlockHitResult blockRayTraceResult = (BlockHitResult) result;
            if(blockRayTraceResult.getType() == HitResult.Type.MISS)
            {
                return;
            }

            Vec3 hitVec = result.getLocation();
            BlockPos pos = blockRayTraceResult.getBlockPos();
            BlockState state = this.level.getBlockState(pos);
            Block block = state.getBlock();

            if(Objects.requireNonNull(block.getRegistryName()).getPath().contains("_button"))
                return;

            if(Config.COMMON.gameplay.enableGunGriefing.get() &&
                    (block instanceof HalfTransparentBlock || block instanceof IronBarsBlock) &&
                    state.getMaterial() == Material.GLASS)
            {
                this.level.destroyBlock(blockRayTraceResult.getBlockPos(), Config.COMMON.gameplay.glassDrop.get(), this.shooter);
            }

            /*if(modifiedGun.getProjectile().isRicochet() &&
            ((
                            state.getMaterial() == Material.ROCK ||
                            state.getMaterial() == Material.PACKED_ICE ||
                            state.getMaterial() == Material.IRON ||
                            state.getMaterial() == Material.ANVIL
            )))
            {
                this.onHitBlock(blockRayTraceResult);
            }*/

            if(block instanceof IDamageable)
            {
                ((IDamageable) block).onBlockDamaged(this.level, state, pos, this, this.getDamage(), (int) Math.ceil(this.getDamage() / 2.0) + 1);
            }

            this.onHitBlock(state, pos, blockRayTraceResult.getDirection(), hitVec.x, hitVec.y, hitVec.z);

            if (block instanceof BellBlock bell) {
                bell.attemptToRing(this.level, pos, blockRayTraceResult.getDirection());
            }

            int fireStarterLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FIRE_STARTER.get(), this.weapon);
            if(fireStarterLevel > 0 && Config.COMMON.gameplay.enableGunGriefing.get())
            {
                BlockPos offsetPos = pos.relative(blockRayTraceResult.getDirection());
                if(BaseFireBlock.canBePlacedAt(this.level, offsetPos, blockRayTraceResult.getDirection()))
                {
                    BlockState fireState = BaseFireBlock.getState(this.level, offsetPos);
                    this.level.setBlock(offsetPos, fireState, 11);
                    ((ServerLevel) this.level).sendParticles(ParticleTypes.LAVA, hitVec.x - 1.0 + this.random.nextDouble() * 2.0, hitVec.y, hitVec.z - 1.0 + this.random.nextDouble() * 2.0, 4, 0, 0, 0, 0);
                }
            }
            return;
            // Build pen checks and results per passing through materials
//            if(state.getMaterial() == Material.WOOL || state.getMaterial() == Material.WOOD)
//            {
//                return;
//            }
//            else {
//                this.onHitBlock(state, pos, blockRayTraceResult.getDirection(), hitVec.x, hitVec.y, hitVec.z);
//
//                //TODO: Add wall pen, simple, similar to ricochet but without anything crazy nor issues caused with block-face detection
//                this.remove(RemovalReason.DISCARDED);
//                return;
//            }
        }

        if(result instanceof ExtendedEntityRayTraceResult)
        {
            ExtendedEntityRayTraceResult entityRayTraceResult = (ExtendedEntityRayTraceResult) result;
            Entity entity = entityRayTraceResult.getEntity();
            if(entity.getId() == this.shooterId && !Config.COMMON.development.bulletSelfHarm.get())
            {
                return;
            }

            int fireStarterLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FIRE_STARTER.get(), this.weapon);
            if(fireStarterLevel > 0)
            {
                entity.setSecondsOnFire(2);
            }
            if(!entity.isAlive())
            {
                entity.invulnerableTime = 0;
            }
            else if(entity.isAlive())
            {
                this.onHitEntity(entity, result.getLocation(), startVec, endVec, entityRayTraceResult.isHeadshot());

                int collateralLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.COLLATERAL.get(), this.shooter);
                if(collateralLevel == 0)
                {
                    this.remove(RemovalReason.DISCARDED);
                }

                entity.invulnerableTime = 0;
            }

        }
    }

    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot)
    {
        float damage = this.getDamage(hitVec);
        float newDamage = this.getCriticalDamage(this.weapon, this.random, damage);
        boolean critical = damage != newDamage;
        damage = newDamage;

        if(headshot)
        {
            if (Config.COMMON.gameplay.headShotDamageMultiplier.get() * this.projectile.getGunHeadDamage() >= 0)
                damage *= (float) (Config.COMMON.gameplay.headShotDamageMultiplier.get() * this.projectile.getGunHeadDamage());
            damage += GunModifierHelper.getAdditionalHeadshotDamage(this.weapon) == 0F ? 1F : GunModifierHelper.getAdditionalHeadshotDamage(this.weapon);
        }

        DamageSource source = new DamageSourceProjectile("bullet", this, shooter, weapon).setProjectile();
        if(entity instanceof Player && !WearableHelper.PlayerWornRig((Player) entity).isEmpty())
        {
            if(!WearableHelper.tickFromCurrentDurability((Player) entity, this))
                PacketHandler.getPlayChannel().sendTo(new MessagePlayerShake((Player) entity), ((ServerPlayer)entity).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            else {
                tac_attackEntity(source, entity, damage);
            }
        }
        else {
            tac_attackEntity(source, entity, damage);
        }

        if(this.shooter instanceof Player)
        {
            int hitType = critical ? MessageProjectileHitEntity.HitType.CRITICAL : headshot ? MessageProjectileHitEntity.HitType.HEADSHOT : MessageProjectileHitEntity.HitType.NORMAL;
            updateWeaponLevels(damage);
            PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) this.shooter), new MessageProjectileHitEntity(hitVec.x, hitVec.y, hitVec.z, hitType, entity instanceof Player));
        }

        /* Send blood particle to tracking clients. */
        PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageBlood(hitVec.x, hitVec.y, hitVec.z));
    }

    // Apply blunt damage before applying any effects via armor
    // TODO: Use a new source type, so messages can be "Tickled to death", "Poked to smithereens" etc.
    private void tac_attackWithBluntDamage(DamageSource source, Entity entity, float damage)
    {
        entity.hurt(source, (damage*this.modifiedGun.getProjectile().getBluntDamagePercentage()));
    }

    // tac_ is simply a naming convention for "check tac stuff before you continue this standard mc call", I use it here to explain checking config before applying it's damage, along with armor calculations
    // Is also "bulletClass" aware, makes this a bit more complex than config checks.
    private void tac_attackEntity(DamageSource source, Entity entity, float damage)
    {
        if(Config.COMMON.gameplay.bulletsIgnoreStandardArmor.get()) {
            float damageToMcArmor = 0;
            if (Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore() <= 1.0) {
                damageToMcArmor = (float) (damage * (1 - Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore()));
                entity.hurt(source, damageToMcArmor); // Apply vanilla armor aware damage
            }

            entity.invulnerableTime = 0;
            source.bypassArmor();
            source.bypassMagic();
            if(Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore() > 0.0)
                entity.hurt(source, (damage - damageToMcArmor)); // Apply pure damage
        }
        else
            entity.hurt(source, damage);
    }

    protected void updateWeaponLevels(float damage)
    {
        ItemStack gunStack = this.shooter.getMainHandItem();
        if(!(gunStack.getItem() instanceof GunItem) || gunStack.getTag() == null)
            return;
        if(gunStack.getTag().get("levelDmg") != null)
        {
            gunStack.getTag().putFloat("levelDmg", gunStack.getTag().getFloat("levelDmg") + damage);
        }
        if(gunStack.getTag().get("level") != null)
        {
            TimelessGunItem gunItem = (TimelessGunItem) gunStack.getItem();
            if(gunStack.getTag().getFloat("levelDmg") > (gunItem.getGun().getGeneral().getLevelReq()*((gunStack.getTag().getInt("level")*3.0d))) ) {

                gunStack.getTag().putFloat("levelDmg",0f);
                gunStack.getTag().putInt("level", gunStack.getTag().getInt("level") + 1);
                MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Post((Player) this.shooter, gunStack));
            }
        }
    }

    // RICOCHET IS DEPRECATED
    /*protected void onHitBlock(BlockRayTraceResult blockRayTraceResult)
    {
        if(modifiedGun == null)
            return;
        Direction blockDirection = blockRayTraceResult.getFace();
        switch (blockDirection) {
            case UP:
            case DOWN:
                this.setMotion(this.getMotion().mul(1, -1, 1));
                break;
            case EAST:
            case WEST:
                this.setMotion(this.getMotion().mul(-1, 1, 1));
                break;
            case NORTH:
            case SOUTH:
                this.setMotion(this.getMotion().mul(1, 1, -1));
                break;
            default:
                break;
        }

        Vector3d startVec = this.getPositionVec();
        Vector3d endVec = startVec.add(this.getMotion());
        EntityResult entityResult = this.findEntityOnPath(startVec, endVec);

        RayTraceResult result = rayTraceBlocks(this.world, new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this), IGNORE_LEAVES); // Ricochet Raytrace

        if (entityResult != null) {
            this.tick();
            return;
        } else {
            this.teleportToHitPoint(result);
        }
        this.life -= 1;
    }*/

    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(pos)), new MessageProjectileHitBlock(x, y, z, pos, face));
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FIRE_STARTER.get(), this.weapon) > 0)
            ((ServerLevel) this.level).sendParticles(ParticleTypes.LAVA, x, y, z, 1, 0, 0, 0, 0);
    }

    protected void teleportToHitPoint(HitResult rayTraceResult)
    {
        Vec3 hitResult = rayTraceResult.getLocation();
        this.setPos(hitResult.x, hitResult.y, hitResult.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        this.projectile = new Gun.Projectile();
        this.projectile.deserializeNBT(compound.getCompound("Projectile"));
        this.general = new Gun.General();
        this.general.deserializeNBT(compound.getCompound("General"));
        this.modifiedGravity = compound.getDouble("ModifiedGravity");
        this.life = compound.getInt("MaxLife");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        compound.put("Projectile", this.projectile.serializeNBT());
        compound.put("General", this.general.serializeNBT());
        compound.putDouble("ModifiedGravity", this.modifiedGravity);
        compound.putInt("MaxLife", this.life);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeNbt(this.projectile.serializeNBT());
        buffer.writeNbt(this.general.serializeNBT());
        buffer.writeInt(this.shooterId);
        BufferUtil.writeItemStackToBufIgnoreTag(buffer, this.item);
        buffer.writeDouble(this.modifiedGravity);
        buffer.writeVarInt(this.life);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer)
    {
        this.projectile = new Gun.Projectile();
        this.projectile.deserializeNBT(buffer.readNbt());
        this.general = new Gun.General();
        this.general.deserializeNBT(buffer.readNbt());
        this.shooterId = buffer.readInt();
        this.item = BufferUtil.readItemStackFromBufIgnoreTag(buffer);
        this.modifiedGravity = buffer.readDouble();
        this.life = buffer.readVarInt();
        this.entitySize = new EntityDimensions(this.projectile.getSize(), this.projectile.getSize(), false);
    }

    public void updateHeading()
    {
        float f = Mth.sqrt((float) (this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z()));
        this.setYRot((float) (Mth.atan2(this.getDeltaMovement().x(), this.getDeltaMovement().z()) * (180D / Math.PI)));
        this.setXRot((float) (Mth.atan2(this.getDeltaMovement().y(), (double) f) * (180D / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public Projectile getProjectile()
    {
        return this.projectile;
    }

    private Vec3 getVectorFromRotation(float pitch, float yaw)
    {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    /**
     * Gets the entity who spawned the projectile
     */
    public LivingEntity getShooter()
    {
        return this.shooter;
    }

    /**
     * Gets the id of the entity who spawned the projectile
     */
    public int getShooterId()
    {
        return this.shooterId;
    }

    //DamageReduceOverDistance
    public float getDamage(Vec3 hitVec) {
        float initialDamage = (this.projectile.getDamage() + this.additionalDamage);
        double maxDistance = this.projectile.getLife() * this.projectile.getSpeed();
        double projDistance = hitVec.distanceTo(this.startPos);
        if (this.projectile.isDamageReduceOverLife()) {
            float modifier;
            if (projDistance <= Math.min(Math.min(this.projectile.getSpeed() / 10, maxDistance / 100), 2))
                modifier = this.projectile.getGunCloseDamage() > 1 ? this.projectile.getGunCloseDamage() : 1;
            else {
                float decayStartDistance;
                float decayEndDistance;
                float minDecayMultiplier;
                if (this.projectile.getGunDecayStart() > this.projectile.getGunDecayEnd() && this.projectile.getGunMinDecayMultiplier() > 1f) {
                    decayStartDistance = (float) (Mth.clamp(this.projectile.getGunDecayEnd(), 0f, 1f) * maxDistance);
                    decayEndDistance = (float) (Mth.clamp(this.projectile.getGunDecayStart(), 0f, 1f) * maxDistance);
                    minDecayMultiplier = this.projectile.getGunMinDecayMultiplier();
                } else {
                    decayStartDistance = (float) (Mth.clamp(this.projectile.getGunDecayStart(), 0f, 1f) * maxDistance);
                    decayEndDistance = (float) (Mth.clamp(this.projectile.getGunDecayEnd(), 0f, 1f) * maxDistance);
                    minDecayMultiplier = Mth.clamp(this.projectile.getGunMinDecayMultiplier(), 0f, 1f);
                }

                if (decayStartDistance == decayEndDistance)
                    modifier = projDistance > decayEndDistance ? minDecayMultiplier : 1f;
                else
                    modifier = (float) Mth.clamp(
                            (projDistance - decayEndDistance) * (1 - minDecayMultiplier) / (decayStartDistance - decayEndDistance) + minDecayMultiplier,
                            Math.min(minDecayMultiplier, 1f),
                            Math.max(minDecayMultiplier, 1f));
            }
            initialDamage *= modifier;
        }
        float damage = initialDamage / this.general.getProjectileAmount();
        damage = GunModifierHelper.getModifiedDamage(this.weapon, this.modifiedGun, damage);
        damage = GunEnchantmentHelper.getAcceleratorDamage(this.weapon, damage);
        return Math.max(0F, damage);
    }

    //DamageReduceOverLife
    public float getDamage()
    {
        float initialDamage = (this.projectile.getDamage() + this.additionalDamage);
        if(this.projectile.isDamageReduceOverLife())
        {
            float modifier = ((float) this.projectile.getLife() - (float) (this.tickCount - 1)) / (float) this.projectile.getLife();
            initialDamage *= modifier;
        }
        float damage = initialDamage / this.general.getProjectileAmount();
        damage = GunModifierHelper.getModifiedDamage(this.weapon, this.modifiedGun, damage);
        damage = GunEnchantmentHelper.getAcceleratorDamage(this.weapon, damage);
        return Math.max(0F, damage);
    }

    private float getCriticalDamage(ItemStack weapon, Random rand, float damage)
    {
        float chance = GunModifierHelper.getCriticalChance(weapon) + this.projectile.getGunCritical();
        if (rand.nextFloat() < chance)
        {
            return (float) (damage * Config.COMMON.gameplay.criticalDamageMultiplier.get() * this.projectile.getGunCriticalDamage());
        }
        return damage;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return true;
    }

    @Override
    public void onRemovedFromWorld()
    {
        if(!this.level.isClientSide)
        {
            PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(this::getDeathTargetPoint), new MessageRemoveProjectile(this.getId()));
        }
    }

    private PacketDistributor.TargetPoint getDeathTargetPoint()
    {
        return new PacketDistributor.TargetPoint(this.getX(), this.getY(), this.getZ(), 256, this.level.dimension());
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    private static class BlockRayTraceMeta
    {
        public BlockHitResult blockRayTraceResult;
        public int wallBangCount = 0;
        // Wallbang block hit, update count
        public BlockRayTraceMeta(BlockHitResult blockRayTraceResult, int wallBangCount)
        {
            this.wallBangCount = wallBangCount;
            this.blockRayTraceResult = blockRayTraceResult;
        }
        // No wallbang blocks met
        public BlockRayTraceMeta(BlockHitResult blockRayTraceResult)
        {
            this.blockRayTraceResult = blockRayTraceResult;
        }
    }

    /**
     * A custom implementation of
     * that allows you to pass a predicate to ignore certain blocks when checking for collisions.
     *
     * @param world     the world to perform the ray trace
     * @param context   the ray trace context
     * @param ignorePredicate the block state predicate
     * @return a result of the raytrace
     */
    private static BlockHitResult rayTraceBlocks(Level world, ClipContext context, Predicate<BlockState> ignorePredicate/*, Predicate<BlockState> wallBangPredicate*/)
    {
        /*BlockRayTraceResult r =*/ return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if(ignorePredicate.test(blockState)) return null;
            FluidState fluidState = world.getFluidState(blockPos);
            Vec3 startVec = rayTraceContext.getFrom();
            Vec3 endVec = rayTraceContext.getTo();
            VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockHitResult blockResult = world.clipWithInteractionOverride(startVec, endVec, blockPos, blockShape, blockState);
            VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
            BlockHitResult fluidResult = fluidShape.clip(startVec, endVec, blockPos);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(blockResult.getLocation());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(fluidResult.getLocation());
            /*if(wallBangPredicate.test(blockState))
            {
                return blockResult;
            }*/
            return blockDistance <= fluidDistance ? blockResult : fluidResult;
        }, (rayTraceContext) -> {
            Vec3 Vector3d = rayTraceContext.getFrom().subtract(rayTraceContext.getTo());
            return BlockHitResult.miss(rayTraceContext.getTo(), Direction.getNearest(Vector3d.x, Vector3d.y, Vector3d.z), new BlockPos(rayTraceContext.getTo()));
        });

        //return new BlockRayTraceMeta(r);
    }

    private static <T> T performRayTrace(ClipContext context, BiFunction<ClipContext, BlockPos, T> hitFunction, Function<ClipContext, T> missFactory)
    {
        Vec3 startVec = context.getFrom();
        Vec3 endVec = context.getTo();
        if(startVec.equals(endVec))
        {
            return missFactory.apply(context);
        }
        else
        {
            double startX = Mth.lerp(-0.0000001, endVec.x, startVec.x);
            double startY = Mth.lerp(-0.0000001, endVec.y, startVec.y);
            double startZ = Mth.lerp(-0.0000001, endVec.z, startVec.z);
            double endX = Mth.lerp(-0.0000001, startVec.x, endVec.x);
            double endY = Mth.lerp(-0.0000001, startVec.y, endVec.y);
            double endZ = Mth.lerp(-0.0000001, startVec.z, endVec.z);
            int blockX = Mth.floor(endX);
            int blockY = Mth.floor(endY);
            int blockZ = Mth.floor(endZ);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(blockX, blockY, blockZ);
            T t = hitFunction.apply(context, mutablePos);
            if(t != null)
            {
                return t;
            }

            double deltaX = startX - endX;
            double deltaY = startY - endY;
            double deltaZ = startZ - endZ;
            int signX = Mth.sign(deltaX);
            int signY = Mth.sign(deltaY);
            int signZ = Mth.sign(deltaZ);
            double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / deltaX;
            double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / deltaY;
            double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / deltaZ;
            double d12 = d9 * (signX > 0 ? 1.0D - Mth.frac(endX) : Mth.frac(endX));
            double d13 = d10 * (signY > 0 ? 1.0D - Mth.frac(endY) : Mth.frac(endY));
            double d14 = d11 * (signZ > 0 ? 1.0D - Mth.frac(endZ) : Mth.frac(endZ));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D)
            {
                if(d12 < d13)
                {
                    if(d12 < d14)
                    {
                        blockX += signX;
                        d12 += d9;
                    }
                    else
                    {
                        blockZ += signZ;
                        d14 += d11;
                    }
                }
                else if(d13 < d14)
                {
                    blockY += signY;
                    d13 += d10;
                }
                else
                {
                    blockZ += signZ;
                    d14 += d11;
                }

                T t1 = hitFunction.apply(context, mutablePos.set(blockX, blockY, blockZ));
                if(t1 != null)
                {
                    return t1;
                }
            }

            return missFactory.apply(context);
        }
    }

    /**
     * Creates a projectile explosion for the specified entity.
     *
     * @param entity The entity to explode
     * @param radius The amount of radius the entity should deal
     * @param forceNone If true, forces the explosion mode to be NONE instead of config value
     */
    public static void createExplosion(Entity entity, float radius, boolean forceNone)
    {
        Level world = entity.level;
        if(world.isClientSide())
            return;

        Explosion.BlockInteraction mode = Config.COMMON.gameplay.enableGunGriefing.get() && !forceNone ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
        Explosion explosion = new ProjectileExplosion(world, entity, null, null, entity.getX(), entity.getY(), entity.getZ(), radius, false, mode);

        if(net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion))
            return;

        // Do explosion logic
        explosion.explode();
        explosion.finalizeExplosion(true);

        // Clears the affected blocks if mode is none
        if(mode == Explosion.BlockInteraction.NONE)
        {
            explosion.clearToBlow();
        }

        // Send event to blocks that are exploded (none if mode is none)
        explosion.getToBlow().forEach(pos ->
        {
            if(world.getBlockState(pos).getBlock() instanceof IExplosionDamageable)
            {
                ((IExplosionDamageable) world.getBlockState(pos).getBlock()).onProjectileExploded(world, world.getBlockState(pos), pos, entity);
            }
        });

        for(ServerPlayer player : ((ServerLevel) world).players())
        {
            if(player.distanceToSqr(entity.getX(), entity.getY(), entity.getZ()) < 4096)
            {
                player.connection.send(new ClientboundExplodePacket(entity.getX(), entity.getY(), entity.getZ(), radius, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
            }
        }
    }

    /**
     * Author: Forked from MrCrayfish, continued by Timeless devs
     */
    public static class EntityResult
    {
        private Entity entity;
        private Vec3 hitVec;
        private boolean headshot;

        public EntityResult(Entity entity, Vec3 hitVec, boolean headshot)
        {
            this.entity = entity;
            this.hitVec = hitVec;
            this.headshot = headshot;
        }

        /**
         * Gets the entity that was hit by the projectile
         */
        public Entity getEntity()
        {
            return this.entity;
        }

        /**
         * Gets the position the projectile hit
         */
        public Vec3 getHitPos()
        {
            return this.hitVec;
        }

        /**
         * Gets if this was a headshot
         */
        public boolean isHeadshot()
        {
            return this.headshot;
        }
    }
}
