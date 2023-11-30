package net.raymond.redstone2verilog.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.state.property.Properties;

public final class VerilogInputBlock extends Block {
    public VerilogInputBlock(Settings settings) {
        super(settings);
    }

}
