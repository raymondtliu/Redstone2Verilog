package net.raymond.redstone2verilog.block;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.raymond.redstone2verilog.RedstoneToVerilog;

public abstract class AbstractLogicGateBlock extends AbstractRedstoneGateBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public abstract int gateLogic(World world, BlockPos pos, BlockState state);

    protected AbstractLogicGateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    // States this block can emit power
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        return gateLogic(world, pos, state);
    }

    protected int getDirectionalPower(World world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        int i = world.getEmittedRedstonePower(blockPos, direction);
        if (i >= 15) {
            return i;
        }
        BlockState blockState = world.getBlockState(blockPos);
        return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? blockState.get(RedstoneWireBlock.POWER) : 0);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) { return 0; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }

    // Sets this block can emit power level of 15 (max)
    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction != state.get(FACING)) {
            return 0;
        }
        return state.get(POWERED) ? 15:0;
    }

}
