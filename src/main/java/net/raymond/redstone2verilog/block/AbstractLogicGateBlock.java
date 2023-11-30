package net.raymond.redstone2verilog.block;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractLogicGateBlock extends AbstractRedstoneGateBlock {
    static final Settings SETTINGS = Settings.copy(Blocks.REPEATER);
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty INPUT_POWERED = BooleanProperty.of("input_powered");
//    public static final BooleanProperty RIGHT_INPUT_POWERED = BooleanProperty.of("right_input_powered");
    public abstract int gateLogic(World world, BlockPos pos, BlockState state);
    public String operator_symbol;

    protected AbstractLogicGateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(INPUT_POWERED, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) { return 0; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INPUT_POWERED);
//        builder.add(RIGHT_INPUT_POWERED);
        builder.add(POWERED);
        builder.add(FACING);
    }

}
