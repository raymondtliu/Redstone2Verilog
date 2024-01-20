package net.raymond.redstone2verilog.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class ExtractRedstoneCommand {
    // registers command to
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("verilog").executes(ExtractRedstoneCommand::run));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        // this method is run when the command is called

        ServerWorld world = context.getSource().getWorld();
        RedstoneNetlist extracted_netlist = extractRedstoneNetlist(world);

        // MinecraftClient.getInstance().player.sendMessage(Text.literal(extracted_netlist.toString()));

        VerilogNetlist generated_netlist = extracted_netlist.generateVerilogNetlist();
        // MinecraftClient.getInstance().player.sendMessage(Text.literal(generated_netlist.toString()));
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.literal(generated_netlist.generateVerilog()));
        generated_netlist.exportVerilogCode();


        return 0;
    }

    private static RedstoneNetlist extractRedstoneNetlist(World world) {
        // get player positions
        PlayerEntity player = MinecraftClient.getInstance().player;

        RedstoneNetlist extracted_netlist = new RedstoneNetlist();

        assert player != null;
        int player_xpos = (int) player.getX();
        int player_ypos = (int) player.getY();
        int player_zpos = (int) player.getZ();

        int search_range = 100;

        List<InputVerilogPort> input_blocks = new ArrayList<>();
        List<OutputVerilogPort> output_blocks = new ArrayList<>();

        // loop through nearby blocks
        for (int x = player_xpos - search_range; x < player_xpos + search_range; x++) {
            for (int y = player_ypos - search_range; y < player_ypos + search_range; y++) {
                for (int z = player_zpos - search_range; z < player_zpos + search_range; z++) {
                    //check for verilog input
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
                        input_blocks.add(new InputVerilogPort(new BlockPos(x, y, z)));
                    }
                }
            }
        }

        RedstoneNetlist foundBlocks = null;



        for (InputVerilogPort input_block : input_blocks) {

            foundBlocks = checkRedstoneNet(world, VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK, new directionalBlockPos(input_block.getPort_pos(), null), extracted_netlist, player);
        }

        assert foundBlocks != null;
        for (RedstoneNet net : foundBlocks.getRedstone_netlist()) {
            if (net.finishing_block() == VerilogRedstoneBlocks.GATE_NOT_BLOCK) {
                player.sendMessage(Text.of("found finishing block" + net.finishing_block().toString()));

                checkRedstoneNet(world, VerilogRedstoneBlocks.GATE_NOT_BLOCK, new directionalBlockPos(net.endPos(), Direction.SOUTH), extracted_netlist, player);
            }
        }

        extracted_netlist.setInput_signals(input_blocks);
        extracted_netlist.setOutput_signals(output_blocks);

        return extracted_netlist;
    }

    /**
     * checks the redstone net to see what blocks are connected to the starting block
     *
     * @return
     */
    private static RedstoneNetlist checkRedstoneNet(World world, Block startBlock, directionalBlockPos startPos, RedstoneNetlist netlist, PlayerEntity player) {
        Block block = world.getBlockState(startPos.pos()).getBlock();

        // check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return netlist;
        }

        List<directionalBlockPos> currentPosList = new ArrayList<>();
        List<directionalBlockPos> tempPosList = new ArrayList<>();
        List<directionalBlockPos> endPosList = new ArrayList<>();

        currentPosList.add(startPos);

        Direction facingDirection = Direction.NORTH;
        Direction[] directionList = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        Block[] modBlocksList = {VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK, VerilogRedstoneBlocks.GATE_NOT_BLOCK};

        while (!currentPosList.isEmpty()) {
            for (directionalBlockPos pos:currentPosList){
                for (Direction direction:directionList) {

                    if (pos.direction() == direction) {
                        continue;
                    }

                    Block checkBlock = world.getBlockState(pos.pos().offset(direction)).getBlock();
                    if (checkBlock == Blocks.REDSTONE_WIRE) {
                        player.sendMessage(Text.of(direction.asString()));
                        tempPosList.add(new directionalBlockPos(pos.pos().offset(direction), direction.getOpposite()));
                    } else if (Arrays.asList(modBlocksList).contains(checkBlock)) {
                        endPosList.add(new directionalBlockPos(pos.pos().offset(direction), direction.getOpposite()));
                        player.sendMessage(Text.of("added end block to list!"));
                    }
                }
            }

            currentPosList.clear();
            currentPosList.addAll(tempPosList);
            tempPosList.clear();

        }

        // get ports of start and end ports using the startblock and start pos and facing direction
        String startPort = getPort(world, player, startBlock, startPos.pos(), facingDirection);

        // Create incremental net names
        String net_name = "net" + netlist.getNetlistLength();

        RedstoneNetlist returnNetlist = new RedstoneNetlist();

        for (directionalBlockPos endPos:endPosList) {
            BlockPos endingPos = endPos.pos();
            Block endingBlock = world.getBlockState(endingPos).getBlock();
            String endingPort = getPort(world, player, endingBlock, endingPos, facingDirection.getOpposite());
            if (endingPort == null) continue;

            RedstoneNet net = new RedstoneNet(net_name, startBlock, startPos.pos(), startPort, endingBlock, endingPos, endingPort);
            netlist.addRedstoneNet(net);
            returnNetlist.addRedstoneNet(net);
            player.sendMessage(Text.of("netlist is " + netlist));

        }

        return returnNetlist;
    }

    @Nullable
    private static String getPort(World world, PlayerEntity player, Block facingBlock, BlockPos currentPos, Direction facingDirection) {
        List<Block> mod_blocks_list = List.of(VerilogRedstoneBlocks.GATE_NOT_BLOCK,
                VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK,
                VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK);

        if (!mod_blocks_list.contains(facingBlock)) {
            return null;
        }

        if (facingBlock == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
            return "output";
        }
        if (facingBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
            return "input";
        }

        Direction block_direction = world.getBlockState(currentPos).get(Properties.HORIZONTAL_FACING);

        if (facingDirection.getOpposite() == block_direction) {
            return "output";
        } else if (block_direction == facingDirection) {
            return "input";
        } else {
            return null;
        }
    }
}
