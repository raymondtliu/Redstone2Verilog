package net.raymond.redstone2verilog.command;

import net.minecraft.util.math.BlockPos;

public class VerilogPort {
    public BlockPos getPort_pos() {
        return port_pos;
    }

    private final BlockPos port_pos;

    public VerilogPort(BlockPos port_pos) {
        this.port_pos = port_pos;
    }
}
