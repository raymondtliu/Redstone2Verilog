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
        return "d_latch";
    }

    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        boolean input_powered = getDirectionalPower(world, pos, state.get(FACING)) > 0;
        boolean enable_powered = getDirectionalPower(world, pos, state.get(FACING).rotateYCounterclockwise()) > 0;
        boolean output_powered = getDirectionalPower(world, pos, state.get(FACING).getOpposite()) > 0;

        if (enable_powered) {
            return input_powered ? 15:0;
        } else {
            return output_powered ? 15:0;
        }
    }
}
