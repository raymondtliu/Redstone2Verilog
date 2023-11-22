package net.raymond.redstone2verilog.block;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

            if (state.isOf(ModBlocks.NOT_GATE_BLOCK)) {
                System.out.println("i placed an NOT gate");
            }

    }

    // States this block can emit power
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

//    @Override
//    protected int getPower(World world, BlockPos pos, BlockState state) {
//        Direction direction = state.get(FACING);
//        BlockPos blockPos = pos.offset(direction);
//        super.getPower()
//
//        return world.getEmittedRedstonePower(blockPos, direction);
//    }


    @Override
    public int gateLogic(World world, BlockPos pos, BlockState state) {
        // invert front signal
        return (getPower(world, pos, state) > 0) ? 0:15;
    }

    // Sets this block can emit power level of 15 (max)
    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction != state.get(FACING)) {
            return super.getWeakRedstonePower(state, world, pos, direction);
        }

        return !state.get(POWERED) ? 15:0;
    }



}
