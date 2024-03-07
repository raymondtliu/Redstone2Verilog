package net.raymond.redstone2verilog.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.raymond.redstone2verilog.RedstoneToVerilog;

import java.util.Properties;

public class RedstoneWireCrossBlock extends AbstractLogicGateBlock {
    protected RedstoneWireCrossBlock(Settings settings) {
        super(settings);
    }

    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        return 0;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        for (Direction dir : Direction.values()) {
            world.updateNeighbor(pos.offset(dir), this, pos);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        World world1 = MinecraftClient.getInstance().world;
        world1.scheduleBlockTick(pos, this, 1);

        if (direction == state.get(FACING)) {
            return getDirectionalPower(world1, pos, state.get(FACING));
        } else if (direction == state.get(FACING).rotateYCounterclockwise()) {
            return getDirectionalPower(world1, pos, state.get(FACING).rotateYCounterclockwise());
        }

        return 0;
    }
}
