package net.raymond.redstone2verilog.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.raymond.redstone2verilog.command.ExtractRedstoneCommand;

public class ModRegistries {
    public static void registerModRegistries() {
        registerCommands();
    }

    private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(ExtractRedstoneCommand::register);
    }
}
