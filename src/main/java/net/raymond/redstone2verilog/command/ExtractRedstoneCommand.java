package net.raymond.redstone2verilog.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raymond.redstone2verilog.block.ModBlocks;


public final class ExtractRedstoneCommand {
    // registers command to
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("verilog").executes(ExtractRedstoneCommand::run));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Hello, world!"));
        //checkVerilogBlocks(World., ModBlocks.VERILOG_INPUT_BLOCK);
        return 0;
    }

    private static void checkVerilogBlocks(World world, Block block) {
        // get player positions
        PlayerEntity player = MinecraftClient.getInstance().player;
        int player_xpos = (int) player.getX();
        int player_ypos = (int) player.getY();
        int player_zpos = (int) player.getZ();

        for (int x = player_xpos-100; x < player_xpos+100; x++) {
            for (int y = player_ypos-100; y < player_ypos+100; y++) {
                for (int z = player_zpos-100; z < player_zpos+100; z++) {

                    BlockState state = world.getBlockState(new BlockPos(x, y, z));
                    if (state.getBlock() == block) {
                        // Do something with the block

                        player.sendMessage(Text.literal("Found a " + block.getName() + " block at (" + x + ", " + y + ", " + z + ")"));
                    }
                }
            }
        }
    }
}
