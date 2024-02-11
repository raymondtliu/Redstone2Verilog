package net.raymond.redstone2verilog.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LatchDBlock extends AbstractLogicGateBlock{
    protected LatchDBlock(Settings settings) {
        super(settings);
    }

    @Override
    public String toString() {
        return "dlatch";
    }

    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        boolean right_powered = getDirectionalPower(world, pos, state.get(FACING).rotateYCounterclockwise()) > 0;
        boolean left_powered = getDirectionalPower(world, pos, state.get(FACING).rotateYClockwise()) > 0;

        return left_powered & right_powered ? 15:0;
    }
}
