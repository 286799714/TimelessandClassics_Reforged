package com.tac.guns.world;

import com.google.common.collect.Sets;
import com.tac.guns.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ProjectileExplosion extends Explosion
{
    private static final ExplosionDamageCalculator DEFAULT_CONTEXT = new ExplosionDamageCalculator();

    private static final Predicate<BlockState> IGNORES = input -> input != null && ((Config.COMMON.gameplay.ignoreLeaves.get() && input.getBlock() instanceof LeavesBlock) ||
            (Config.COMMON.gameplay.ignoreFences.get() && (input.getBlock() instanceof FenceBlock || input.getBlock() instanceof IronBarsBlock)) ||
            (Config.COMMON.gameplay.ignoreFenceGates.get() && input.getBlock() instanceof FenceGateBlock));
    private final Level world;
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final float radius;
    private final Entity exploder;
    private final ExplosionDamageCalculator context;

    public ProjectileExplosion(Level world, Entity exploder, @Nullable DamageSource source, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float power, float radius, BlockInteraction mode)
    {
        super(world, exploder, source, context, x, y, z, radius, Config.COMMON.gameplay.explosionCauseFire.get(), mode);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.radius = radius;
        this.exploder = exploder;
        this.context = context == null ? DEFAULT_CONTEXT : context;
    }

    @Override
    public void explode()
    {
        Set<BlockPos> set = Sets.newHashSet();
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        double d0 = ((float) x / 15.0F * 2.0F - 1.0F);
                        double d1 = ((float) y / 15.0F * 2.0F - 1.0F);
                        double d2 = ((float) z / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.radius * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double blockX = this.x;
                        double blockY = this.y;
                        double blockZ = this.z;

                        for (; f > 0.0F; f -= 0.225F) {
                            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
                            BlockState blockState = this.world.getBlockState(pos);
                            FluidState fluidState = this.world.getFluidState(pos);
                            Optional<Float> optional = this.context.getBlockExplosionResistance(this, this.world, pos, blockState, fluidState);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.context.shouldBlockExplode(this, this.world, pos, blockState, f)) {
                                set.add(pos);
                            }

                            blockX += d0 * (double) 0.3F;
                            blockY += d1 * (double) 0.3F;
                            blockZ += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.getToBlow().addAll(set);
        float radius = this.radius;
        int minX = Mth.floor(this.x - (double) radius - 1.0D);
        int maxX = Mth.floor(this.x + (double) radius + 1.0D);
        int minY = Mth.floor(this.y - (double) radius - 1.0D);
        int maxY = Mth.floor(this.y + (double) radius + 1.0D);
        int minZ = Mth.floor(this.z - (double) radius - 1.0D);
        int maxZ = Mth.floor(this.z + (double) radius + 1.0D);
        radius *= 2;
        List<Entity> entities = this.world.getEntities(this.exploder, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, entities, radius);
        Vec3 explosionPos = new Vec3(this.x, this.y, this.z);

        for (Entity entity : entities) {
            if (entity.ignoreExplosion())
                continue;

            AABB boundingBox = entity.getBoundingBox();
            HitResult result;
            double strength;
            double deltaX;
            double deltaY;
            double deltaZ;
            double minDistance = radius;

            Vec3[] d = new Vec3[15];

            if (!(entity instanceof LivingEntity)) {
                Vec3 entityVec = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                strength = entityVec.distanceTo(explosionPos) * 2 / radius;
                deltaX = entity.getX() - this.x;
                deltaY = entity.getY() - this.y;
                deltaZ = entity.getZ() - this.z;
            } else {
                deltaX = (boundingBox.maxX + boundingBox.minX) / 2;
                deltaY = (boundingBox.maxY + boundingBox.minY) / 2;
                deltaZ = (boundingBox.maxZ + boundingBox.minZ) / 2;
                d[0] = new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                d[1] = new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
                d[2] = new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
                d[3] = new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
                d[4] = new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
                d[5] = new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
                d[6] = new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
                d[7] = new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                d[8] = new Vec3(boundingBox.minX, deltaY, deltaZ);
                d[9] = new Vec3(boundingBox.maxX, deltaY, deltaZ);
                d[10] = new Vec3(deltaX, boundingBox.minY, deltaZ);
                d[11] = new Vec3(deltaX, boundingBox.maxY, deltaZ);
                d[12] = new Vec3(deltaX, deltaY, boundingBox.minZ);
                d[13] = new Vec3(deltaX, deltaY, boundingBox.maxZ);
                d[14] = new Vec3(deltaX, deltaY, deltaZ);
                for (int i = 0; i < 15; i++) {
                    result = rayTraceBlocks(this.world, new ClipContext(explosionPos, d[i], ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null), IGNORES);
                    minDistance = (result.getType() != HitResult.Type.BLOCK) ? Math.min(minDistance, explosionPos.distanceTo(d[i])) : minDistance;
                }
                strength = minDistance * 2 / radius;
                deltaX -= this.x;
                deltaY -= this.y;
                deltaZ -= this.z;
            }

            if (strength > 1.0D)
                continue;

            double distanceToExplosion = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));

            if (distanceToExplosion != 0.0D) {
                deltaX /= distanceToExplosion;
                deltaY /= distanceToExplosion;
                deltaZ /= distanceToExplosion;
            } else {
                // Fixes an issue where explosion exactly on the player would cause no damage
                deltaX = 0.0;
                deltaY = 1.0;
                deltaZ = 0.0;
            }

            double damage = 1.0D - strength;
            entity.hurt(this.getDamageSource(), (float) damage * this.power);

            if (entity instanceof LivingEntity) {
                damage = (float) ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, damage);
            }

            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * damage, deltaY * damage, deltaZ * damage));
            if (entity instanceof Player player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    this.getHitPlayers().put(player, new Vec3(deltaX * damage, deltaY * damage, deltaZ * damage));
                }
            }
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
}