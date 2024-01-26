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

        RedstoneNetlist foundBlocks = new RedstoneNetlist();
        RedstoneNetlist tempNetlist = new RedstoneNetlist();

        for (InputVerilogPort input_block : input_blocks) {
            tempNetlist.redstone_netlist.addAll(checkRedstoneNet(world, VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK, new directionalBlockPos(input_block.getPort_pos(), null), extracted_netlist, player).getRedstone_netlist());
        }

        foundBlocks.redstone_netlist.clear();
        foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
        tempNetlist.redstone_netlist.clear();

        assert foundBlocks != null;

        while (!foundBlocks.getRedstone_netlist().isEmpty()) {
            for (RedstoneNet net : foundBlocks.getRedstone_netlist()) {
                RedstoneToVerilog.LOGGER.info("net is " + net.toString());
                if (VerilogRedstoneBlocks.getGateBlocksList().contains(net.finishing_block())) {
                    tempNetlist = checkRedstoneNet(world, net.finishing_block(), net.endPos(), extracted_netlist, player);
                }
            }
            foundBlocks.redstone_netlist.clear();
            foundBlocks.redstone_netlist.addAll(tempNetlist.getRedstone_netlist());
            tempNetlist.redstone_netlist.clear();
        }

        RedstoneToVerilog.LOGGER.info("setting input blocks: " + input_blocks.toString());
        extracted_netlist.setInput_signals(input_blocks);
        RedstoneToVerilog.LOGGER.info("setting output blocks: " + output_blocks.toString());
        extracted_netlist.setOutput_signals(output_blocks);

        return extracted_netlist;
    }

    /**
     * checks the redstone net to see what blocks are connected to the starting block
     *
     */
    private static RedstoneNetlist checkRedstoneNet(World world, Block startBlock, directionalBlockPos startPos, RedstoneNetlist netlist, PlayerEntity player) {
        Block block = world.getBlockState(startPos.pos()).getBlock();

        RedstoneToVerilog.LOGGER.info("start block" + startBlock + "and directional block pos" + startPos + "redstone netlist " + netlist);
        // check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return netlist;
        }

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
                    } else if (VerilogRedstoneBlocks.getGateBlocksList().contains(checkBlock) | checkBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
                        endPosList.add(new directionalBlockPos(dirpos.pos().offset(direction), direction.getOpposite()));
                        player.sendMessage(Text.of("added " + checkBlock.getName().getString() + " to list!"));
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

        // Create incremental net names
        String net_name = "net" + netlist.getNetlistLength();
        RedstoneToVerilog.LOGGER.info("netname is: " + net_name);

        RedstoneNetlist returnNetlist = new RedstoneNetlist();

        for (directionalBlockPos endDirPos:endPosList) {
            BlockPos endingPos = endDirPos.pos();
            Block endingBlock = world.getBlockState(endingPos).getBlock();

            RedstoneToVerilog.LOGGER.info("finding ending port: " + endingBlock + endingPos + endDirPos.direction());
            String endingPort = getPort(world, player, endingBlock, endingPos, endDirPos.direction());
            RedstoneToVerilog.LOGGER.info("ending port is: " + endingPort);

            if (endingPort == null) continue;

            RedstoneNet net = new RedstoneNet(net_name, startBlock, startPos, startPort, endingBlock, endDirPos, endingPort);
            netlist.addRedstoneNet(net);
            returnNetlist.addRedstoneNet(net);

            player.sendMessage(Text.of("netlist is " + netlist));

        }

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
            return "output";
        }
        if (facingBlock == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
            return "input";
        }

        Direction block_direction = world.getBlockState(currentPos).get(Properties.HORIZONTAL_FACING);

        if (VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "input";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "output";
            } else {
                return "";
            }
        }

        if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(facingBlock)) {
            if (facingDirection == block_direction) {
                return "input";
            } else if (facingDirection == block_direction.getOpposite()) {
                return "output";
            } else if (facingDirection == block_direction.rotateYClockwise()) {
                return "left_port";
            } else if (facingDirection == block_direction.rotateYCounterclockwise()) {
                return "right_port";
            } else {
                return "";
            }
        }
        return null;
    }
}
