package net.raymond.redstone2verilog;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;
import net.raymond.redstone2verilog.event.BlockPlaceHandler;
import net.raymond.redstone2verilog.item.VerilogRedstoneItemGroups;
import net.raymond.redstone2verilog.item.VerilogRedstoneItems;
import net.raymond.redstone2verilog.util.VerilogRedstoneRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneToVerilog implements ModInitializer {
	public static final String MOD_ID = "redstone2verilog";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		VerilogRedstoneItems.registerModItems();
		VerilogRedstoneItemGroups.registerItemGroups();

		VerilogRedstoneBlocks.registerModBlocks();

		VerilogRedstoneRegistries.registerModRegistries();

		UseBlockCallback.EVENT.register(new BlockPlaceHandler());


	}
}