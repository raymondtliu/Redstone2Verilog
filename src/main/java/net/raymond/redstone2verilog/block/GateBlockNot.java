package net.raymond.redstone2verilog.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GateBlockNot extends AbstractLogicGateBlock {
    public GateBlockNot(Settings settings) {
        super(settings);
    }
    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        // invert input signal
        boolean front_powered = getDirectionalPower(world, pos, state.get(FACING)) > 0;
        return front_powered ? 0:15;
    }
}
