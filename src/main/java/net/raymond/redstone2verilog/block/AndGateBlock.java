package net.raymond.redstone2verilog.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AndGateBlock extends Block {


    public AndGateBlock(Settings settings) {
        super(settings);
    }


    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty LEFT_INPUT_POWERED = BooleanProperty.of("left_input_powered");
    public static final BooleanProperty RIGHT_INPUT_POWERED = BooleanProperty.of("right_input_powered");

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

            if (state.isOf(ModBlocks.AND_GATE_BLOCK)) {
                System.out.println("i placed an AND gate");
            }

    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 15;
    }



}
