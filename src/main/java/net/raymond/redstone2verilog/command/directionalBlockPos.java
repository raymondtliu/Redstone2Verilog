package net.raymond.redstone2verilog.command;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record directionalBlockPos(BlockPos pos, Direction direction) {
}
