package net.raymond.redstone2verilog.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;

public abstract class AbstractLogicTwoGateBlock extends AbstractLogicGateBlock{


    protected AbstractLogicTwoGateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(INPUT_POWERED, false)
                .with(LEFT_INPUT_POWERED, false)
                .with(RIGHT_INPUT_POWERED, false)
                .with(FACING, Direction.NORTH));
    }

    public static final BooleanProperty RIGHT_INPUT_POWERED = BooleanProperty.of("right_input_powered");
    public static final BooleanProperty LEFT_INPUT_POWERED = BooleanProperty.of("left_input_powered");

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(INPUT_POWERED);
        builder.add(RIGHT_INPUT_POWERED);
        builder.add(LEFT_INPUT_POWERED);
    }
}
