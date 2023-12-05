package net.raymond.redstone2verilog.command;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;


public record RedstoneNet(Block starting_block, BlockPos startPos, Block finishing_block, BlockPos endPos) {

    @Override
    public String toString() {
        return starting_block.getName().getString() + " " +
                finishing_block.getName().getString() + " " +
                startPos.getX() + "," +
                startPos.getY() + "," +
                startPos.getZ() + " " +
                endPos.getX() + "," +
                endPos.getY() + "," +
                endPos.getZ()
                ;
    }
}
