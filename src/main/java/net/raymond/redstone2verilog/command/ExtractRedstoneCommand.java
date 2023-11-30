package net.raymond.redstone2verilog.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.raymond.redstone2verilog.block.ModBlocks;


public final class ExtractRedstoneCommand {
    // registers command to
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("verilog").executes(ExtractRedstoneCommand::run));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        // this method is run when the command is called
        // sends a message to the player
        //context.getSource().sendMessage(Text.literal("Hello, world!"));

        locateBlocks(context.getSource().getWorld(), ModBlocks.VERILOG_INPUT_BLOCK);

        return 0;
    }

    private static void locateBlocks(World world, Block block) {
        // get player positions
        PlayerEntity player = MinecraftClient.getInstance().player;

        int player_xpos = (int) player.getX();
        int player_ypos = (int) player.getY();
        int player_zpos = (int) player.getZ();

        int search_range = 100;
        // loop through nearby blocks
        for (int x = player_xpos - search_range; x < player_xpos + search_range; x++) {
            for (int y = player_ypos - search_range; y < player_ypos + search_range; y++) {
                for (int z = player_zpos - search_range; z < player_zpos + search_range; z++) {
                    checkBlocks(world, block, x, y, z, player);
                }
            }
        }
    }

    private static void checkBlocks(World world, Block startingBlock, int x, int y, int z, PlayerEntity player) {
        BlockState state = world.getBlockState(new BlockPos(x, y, z));

        if (state.isOf(startingBlock)) {
            // Do something with the block

            player.sendMessage(Text.literal("Found a " + startingBlock.getName().getString() + " block at (" + x + ", " + y + ", " + z + ")" ));

            BlockPos position = new BlockPos(x, y, z);
            position = position.offset(Direction.NORTH);

            BlockState facingBlock = world.getBlockState(position);

            while (true) {
                while (facingBlock.isOf(Blocks.REDSTONE_WIRE)) {
                    player.sendMessage(Text.literal("Found a " + facingBlock.getBlock().getName().getString() + " block at (" + position.toString() + ")" ));

                    position = position.offset(Direction.NORTH);
                    facingBlock = world.getBlockState(position);
                }



                if (facingBlock.isOf(ModBlocks.NOT_GATE_BLOCK)) {
                    player.sendMessage(Text.literal("Found a " + facingBlock.getBlock().getName().getString() + " block at (" + position.toString() + ")" ));

                    position = position.offset(Direction.NORTH);
                    facingBlock = world.getBlockState(position);
                    continue;
                }

                player.sendMessage(Text.literal("Found a " + facingBlock.getBlock().getName().getString() + " block at (" + position.toString() + ")" ));

                break;
            }
        }
    }
}
