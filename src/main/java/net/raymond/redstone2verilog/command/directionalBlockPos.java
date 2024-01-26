package net.raymond.redstone2verilog.command;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record directionalBlockPos(BlockPos pos, Direction direction) {
    public directionalBlockPos flipDir() {
        return new directionalBlockPos(this.pos, this.direction.getOpposite());
    }
}
