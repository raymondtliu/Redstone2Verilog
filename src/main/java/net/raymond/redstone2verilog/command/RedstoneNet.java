package net.raymond.redstone2verilog.command;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;


public class RedstoneNet {
    private final Block starting_block;
    private final Block finishing_block;
    private final BlockPos startPos;
    private final BlockPos endPos;

    public BlockPos getStartPos() {
        return startPos;
    }
    public BlockPos getEndPos() {
        return endPos;
    }
    public Block getStarting_block() {
        return starting_block;
    }

    public Block getFinishing_block() {
        return finishing_block;
    }

    public RedstoneNet(Block starting_block, BlockPos startPos, Block finishing_block, BlockPos endPos) {
        this.starting_block = starting_block;
        this.finishing_block = finishing_block;
        this.startPos = startPos;
        this.endPos = endPos;
    }

}
