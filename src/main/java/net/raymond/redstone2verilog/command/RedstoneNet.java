package net.raymond.redstone2verilog.command;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;


public record RedstoneNet(String net_name, Block starting_block, BlockPos startPos, String startPort, Block finishing_block, BlockPos endPos, String endPort) {

    @Override
    public String toString() {
        return  net_name + " " +
                starting_block.getName().getString() + " " +
                finishing_block.getName().getString() + " " +
                startPort + " " +
                endPort + " " +
                startPos.getX() + "," +
                startPos.getY() + "," +
                startPos.getZ() + " " +
                endPos.getX() + "," +
                endPos.getY() + "," +
                endPos.getZ()
                ;
    }
}
