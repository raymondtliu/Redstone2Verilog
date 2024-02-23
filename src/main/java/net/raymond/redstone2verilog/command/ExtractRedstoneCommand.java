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
import net.raymond.redstone2verilog.block.RedstoneWireCrossBlock;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
        List<BlockPos> checkedPos = new ArrayList<>();

        assert player != null;
        int player_xpos = (int) player.getX();
        int player_ypos = (int) player.getY();
        int player_zpos = (int) player.getZ();

        int search_range = 100;

        List<InputVerilogPort> input_blocks = new ArrayList<>();

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

        RedstoneNetlist foundBlocks = new RedstoneNetlist();
        RedstoneNetlist tempNetlist = new RedstoneNetlist();

        for (InputVerilogPort input_block : input_blocks) {
            tempNetlist.redstone_netlist.addAll(checkRedstoneNet(world, VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK, checkedPos, new directionalBlockPos(input_block.getPort_pos(), null), extracted_netlist, player).getRedstone_netlist());
        }

        foundBlocks.redstone_netlist.clear();
        foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
        tempNetlist.redstone_netlist.clear();

        while (!foundBlocks.getRedstone_netlist().isEmpty()) {
            for (RedstoneNet net : foundBlocks.getRedstone_netlist()) {
                RedstoneToVerilog.LOGGER.info("net is " + net.toString());
                if (checkedPos.contains(net.endPos().pos())) {
                    RedstoneToVerilog.LOGGER.info("found end pos in checked pos list: " + net.endPos().pos());
                    continue;
                }

                if (VerilogRedstoneBlocks.getGateBlocksList().contains(net.finishing_block())) {
                    tempNetlist.redstone_netlist.addAll(checkRedstoneNet(world, net.finishing_block(), checkedPos, net.endPos(), extracted_netlist, player).getRedstone_netlist());
                }
            }
            foundBlocks.redstone_netlist.clear();
            foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
            tempNetlist.redstone_netlist.clear();
        }

        return extracted_netlist;
    }

    /**
     * checks the redstone net to see what blocks are connected to the starting block
     *
     */
    private static RedstoneNetlist checkRedstoneNet(World world, Block startBlock, List<BlockPos> posList, directionalBlockPos startPos, RedstoneNetlist netlist, PlayerEntity player) {
        Block block = world.getBlockState(startPos.pos()).getBlock();

        RedstoneToVerilog.LOGGER.info("start block: " + startBlock + ", and directional block pos: " + startPos + ", redstone netlist: " + netlist);
        // check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return netlist;
        }

        posList.add(startPos.pos());

        List<directionalBlockPos> currentPosList = new ArrayList<>();
        List<directionalBlockPos> tempPosList = new ArrayList<>();
        List<directionalBlockPos> endPosList = new ArrayList<>();

        currentPosList.add(startPos);

        Direction[] directionList = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        RedstoneToVerilog.LOGGER.info("currentpos list " + currentPosList);
        while (!currentPosList.isEmpty()) {
            for (directionalBlockPos dirpos:currentPosList){
                for (Direction direction:directionList) {
                    if (world.getBlockState(dirpos.pos()).getBlock() == startBlock
                            & world.getBlockState(dirpos.pos()).getBlock() != VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
                        if (direction != world.getBlockState(startPos.pos()).get(Properties.HORIZONTAL_FACING).getOpposite()) {
                            continue;
                        }
                    } else if (dirpos.direction() == direction & world.getBlockState(dirpos.pos()).getBlock() != startBlock) {
                        continue;
                    }

                    Block checkBlock = world.getBlockState(dirpos.pos().offset(direction)).getBlock();
                    if (checkBlock == Blocks.REDSTONE_WIRE) {
                        RedstoneToVerilog.LOGGER.info(direction.asString());
                        tempPosList.add(new directionalBlockPos(dirpos.pos().offset(direction), direction.getOpposite()));
                    } else if (checkBlock == VerilogRedstoneBlocks.REDSTONE_WIRE_CROSS_BLOCK || checkBlock == Blocks.REPEATER) {
                        RedstoneToVerilog.LOGGER.info(direction.asString());
                        tempPosList.add(new directionalBlockPos(dirpos.pos().offset(direction).offset(direction), direction.getOpposite()));
                    } else if (VerilogRedstoneBlocks.getGateBlocksList().contains(checkBlock) | checkBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
                        endPosList.add(new directionalBlockPos(dirpos.pos().offset(direction), direction.getOpposite()));
                    }
                }
            }

            currentPosList.clear();
            currentPosList.addAll(tempPosList);
            tempPosList.clear();

        }

        // get ports of start and end ports using the startblock and start pos and facing direction
        String startPort = getPort(world, player, startBlock, startPos.pos(), (startPos.direction() == null) ? null : world.getBlockState(startPos.pos()).get(Properties.HORIZONTAL_FACING).getOpposite());
        RedstoneToVerilog.LOGGER.info("startport: " + startPort);

        RedstoneNetlist returnNetlist = new RedstoneNetlist();

        String net_name = "";
        if (startBlock.equals(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK)) {
            net_name = "in" + netlist.getInputNetSize();
        } else {
            for (directionalBlockPos endDirPos:endPosList) {
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

        for (directionalBlockPos endDirPos:endPosList) {
            BlockPos endingPos = endDirPos.pos();
            Block endingBlock = world.getBlockState(endingPos).getBlock();

            RedstoneToVerilog.LOGGER.info("finding ending port: " + endingBlock + endingPos + endDirPos.direction());
            String endingPort = getPort(world, player, endingBlock, endingPos, endDirPos.direction());
            RedstoneToVerilog.LOGGER.info("ending port is: " + endingPort);

            if (endingPort == "") continue;

            RedstoneToVerilog.LOGGER.info("netname is: " + net_name);

            RedstoneNet net = new RedstoneNet(net_name, startBlock, startPos, startPort, endingBlock, endDirPos, endingPort);
            netlist.addRedstoneNet(net);
            returnNetlist.addRedstoneNet(net);


        }

        RedstoneToVerilog.LOGGER.info("return netlist is \n" + returnNetlist);
        return returnNetlist;
    }

    @Nullable
    private static String getPort(World world, PlayerEntity player, Block facingBlock, BlockPos currentPos, Direction facingDirection) {
        //Check if block is not a verilog block
        List<Block> mod_blocks_list = VerilogRedstoneBlocks.getVerilogBlocksList();

        if (!mod_blocks_list.contains(facingBlock)) {
            return null;
        }

        if (facingBlock == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
            return "out";
        }
        if (facingBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
            return "in";
        }

        Direction block_direction = world.getBlockState(currentPos).get(Properties.HORIZONTAL_FACING);

        if (VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "in";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "out";
            }
        }

        if (VerilogRedstoneBlocks.getLatchBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "d";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "q";
            } else if (facingDirection == block_direction.rotateYCounterclockwise()) {
                return "clk";
            }
        }

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
