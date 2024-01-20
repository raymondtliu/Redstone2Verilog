package net.raymond.redstone2verilog.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AndGateBlock extends AbstractLogicTwoGateBlock{
    protected AndGateBlock(Settings settings) {
        super(settings);
    }

    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        return 0;
    }
}
