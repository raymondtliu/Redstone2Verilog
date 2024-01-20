package net.raymond.redstone2verilog.block;

import net.minecraft.block.Block;

public final class VerilogOutputBlock extends Block {
    public VerilogOutputBlock(Settings settings) {
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
}
