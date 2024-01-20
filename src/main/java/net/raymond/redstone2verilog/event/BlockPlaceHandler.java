package net.raymond.redstone2verilog.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

public class BlockPlaceHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockState state = world.getBlockState(hitResult.getBlockPos());
        if (state.getBlock() == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
            player.sendMessage(Text.literal("this is verilog input block"));
        }

        return ActionResult.PASS;
    }
}
