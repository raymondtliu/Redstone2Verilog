package net.raymond.redstone2verilog.block;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class NotGateBlock extends AbstractLogicGateBlock {
    public NotGateBlock(Settings settings) {
        super(settings);
    }

//    @Override
//    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//
//        if(world.isClient()) {
//            player.sendMessage(Text.literal("this is action"), false);
//        }
//        return ActionResult.PASS;
//    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (state.isOf(VerilogRedstoneBlocks.NOT_GATE_BLOCK)) {
            System.out.println("i placed an NOT gate");
        }
    }

    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        // invert input signal
        boolean front_powered = getDirectionalPower(world, pos, state.get(FACING)) > 0;

        return front_powered ? 0:15;
    }
}
