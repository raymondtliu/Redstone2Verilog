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
import net.minecraft.text.Text;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.raymond.redstone2verilog.block.ModBlocks;


public final class ExtractRedstoneCommand {
    // registers command to
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("verilog").executes(ExtractRedstoneCommand::run));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        // this method is run when the command is called

        ServerWorld world = context.getSource().getWorld();
        RedstoneNetlist extracted_netlist = extractRedstoneNetlist(world);

        MinecraftClient.getInstance().player.sendMessage(Text.literal(extracted_netlist.toString()));

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
        // loop through nearby blocks
        for (int x = player_xpos - search_range; x < player_xpos + search_range; x++) {
            for (int y = player_ypos - search_range; y < player_ypos + search_range; y++) {
                for (int z = player_zpos - search_range; z < player_zpos + search_range; z++) {
                    //check for verilog input
                    checkRedstoneNet(world, ModBlocks.VERILOG_INPUT_BLOCK, new BlockPos(x, y, z), extracted_netlist, player);
                }
            }
        }
        if (extracted_netlist.getLastRedstoneNet().getFinishing_block() == ModBlocks.NOT_GATE_BLOCK) {
            checkRedstoneNet(world, ModBlocks.NOT_GATE_BLOCK, extracted_netlist.getLastRedstoneNet().getEndPos(), extracted_netlist, player);
        }
        return extracted_netlist;
    }

    private static int checkRedstoneNet(World world, Block startBlock, BlockPos startPos, RedstoneNetlist netlist, PlayerEntity player) {
        Block block = world.getBlockState(startPos).getBlock();

        // check if coordinates lead to same block as starting block
        if (block != startBlock) {
            return 0;
        }

        player.sendMessage(Text.literal("Found a " + startBlock.getName().getString() + " block at (" + startPos.toString() + ")" ));

        Direction facingDirection = Direction.NORTH;
        BlockPos currentPos = startPos.offset(facingDirection);

        Block facingBlock = world.getBlockState(currentPos).getBlock();
        Block endingBlock;

        while (facingBlock == Blocks.REDSTONE_WIRE) {
            player.sendMessage(Text.literal("Found a " + facingBlock.getName().getString() + " block at (" + currentPos.toString() + ")" ));

            currentPos = currentPos.offset(facingDirection);
            facingBlock = world.getBlockState(currentPos).getBlock();
        }

        endingBlock = facingBlock;
        player.sendMessage(Text.literal("ending block" + endingBlock.getName().getString() + " block at (" + currentPos.toString() + ")" ));

        RedstoneNet net = new RedstoneNet(startBlock, startPos, endingBlock, currentPos);
        netlist.addRedstoneNet(net);

        return 1;
    }

    private static String setConnection() {

        return "hello";
    }
}
