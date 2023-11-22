package net.raymond.redstone2verilog.block;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;

abstract class AbstractLogicGateBlock extends AbstractRedstoneGateBlock {

    static final Settings SETTINGS = Settings.copy(Blocks.REPEATER);

    protected AbstractLogicGateBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager)
    {
        super.appendProperties(stateManager);
        stateManager.add(FACING);
        stateManager.add(POWERED);
    }
}
