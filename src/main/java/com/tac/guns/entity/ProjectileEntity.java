package com.tac.guns.entity;

//import com.sun.tools.jdi.Packet;

import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import com.tac.guns.Config;
import com.tac.guns.common.BoundingBoxManager;
import com.tac.guns.common.Gun;
import com.tac.guns.common.Gun.Projectile;
import com.tac.guns.common.SpreadTracker;
import com.tac.guns.event.GunProjectileHitEvent;
import com.tac.guns.event.LevelUpEvent;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.interfaces.IExplosionDamageable;
import com.tac.guns.interfaces.IHeadshotBox;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.*;
import com.tac.guns.util.BufferUtil;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.util.math.ExtendedEntityRayTraceResult;
import com.tac.guns.world.ProjectileExplosion;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


//TODO: Blast this whole damned file and redo.
public class ProjectileEntity extends Entity implements IEntityAdditionalSpawnData
{
    private static final Predicate<Entity> PROJECTILE_TARGETS = input -> input != null && input.canBeCollidedWith() && !input.isSpectator();
    private static final Predicate<BlockState> IGNORE_LEAVES = input -> input != null && Config.COMMON.gameplay.ignoreLeaves.get() && input.getBlock() instanceof LeavesBlock;

    protected int shooterId;
    protected LivingEntity shooter;
    protected Gun modifiedGun;
    protected Gun.General general;
    protected Gun.Projectile projectile;
    private ItemStack weapon = ItemStack.EMPTY;
    private ItemStack item = ItemStack.EMPTY;
    protected float additionalDamage = 0.0F;
    protected EntitySize entitySize;
    protected double modifiedGravity;
    public int life;

    private float randomRecoilP = 0f;
    private float randomRecoilY = 0f;

    public static HashMap<PlayerEntity, Vector3d> cachePlayerPosition = new HashMap<>();
    public static HashMap<PlayerEntity, Vector3d> cachePlayerVelocity = new HashMap<>();

    public ProjectileEntity(EntityType<? extends Entity> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    public ProjectileEntity(EntityType<? extends Entity> entityType, World worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun, float randP, float randY)
    {
        this(entityType, worldIn);
        this.shooterId = shooter.getEntityId();
        this.shooter = shooter;
        this.modifiedGun = modifiedGun;
        this.general = modifiedGun.getGeneral();
        this.projectile = modifiedGun.getProjectile();
        this.entitySize = new EntitySize(this.projectile.getSize(), this.projectile.getSize(), false);
        this.modifiedGravity = modifiedGun.getProjectile().isGravity() ? GunModifierHelper.getModifiedProjectileGravity(weapon, -0.0285) : 0.0; // -0.0285 Default upcoming new -0.0125
        this.life = GunModifierHelper.getModifiedProjectileLife(weapon, this.projectile.getLife());
        this.randomRecoilP = randP;
        this.randomRecoilY = randY;

        /* Get speed and set motion */
        Vector3d dir = this.getDirection(shooter, weapon, item, modifiedGun);
        double speedModifier = GunEnchantmentHelper.getProjectileSpeedModifier(weapon);
        double speed = GunModifierHelper.getModifiedProjectileSpeed(weapon, this.projectile.getSpeed() * speedModifier);
        this.setMotion(dir.x * speed, dir.y * speed, dir.z * speed);
        this.updateHeading();

        /* Spawn the projectile half way between the previous and current position */
        double posX = shooter.lastTickPosX + (shooter.getPosX() - shooter.lastTickPosX) / 2.0;
        double posY = shooter.lastTickPosY + (shooter.getPosY() - shooter.lastTickPosY) / 2.0 + shooter.getEyeHeight();
        double posZ = shooter.lastTickPosZ + (shooter.getPosZ() - shooter.lastTickPosZ) / 2.0;
        this.setPosition(posX, posY, posZ);

        Item ammo = ForgeRegistries.ITEMS.getValue(this.projectile.getItem());
        if(ammo != null)
        {
            int customModelData = -1;
            if(weapon.getTag() != null)
            {
                if(weapon.getTag().contains("Model", Constants.NBT.TAG_COMPOUND))
                {
                    ItemStack model = ItemStack.read(weapon.getTag().getCompound("Model"));
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
    protected void registerData() {}

    @Override
    public EntitySize getSize(Pose pose)
    {
        return this.entitySize;
    }

    private Vector3d getDirection(LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun)
    {
        float gunSpread = GunModifierHelper.getModifiedSpread(weapon, modifiedGun.getGeneral().getSpread()) * GunEnchantmentHelper.getSpreadModifier(weapon);

        if(gunSpread == 0F)
        {
            return this.getVectorFromRotation(shooter.rotationPitch, shooter.rotationYaw);
        }

        if(shooter instanceof PlayerEntity)
        {
            if(!modifiedGun.getGeneral().isAlwaysSpread())
            {
                float modSpread = SpreadTracker.get((PlayerEntity) shooter).getSpread(item);
                if(modSpread != 0)
                    gunSpread *= SpreadTracker.get((PlayerEntity) shooter).getSpread(item);
                else {
                    gunSpread = modifiedGun.getGeneral().getFirstShotSpread();
                    gunSpread = GunModifierHelper.getModifiedFirstShotSpread(weapon, gunSpread);
                }
            }
            if(!SyncedPlayerData.instance().get((PlayerEntity) shooter, ModSyncedDataKeys.AIMING))
            {
                if(gunSpread < 0.5)
                    gunSpread+=0.5f;
                gunSpread *= modifiedGun.getGeneral().getHipFireInaccuracy();
                gunSpread = GunModifierHelper.getModifiedHipFireSpread(weapon, gunSpread);
                if(SyncedPlayerData.instance().get((PlayerEntity) shooter, ModSyncedDataKeys.MOVING) != 0)
                {
                    gunSpread *= Math.max(1 , (2F * ( 1 + SyncedPlayerData.instance().get((PlayerEntity) shooter, ModSyncedDataKeys.MOVING))) * modifiedGun.getGeneral().getMovementInaccuracy());
                }
            }
            if(((PlayerEntity) shooter).isCrouching() && modifiedGun.getGeneral().getProjectileAmount() == 1)
            {
                gunSpread *= 0.75F;
            }
        }

        return this.getVectorFromRotation(shooter.rotationPitch - (gunSpread / 2.0F) + rand.nextFloat() * gunSpread, shooter.rotationYaw - (gunSpread / 2.0F) + rand.nextFloat() * gunSpread);
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

        if(!this.world.isRemote())
        {
            Vector3d startVec = this.getPositionVec();
            Vector3d endVec = startVec.add(this.getMotion());
            if (this.shooter instanceof PlayerEntity) {
                Vector3d v = cachePlayerVelocity.get((PlayerEntity) shooter);
                startVec = startVec.subtract(v);
                endVec = endVec.subtract(v);
            }
            RayTraceResult result = rayTraceBlocks(this.world, new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this), IGNORE_LEAVES);
            if(result.getType() != RayTraceResult.Type.MISS)
            {
                endVec = result.getHitVec();
            }

            List<EntityResult> hitEntities = null;
            int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.COLLATERAL.get(), this.weapon);
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
                    if(((EntityRayTraceResult) result).getEntity() instanceof PlayerEntity)
                    {
                        PlayerEntity player = (PlayerEntity) ((EntityRayTraceResult) result).getEntity();

                        if(this.shooter instanceof PlayerEntity && !((PlayerEntity) this.shooter).canAttackPlayer(player) && !Config.COMMON.development.bulletSelfHarm.get())
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

        double nextPosX = this.getPosX() + this.getMotion().getX();
        double nextPosY = this.getPosY() + this.getMotion().getY();
        double nextPosZ = this.getPosZ() + this.getMotion().getZ();
        this.setPosition(nextPosX, nextPosY, nextPosZ);

        if(this.projectile.isGravity())
        {
            this.setMotion(this.getMotion().add(0, this.modifiedGravity, 0));
        }

        if(this.ticksExisted >= this.life)
        {
            if(this.isAlive())
            {
                this.onExpired();
            }
            this.remove();
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
    protected EntityResult findEntityOnPath(Vector3d startVec, Vector3d endVec)
    {
        Vector3d hitVec = null;
        Entity hitEntity = null;
        boolean headshot = false;
        List<Entity> entities = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().expand(this.getMotion()).grow(1.0), PROJECTILE_TARGETS);
        double closestDistance = Double.MAX_VALUE;
        for(Entity entity : entities)
        {
            if(!entity.equals(this.shooter) || Config.COMMON.development.bulletSelfHarm.get())
            {
                EntityResult result = this.getHitResult(entity, startVec, endVec);
                if(result == null)
                    continue;
                Vector3d hitPos = result.getHitPos();
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
    protected List<EntityResult> findEntitiesOnPath(Vector3d startVec, Vector3d endVec)
    {
        List<EntityResult> hitEntities = new ArrayList<>();
        List<Entity> entities = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().expand(this.getMotion()).grow(1.0), PROJECTILE_TARGETS);
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
    private EntityResult getHitResult(Entity entity, Vector3d startVec, Vector3d endVec)
    {
        double expandHeight = entity instanceof PlayerEntity && !entity.isCrouching() ? 0.0625 : 0.0;
        AxisAlignedBB boundingBox = entity.getBoundingBox();
        if(Config.COMMON.gameplay.improvedHitboxes.get() && entity instanceof ServerPlayerEntity && this.shooter != null)
        {
            int ping = (int) Math.floor((((ServerPlayerEntity) this.shooter).ping / 1000.0) * 20.0 + 0.5);
            boundingBox = BoundingBoxManager.getBoundingBox((PlayerEntity) entity, ping);
        }
        if (entity instanceof PlayerEntity) {
            Vector3d v = cachePlayerVelocity.get(entity);
            boundingBox = boundingBox.offset(-v.x, -v.y, -v.z);
        }
        boundingBox = boundingBox.expand(0, expandHeight, 0);

        Vector3d hitPos = boundingBox.rayTrace(startVec, endVec).orElse(null);
        Vector3d grownHitPos = boundingBox.grow(Config.COMMON.gameplay.growBoundingBoxAmountV2.get(), 0, Config.COMMON.gameplay.growBoundingBoxAmountV2.get()).rayTrace(startVec, endVec).orElse(null);
        if(hitPos == null && grownHitPos != null)
        {
            RayTraceResult raytraceresult = rayTraceBlocks(this.world, new RayTraceContext(startVec, grownHitPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this), IGNORE_LEAVES);
            if(raytraceresult.getType() == RayTraceResult.Type.BLOCK)
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
                AxisAlignedBB box = headshotBox.getHeadshotBox((LivingEntity) entity);
                if(box != null)
                {
                    box = box.offset(boundingBox.getCenter().x, boundingBox.minY, boundingBox.getCenter().z);
                    Optional<Vector3d> headshotHitPos = box.rayTrace(startVec, endVec);
                    if(!headshotHitPos.isPresent())
                    {
                        box = box.grow(Config.COMMON.gameplay.growBoundingBoxAmountV2.get(), 0, Config.COMMON.gameplay.growBoundingBoxAmountV2.get());
                        headshotHitPos = box.rayTrace(startVec, endVec);
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

    private void onHit(RayTraceResult result, Vector3d startVec, Vector3d endVec)
    {
        if(modifiedGun == null)
            return;

        MinecraftForge.EVENT_BUS.post(new GunProjectileHitEvent(result, this));

        if(result instanceof BlockRayTraceResult)
        {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) result;
            if(blockRayTraceResult.getType() == RayTraceResult.Type.MISS)
            {
                return;
            }

            Vector3d hitVec = result.getHitVec();
            BlockPos pos = blockRayTraceResult.getPos();
            BlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();

            if(block.getRegistryName().getPath().contains("_button"))
                return;

            if(Config.COMMON.gameplay.enableGunGriefing.get() && (block instanceof BreakableBlock || block instanceof PaneBlock) && state.getMaterial() == Material.GLASS)
            {
                this.world.destroyBlock(blockRayTraceResult.getPos(), false);
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

            this.onHitBlock(state, pos, blockRayTraceResult.getFace(), hitVec.x, hitVec.y, hitVec.z);

//            int fireStarterLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FIRE_STARTER.get(), this.weapon);
//            if(fireStarterLevel > 0 && Config.COMMON.gameplay.enableGunGriefing.get())
//            {
//                BlockPos offsetPos = pos.offset(blockRayTraceResult.getFace());
//                if(AbstractFireBlock.canLightBlock(this.world, offsetPos, blockRayTraceResult.getFace()))
//                {
//                    BlockState fireState = AbstractFireBlock.getFireForPlacement(this.world, offsetPos);
//                    this.world.setBlockState(offsetPos, fireState, 11);
//                }
//            }
            //TODO: Add wall pen, simple, similar to ricochet but without anything crazy nor issues caused with block-face detection
            this.remove();
            return;
        }

        if(result instanceof ExtendedEntityRayTraceResult)
        {
            ExtendedEntityRayTraceResult entityRayTraceResult = (ExtendedEntityRayTraceResult) result;
            Entity entity = entityRayTraceResult.getEntity();
            if(entity.getEntityId() == this.shooterId && !Config.COMMON.development.bulletSelfHarm.get())
            {
                return;
            }

            int fireStarterLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FIRE_STARTER.get(), this.weapon);
            if(fireStarterLevel > 0)
            {
                entity.setFire(2);
            }
            if(!entity.isAlive())
            {
                entity.hurtResistantTime = 0;
            }
            else if(entity.isAlive())
            {
                this.onHitEntity(entity, result.getHitVec(), startVec, endVec, entityRayTraceResult.isHeadshot());

                int collateralLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.COLLATERAL.get(), weapon);
                if (collateralLevel == 0)
                {
                    this.remove();
                }

                entity.hurtResistantTime = 0;
            }

        }
    }

    protected void onHitEntity(Entity entity, Vector3d hitVec, Vector3d startVec, Vector3d endVec, boolean headshot)
    {
        float damage = this.getDamage();
        float newDamage = this.getCriticalDamage(this.weapon, this.rand, damage);
        boolean critical = damage != newDamage;
        damage = newDamage;

        if(headshot)
        {
            if (Config.COMMON.gameplay.headShotDamageMultiplier.get() * this.projectile.getGunHeadDamage() >= 0)
                damage *= (Config.COMMON.gameplay.headShotDamageMultiplier.get() * this.projectile.getGunHeadDamage());
            damage *= GunModifierHelper.getAdditionalHeadshotDamage(this.weapon) == 0F ? 1F : GunModifierHelper.getAdditionalHeadshotDamage(this.weapon);
        }

        DamageSource source = new DamageSourceProjectile("bullet", this, shooter, weapon).setProjectile();
        if(entity instanceof PlayerEntity && WearableHelper.PlayerWornRig((PlayerEntity) entity) != null)
        {
            if(!WearableHelper.tickFromCurrentDurability((PlayerEntity) entity, this))
                PacketHandler.getPlayChannel().sendTo(new MessagePlayerShake((PlayerEntity) entity), ((ServerPlayerEntity)entity).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            else {
                tac_attackEntity(source, entity, damage);
            }
        }
        else {
            tac_attackEntity(source, entity, damage);
        }

        if(this.shooter instanceof PlayerEntity)
        {
            int hitType = critical ? MessageProjectileHitEntity.HitType.CRITICAL : headshot ? MessageProjectileHitEntity.HitType.HEADSHOT : MessageProjectileHitEntity.HitType.NORMAL;
            updateWeaponLevels(damage);
            PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) this.shooter), new MessageProjectileHitEntity(hitVec.x, hitVec.y, hitVec.z, hitType, entity instanceof PlayerEntity));
        }

        /* Send blood particle to tracking clients. */
        PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageBlood(hitVec.x, hitVec.y, hitVec.z));
    }

    // Apply blunt damage before applying any effects via armor
    // TODO: Use a new source type, so messages can be "Tickled to death", "Poked to smithereens" etc.
    private void tac_attackWithBluntDamage(DamageSource source, Entity entity, float damage)
    {
        entity.attackEntityFrom(source, (damage*this.modifiedGun.getProjectile().getBluntDamagePercentage()));
    }

    // tac_ is simply a naming convention for "check tac stuff before you continue this standard mc call", I use it here to explain checking config before applying it's damage, along with armor calculations
    // Is also "bulletClass" aware, makes this a bit more complex than config checks.
    private void tac_attackEntity(DamageSource source, Entity entity, float damage)
    {
        if(Config.COMMON.gameplay.bulletsIgnoreStandardArmor.get()) {
            float damageToMcArmor = 0;

            if (Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore() <= 1.0)
                damageToMcArmor = (float) (damage * (1 - Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore()));

            if (Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get() * this.projectile.getGunArmorIgnore() <= 0.0)
                damageToMcArmor = damage;

            entity.attackEntityFrom(source, damageToMcArmor); // Apply vanilla armor aware damage

            entity.hurtResistantTime = 0;
            source.setDamageBypassesArmor();
            source.setDamageIsAbsolute();
            entity.attackEntityFrom(source, (damage - damageToMcArmor)); // Apply pure damage
        }
        else
            entity.attackEntityFrom(source, damage);
    }

    protected void updateWeaponLevels(float damage)
    {
        ItemStack gunStack = this.shooter.getHeldItemMainhand();
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
                MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Post((PlayerEntity) this.shooter, gunStack));
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
        PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(pos)), new MessageProjectileHitBlock(x, y, z, pos, face));
    }

    protected void teleportToHitPoint(RayTraceResult rayTraceResult)
    {
        Vector3d hitResult = rayTraceResult.getHitVec();
        this.setPosition(hitResult.x, hitResult.y, hitResult.z);
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        this.projectile = new Gun.Projectile();
        this.projectile.deserializeNBT(compound.getCompound("Projectile"));
        this.general = new Gun.General();
        this.general.deserializeNBT(compound.getCompound("General"));
        this.modifiedGravity = compound.getDouble("ModifiedGravity");
        this.life = compound.getInt("MaxLife");
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        compound.put("Projectile", this.projectile.serializeNBT());
        compound.put("General", this.general.serializeNBT());
        compound.putDouble("ModifiedGravity", this.modifiedGravity);
        compound.putInt("MaxLife", this.life);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        buffer.writeCompoundTag(this.projectile.serializeNBT());
        buffer.writeCompoundTag(this.general.serializeNBT());
        buffer.writeInt(this.shooterId);
        BufferUtil.writeItemStackToBufIgnoreTag(buffer, this.item);
        buffer.writeDouble(this.modifiedGravity);
        buffer.writeVarInt(this.life);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        this.projectile = new Gun.Projectile();
        this.projectile.deserializeNBT(buffer.readCompoundTag());
        this.general = new Gun.General();
        this.general.deserializeNBT(buffer.readCompoundTag());
        this.shooterId = buffer.readInt();
        this.item = BufferUtil.readItemStackFromBufIgnoreTag(buffer);
        this.modifiedGravity = buffer.readDouble();
        this.life = buffer.readVarInt();
        this.entitySize = new EntitySize(this.projectile.getSize(), this.projectile.getSize(), false);
    }

    public void updateHeading()
    {
        float f = MathHelper.sqrt(this.getMotion().getX() * this.getMotion().getX() + this.getMotion().getZ() * this.getMotion().getZ());
        this.rotationYaw = (float) (MathHelper.atan2(this.getMotion().getX(), this.getMotion().getZ()) * (180D / Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(this.getMotion().getY(), (double) f) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public Projectile getProjectile()
    {
        return this.projectile;
    }

    private Vector3d getVectorFromRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vector3d((double) (f1 * f2), (double) f3, (double) (f * f2));
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

    public float getDamage()
    {
        float initialDamage = (this.projectile.getDamage() + this.additionalDamage);
        if(this.projectile.isDamageReduceOverLife())
        {
            float modifier = ((float) this.projectile.getLife() - (float) (this.ticksExisted - 1)) / (float) this.projectile.getLife();
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
        if (rand.nextFloat() < chance && Config.COMMON.gameplay.criticalDamageMultiplier.get() * this.projectile.getGunCriticalDamage() >= 0)
        {
            return (float) (damage * Config.COMMON.gameplay.criticalDamageMultiplier.get() * this.projectile.getGunCriticalDamage());
        }
        return damage;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return true;
    }

    @Override
    public void onRemovedFromWorld()
    {
        if(!this.world.isRemote)
        {
            PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(this::getDeathTargetPoint), new MessageRemoveProjectile(this.getEntityId()));
        }
    }

    private PacketDistributor.TargetPoint getDeathTargetPoint()
    {
        return new PacketDistributor.TargetPoint(this.getPosX(), this.getPosY(), this.getPosZ(), 256, this.world.getDimensionKey());
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    /**
     * A custom implementation of
     * that allows you to pass a predicate to ignore certain blocks when checking for collisions,
     * along with keeping track if passing through multiple blocks
     *
     * Should be used for checking if the first block is wallbangable, and second block being air.
     *
     * @param world     the world to perform the ray trace
     * @param context   the ray trace context
     * @param ignorePredicate the block state predicate
     * @return a result of the raytrace
     */

    private static BlockRayTraceResult rayTraceBlocksPassThrough(World world, RayTraceContext context, Predicate<BlockState> ignorePredicate)
    {
        //TODO: Reperform raytrace after first hit, if the distance of the next hit is between 0-1.1 blocks then end as a block hit, else return wallbang
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if(ignorePredicate.test(blockState)) return null;
            FluidState fluidState = world.getFluidState(blockPos);
            Vector3d startVec = rayTraceContext.getStartVec();
            Vector3d endVec = rayTraceContext.getEndVec();
            VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockRayTraceResult blockResult = world.rayTraceBlocks(startVec, endVec, blockPos, blockShape, blockState);
            VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
            BlockRayTraceResult fluidResult = fluidShape.rayTrace(startVec, endVec, blockPos);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getStartVec().squareDistanceTo(blockResult.getHitVec());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getStartVec().squareDistanceTo(fluidResult.getHitVec());
            return blockDistance <= fluidDistance ? blockResult : fluidResult;
        }, (rayTraceContext) -> {
            Vector3d Vector3d = rayTraceContext.getStartVec().subtract(rayTraceContext.getEndVec());
            return BlockRayTraceResult.createMiss(rayTraceContext.getEndVec(), Direction.getFacingFromVector(Vector3d.x, Vector3d.y, Vector3d.z), new BlockPos(rayTraceContext.getEndVec()));
        });
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
    private static BlockRayTraceResult rayTraceBlocks(World world, RayTraceContext context, Predicate<BlockState> ignorePredicate)
    {
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if(ignorePredicate.test(blockState)) return null;
            FluidState fluidState = world.getFluidState(blockPos);
            Vector3d startVec = rayTraceContext.getStartVec();
            Vector3d endVec = rayTraceContext.getEndVec();
            VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockRayTraceResult blockResult = world.rayTraceBlocks(startVec, endVec, blockPos, blockShape, blockState);
            VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
            BlockRayTraceResult fluidResult = fluidShape.rayTrace(startVec, endVec, blockPos);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getStartVec().squareDistanceTo(blockResult.getHitVec());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getStartVec().squareDistanceTo(fluidResult.getHitVec());
            return blockDistance <= fluidDistance ? blockResult : fluidResult;
        }, (rayTraceContext) -> {
            Vector3d Vector3d = rayTraceContext.getStartVec().subtract(rayTraceContext.getEndVec());
            return BlockRayTraceResult.createMiss(rayTraceContext.getEndVec(), Direction.getFacingFromVector(Vector3d.x, Vector3d.y, Vector3d.z), new BlockPos(rayTraceContext.getEndVec()));
        });
    }

    private static <T> T performRayTrace(RayTraceContext context, BiFunction<RayTraceContext, BlockPos, T> hitFunction, Function<RayTraceContext, T> missFactory)
    {
        Vector3d startVec = context.getStartVec();
        Vector3d endVec = context.getEndVec();
        if(startVec.equals(endVec))
        {
            return missFactory.apply(context);
        }
        else
        {
            double startX = MathHelper.lerp(-0.0000001, endVec.x, startVec.x);
            double startY = MathHelper.lerp(-0.0000001, endVec.y, startVec.y);
            double startZ = MathHelper.lerp(-0.0000001, endVec.z, startVec.z);
            double endX = MathHelper.lerp(-0.0000001, startVec.x, endVec.x);
            double endY = MathHelper.lerp(-0.0000001, startVec.y, endVec.y);
            double endZ = MathHelper.lerp(-0.0000001, startVec.z, endVec.z);
            int blockX = MathHelper.floor(endX);
            int blockY = MathHelper.floor(endY);
            int blockZ = MathHelper.floor(endZ);
            BlockPos.Mutable mutablePos = new BlockPos.Mutable(blockX, blockY, blockZ);
            T t = hitFunction.apply(context, mutablePos);
            if(t != null)
            {
                return t;
            }

            double deltaX = startX - endX;
            double deltaY = startY - endY;
            double deltaZ = startZ - endZ;
            int signX = MathHelper.signum(deltaX);
            int signY = MathHelper.signum(deltaY);
            int signZ = MathHelper.signum(deltaZ);
            double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / deltaX;
            double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / deltaY;
            double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / deltaZ;
            double d12 = d9 * (signX > 0 ? 1.0D - MathHelper.frac(endX) : MathHelper.frac(endX));
            double d13 = d10 * (signY > 0 ? 1.0D - MathHelper.frac(endY) : MathHelper.frac(endY));
            double d14 = d11 * (signZ > 0 ? 1.0D - MathHelper.frac(endZ) : MathHelper.frac(endZ));

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

                T t1 = hitFunction.apply(context, mutablePos.setPos(blockX, blockY, blockZ));
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
        World world = entity.world;
        if(world.isRemote())
            return;

        Explosion.Mode mode = Config.COMMON.gameplay.enableGunGriefing.get() && !forceNone ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
        Explosion explosion = new ProjectileExplosion(world, entity, null, null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), radius, false, mode);

        if(net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion))
            return;

        // Do explosion logic
        explosion.doExplosionA();
        explosion.doExplosionB(true);

        // Clears the affected blocks if mode is none
        if(mode == Explosion.Mode.NONE)
        {
            explosion.clearAffectedBlockPositions();
        }

        // Send event to blocks that are exploded (none if mode is none)
        explosion.getAffectedBlockPositions().forEach(pos ->
        {
            if(world.getBlockState(pos).getBlock() instanceof IExplosionDamageable)
            {
                ((IExplosionDamageable) world.getBlockState(pos).getBlock()).onProjectileExploded(world, world.getBlockState(pos), pos, entity);
            }
        });

        for(ServerPlayerEntity player : ((ServerWorld) world).getPlayers())
        {
            if(player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ()) < 4096)
            {
                player.connection.sendPacket(new SExplosionPacket(entity.getPosX(), entity.getPosY(), entity.getPosZ(), radius, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(player)));
            }
        }
    }

    /**
     * Author: Forked from MrCrayfish, continued by Timeless devs
     */
    public static class EntityResult
    {
        private Entity entity;
        private Vector3d hitVec;
        private boolean headshot;

        public EntityResult(Entity entity, Vector3d hitVec, boolean headshot)
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
        public Vector3d getHitPos()
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