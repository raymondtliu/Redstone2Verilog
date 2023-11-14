package net.raymond.redstone2verilog;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.raymond.redstone2verilog.block.ModBlocks;
import net.raymond.redstone2verilog.item.ModItemGroups;
import net.raymond.redstone2verilog.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.minecraft.server.command.CommandManager.*;

public class RedstoneToVerilog implements ModInitializer {
	public static final String MOD_ID = "redstone2verilog";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();

		ModBlocks.registerModBlocks();


	}
}