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
import net.raymond.redstone2verilog.block.ModBlocks;
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
        MinecraftClient.getInstance().player.sendMessage(Text.literal(generated_netlist.generateVerilog()));
        generated_netlist.exportVerilogCode();


        return 0;
    }

    private static RedstoneNetlist extractRedstoneNetlist(World world) {
        // get player positions
        PlayerEntity player = MinecraftClient.getInstance().player;

        RedstoneNetlist extracted_netlist = new RedstoneNetlist();

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
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ModBlocks.VERILOG_INPUT_BLOCK) {
                        input_blocks.add(new InputVerilogPort(new BlockPos(x, y, z)));
                    }
                }
            }
        }


        for (InputVerilogPort input_block : input_blocks) {
            checkRedstoneNet(world, ModBlocks.VERILOG_INPUT_BLOCK, input_block.getPort_pos(), extracted_netlist, player);

        }
        if (extracted_netlist.getLastRedstoneNet().finishing_block() == ModBlocks.NOT_GATE_BLOCK) {
            checkRedstoneNet(world, ModBlocks.NOT_GATE_BLOCK, extracted_netlist.getLastRedstoneNet().endPos(), extracted_netlist, player);
        }
        extracted_netlist.setInput_signals(input_blocks);
        extracted_netlist.setOutput_signals(output_blocks);

        return extracted_netlist;
    }

    private static void checkRedstoneNet(World world, Block startBlock, BlockPos startPos, RedstoneNetlist netlist, PlayerEntity player) {
        Block block = world.getBlockState(startPos).getBlock();

        // check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return;
        }



        Direction facingDirection = Direction.NORTH;
        BlockPos currentPos = startPos.offset(facingDirection);

        Block facingBlock = world.getBlockState(currentPos).getBlock();
        Block endingBlock;

        while (facingBlock == Blocks.REDSTONE_WIRE) {

            currentPos = currentPos.offset(facingDirection);
            facingBlock = world.getBlockState(currentPos).getBlock();
        }


        String endingPort;
        String startPort;

        startPort = getPort(world, player, startBlock, startPos, facingDirection);
        endingPort = getPort(world, player, facingBlock, currentPos, facingDirection.getOpposite());
        String net_name = "net" + netlist.getNetlistLength();

        if (endingPort == null) return;

        endingBlock = facingBlock;

        RedstoneNet net = new RedstoneNet(net_name, startBlock, startPos, startPort, endingBlock, currentPos, endingPort);
        netlist.addRedstoneNet(net);

    }

    @Nullable
    private static String getPort(World world, PlayerEntity player, Block facingBlock, BlockPos currentPos, Direction facingDirection) {
        List<Block> mod_blocks_list = List.of(ModBlocks.NOT_GATE_BLOCK,
                ModBlocks.VERILOG_INPUT_BLOCK,
                ModBlocks.VERILOG_OUTPUT_BLOCK);

        if (!mod_blocks_list.contains(facingBlock)) {
            return null;
        }

        if (facingBlock == ModBlocks.VERILOG_INPUT_BLOCK) {
            return "output";
        }
        if (facingBlock == ModBlocks.VERILOG_OUTPUT_BLOCK) {
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

    private static String setConnection() {

        return "hello";
    }
}
