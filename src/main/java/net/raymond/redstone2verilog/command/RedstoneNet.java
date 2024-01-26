package net.raymond.redstone2verilog.command;

import net.minecraft.block.Block;

public record RedstoneNet(String net_name, Block starting_block, directionalBlockPos startPos, String startPort, Block finishing_block, directionalBlockPos endPos, String endPort) {

    @Override
    public String toString() {
        return  net_name + " " +
                starting_block.getName().getString() + " " +
                finishing_block.getName().getString() + " " +
                startPort + " " +
                endPort + " " +
                startPos.pos().getX() + "," +
                startPos.pos().getY() + "," +
                startPos.pos().getZ() + " " +
                endPos.pos().getX() + "," +
                endPos.pos().getY() + "," +
                endPos.pos().getZ()
                ;
    }
}
