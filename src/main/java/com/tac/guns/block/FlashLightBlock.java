package com.tac.guns.block;

import com.tac.guns.tileentity.FlashLightSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Material.Builder;
import net.minecraft.world.level.material.MaterialColor;

public class FlashLightBlock extends AirBlock implements EntityBlock
{
    public static final Material flashLightBlock;

    public FlashLightBlock() {
        super(Properties.of(flashLightBlock).noCollission().noDrops().air().instabreak().lightLevel((p_235470_0_) -> {
            return 15;
        }));
    }
    public int getLightValue(BlockState state, BlockGetter world, BlockPos pos) {
        return 15;
    }
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new FlashLightSource(p_153215_, p_153216_);
    }
    static {
        flashLightBlock = (new Builder(MaterialColor.NONE)).noCollider().nonSolid().build();
    }
}
