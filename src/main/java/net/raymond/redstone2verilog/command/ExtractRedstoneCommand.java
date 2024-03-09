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
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class ExtractRedstoneCommand {
    // registers command to
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("extract_verilog").executes(ExtractRedstoneCommand::run));
    }

    /**
     * this method is run when the command is called
     */
    private static int run(CommandContext<ServerCommandSource> context) {
        // get current world
        ServerWorld world = context.getSource().getWorld();

        // extracts any redstone circuits found in current world
        RedstoneNetlist extracted_netlist = extractRedstoneNetlist(world);

        // generate a verilog netlist class from the redstone netlist
        RedstoneToVerilog.LOGGER.info("Generating Verilog Netlist from Redstone Netlist");
        VerilogNetlist generated_netlist = extracted_netlist.generateVerilogNetlist();

        // export verilog code and save it to disk
        RedstoneToVerilog.LOGGER.info("Exporting Code from Generated Verilog netlist");
        generated_netlist.exportVerilogCode();

        return 0;
    }

    private static RedstoneNetlist extractRedstoneNetlist(World world) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        RedstoneNetlist extracted_netlist = new RedstoneNetlist();
        List<BlockPos> checkedPos = new ArrayList<>();
        List<InputVerilogPort> input_blocks = new ArrayList<>();
        RedstoneNetlist foundBlocks = new RedstoneNetlist();
        RedstoneNetlist tempNetlist = new RedstoneNetlist();

        // get player positions
        assert player != null;
        int player_xpos = (int) player.getX();
        int player_ypos = (int) player.getY();
        int player_zpos = (int) player.getZ();

        // range for the program to search for input blocks
        int search_range = 100;

        searchInputBlocks(world, player_xpos, search_range, player_ypos, player_zpos, input_blocks);

        breadthFirstSearch(world, input_blocks, tempNetlist, checkedPos, extracted_netlist, foundBlocks);

        return extracted_netlist;
    }

    /**
     * @param foundBlocks
     * Uses a temporary netlist to be manipulated during the for loop, once the for loop is finished, all the contents are then shifted to the found blocks list
     */
    private static void breadthFirstSearch(World world, List<InputVerilogPort> input_blocks, RedstoneNetlist tempNetlist, List<BlockPos> checkedPos, RedstoneNetlist extracted_netlist, RedstoneNetlist foundBlocks) {
        // check all input blocks for any connected verilog blocks
        for (InputVerilogPort input_block : input_blocks) {
            tempNetlist.redstone_netlist.addAll(findConnectedRedstoneNets(world, VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK, checkedPos, new directionalBlockPos(input_block.getPort_pos(), null), extracted_netlist).getRedstone_netlist());
        }

        // shifts all temporary values into found blocks
        foundBlocks.redstone_netlist.clear();
        foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
        tempNetlist.redstone_netlist.clear();

        // Loop continues while the search is still finding blocks,
        while (!foundBlocks.getRedstone_netlist().isEmpty()) {
            for (RedstoneNet net : foundBlocks.getRedstone_netlist()) {
                RedstoneToVerilog.LOGGER.info("net is " + net.toString());

                // if position already checked, then block is a 2 input gate, can skip
                if (checkedPos.contains(net.endPos().pos())) {
                    RedstoneToVerilog.LOGGER.info("found end pos in checked pos list: " + net.endPos().pos());
                    continue;
                }

                // if ending at an output block, then add to seperate list
                if (VerilogRedstoneBlocks.getGateBlocksList().contains(net.finishing_block())) {
                    tempNetlist.redstone_netlist.addAll(findConnectedRedstoneNets(world, net.finishing_block(), checkedPos, net.endPos(), extracted_netlist).getRedstone_netlist());
                }
            }

            // shift all values from temp netlist into found blocks
            foundBlocks.redstone_netlist.clear();
            foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
            tempNetlist.redstone_netlist.clear();

            RedstoneToVerilog.LOGGER.info("Extracted netlist is: " + extracted_netlist.getRedstone_netlist().toString());
        }
    }

    private static void searchInputBlocks(World world, int player_xpos, int search_range, int player_ypos, int player_zpos, List<InputVerilogPort> input_blocks) {
        // loop through nearby coordinates and add any input blocks to a list
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
    }

    /**

     */
    private static RedstoneNetlist findConnectedRedstoneNets(World world, Block startBlock, List<BlockPos> posList, directionalBlockPos startPos, RedstoneNetlist netlist) {
        Block block = world.getBlockState(startPos.pos()).getBlock();
        List<directionalBlockPos> currentPosList = new ArrayList<>();
        List<directionalBlockPos> tempPosList = new ArrayList<>();
        List<directionalBlockPos> endPosList = new ArrayList<>();
        Direction[] directionList = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        RedstoneToVerilog.LOGGER.info("start block: " + startBlock.toString() + ", and directional block pos: " + startPos.toString() + ", redstone netlist: " + netlist);

        // guard clause to check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return netlist;
        }

        // add starting postion to position list, which will be iterated upon
        posList.add(startPos.pos());
        currentPosList.add(startPos);


        iterativeRedstoneNetSearch(world, startBlock, startPos, currentPosList, directionList, tempPosList, endPosList);

        // get ports of start and end ports using the startblock and start pos and facing direction
        String startPort = getPortName(world, startBlock, startPos.pos(), (startPos.direction() == null) ? null : world.getBlockState(startPos.pos()).get(Properties.HORIZONTAL_FACING).getOpposite());

        RedstoneNetlist returnNetlist = new RedstoneNetlist();

        String net_name = generateNetName(world, startBlock, netlist, endPosList);

        addNetsToNetlist(world, startBlock, startPos, netlist, endPosList, net_name, startPort, returnNetlist);

        RedstoneToVerilog.LOGGER.info("return netlist is: " + returnNetlist);
        return returnNetlist;
    }

    private static void addNetsToNetlist(World world, Block startBlock, directionalBlockPos startPos, RedstoneNetlist netlist, List<directionalBlockPos> endPosList, String net_name, String startPort, RedstoneNetlist returnNetlist) {
        // find the ports for all the ending blocks as nets to the netlist
        for (directionalBlockPos endDirPos: endPosList) {
            BlockPos endingPos = endDirPos.pos();
            Block endingBlock = world.getBlockState(endingPos).getBlock();
            String endingPort = getPortName(world, endingBlock, endingPos, endDirPos.direction());

            // ending port can be blank if connected to an invalid side of a gate
            if (Objects.equals(endingPort, "")) continue;

            RedstoneNet net = new RedstoneNet(net_name, startBlock, startPos, startPort, endingBlock, endDirPos, endingPort);
            netlist.addRedstoneNet(net);
            returnNetlist.addRedstoneNet(net);
        }
    }

    @NotNull
    private static String generateNetName(World world, Block startBlock, RedstoneNetlist netlist, List<directionalBlockPos> endPosList) {
        String net_name = "";
        if (startBlock.equals(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK)) {
            net_name = "in" + netlist.getInputNetSize();
        } else {
            for (directionalBlockPos endDirPos: endPosList) {
                Block endingBlock = world.getBlockState(endDirPos.pos()).getBlock();
                if (endingBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
                    net_name = "out" + netlist.getOutputNetSize();
                    break;
                }
            }
            if (net_name.isEmpty()) {
                net_name = "net" + netlist.getNetSize();
            }
        }
        return net_name;
    }

    /**
     * method to iterate search over the entire redstone net, similar to a maze solving algorithm
     */
    private static void iterativeRedstoneNetSearch(World world, Block startBlock, directionalBlockPos startPos, List<directionalBlockPos> currentPosList, Direction[] directionList, List<directionalBlockPos> tempPosList, List<directionalBlockPos> endPosList) {
        // continues searching if there are still positions in the currentPosList
        while (!currentPosList.isEmpty()) {
            // for each item in the currentPosList, look at each direction surrounding it and verify the block
            for (directionalBlockPos dirpos: currentPosList){
                for (Direction direction: directionList) {
                    // all the directional block pos contains a direction which was the incoming signal, skip the direction to avoid backtracking
                    // if it is the very start block, use the property of the block, otherwise use the direction in the dirpos object
                    // input blocks do not have a direction, so it should search in every direction
                    Block foundBlock = world.getBlockState(dirpos.pos()).getBlock();

                    if (foundBlock == startBlock
                            & foundBlock != VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
                        Direction foundDir = world.getBlockState(startPos.pos()).get(Properties.HORIZONTAL_FACING).getOpposite();
                        if (direction != foundDir) {
                            continue;
                        }
                    } else {
                        Direction dirToSkip = dirpos.direction();
                        if (dirToSkip == direction & foundBlock != startBlock) {
                            continue;
                        }
                    }

                    // get block and check for redstone wires, cross wire blocks, repeaters and any verilog blocks
                    Block neighbourBlock = world.getBlockState(dirpos.pos().offset(direction)).getBlock();
                    directionalBlockPos neighbourDirPos = new directionalBlockPos(dirpos.pos().offset(direction), direction.getOpposite());
                    if (neighbourBlock == Blocks.REDSTONE_WIRE) {
                        // continues searching in that direction
                        tempPosList.add(neighbourDirPos);
                    } else if (neighbourBlock == VerilogRedstoneBlocks.REDSTONE_WIRE_CROSS_BLOCK || neighbourBlock == Blocks.REPEATER) {
                        // skips a block in the direction found, as they both only allow for uni-directional output
                        directionalBlockPos spacedNeighbourDirPos = new directionalBlockPos(dirpos.pos().offset(direction).offset(direction), direction.getOpposite());
                        tempPosList.add(spacedNeighbourDirPos);
                    } else if (VerilogRedstoneBlocks.getGateBlocksList().contains(neighbourBlock) | neighbourBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
                        // add to end blocks positions so that outputs can be generated
                        endPosList.add(neighbourDirPos);
                    }
                }
            }

            // move all temp values into current pos list to search again
            currentPosList.clear();
            currentPosList.addAll(tempPosList);
            tempPosList.clear();

        }
    }

    @Nullable
    private static String getPortName(World world, Block facingBlock, BlockPos currentPos, Direction facingDirection) {
        //Check if block is not a verilog block
        List<Block> mod_blocks_list = VerilogRedstoneBlocks.getVerilogBlocksList();

        // guard clause to check if facing block is a verilog block
        if (!mod_blocks_list.contains(facingBlock)) {
            return null;
        }

        // input and output blocks only have one port
        if (facingBlock == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
            return "out";
        }
        if (facingBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
            return "in";
        }

        Direction block_direction = world.getBlockState(currentPos).get(Properties.HORIZONTAL_FACING);

        // one input gates only have an input and output
        if (VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "in";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "out";
            }
        }

        // latch blocks have the input "d", output "q" and an enable called "clk"
        if (VerilogRedstoneBlocks.getLatchBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "d";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "q";
            } else if (facingDirection == block_direction.rotateYCounterclockwise()) {
                return "clk";
            }
        }

        // two input gates have two inputs: "i1" and "i2" and an output "out"
        if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction.getOpposite()) {
                return "out";
            } else if (facingDirection == block_direction.rotateYClockwise()) {
                return "i1";
            } else if (facingDirection == block_direction.rotateYCounterclockwise()) {
                return "i2";
            } else {
                return "";
            }
        }
        return null;
    }
}
