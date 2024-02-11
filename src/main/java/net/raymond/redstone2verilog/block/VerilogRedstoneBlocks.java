package net.raymond.redstone2verilog.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.raymond.redstone2verilog.RedstoneToVerilog;

import java.util.List;

public class VerilogRedstoneBlocks {
    public static final Block VERILOG_INPUT_BLOCK = registerBlock("verilog_input_block",
            new VerilogInputBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));
    public static final Block VERILOG_OUTPUT_BLOCK = registerBlock("verilog_output_block",
            new VerilogOutputBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));
    public static final Block GATE_NOT_BLOCK = registerBlock("gate_not_block",
            new GateNotBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));
    public static final Block GATE_AND_BLOCK = registerBlock("gate_and_block",
            new GateAndBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));
    public static final Block GATE_OR_BLOCK = registerBlock("gate_or_block",
            new GateOrBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));
    public static final Block LATCH_D_BLOCK = registerBlock("latch_d_block",
            new LatchDBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));


    public static List<Block> getGateBlocksList() {
        return List.of(
                VerilogRedstoneBlocks.GATE_NOT_BLOCK,
                VerilogRedstoneBlocks.GATE_AND_BLOCK,
                VerilogRedstoneBlocks.GATE_OR_BLOCK,
                VerilogRedstoneBlocks.LATCH_D_BLOCK);
    }
    public static List<Block> getOneInputGateBlocksList() {
        return List.of(
                VerilogRedstoneBlocks.GATE_NOT_BLOCK,
                VerilogRedstoneBlocks.LATCH_D_BLOCK);
    }

    public static List<Block> getTwoInputGateBlocksList() {
        return List.of(
                VerilogRedstoneBlocks.GATE_AND_BLOCK,
                VerilogRedstoneBlocks.GATE_OR_BLOCK);
    }
    public static List<Block> getVerilogBlocksList() {
        return List.of(
                VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK,
                VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK,
                VerilogRedstoneBlocks.GATE_NOT_BLOCK,
                VerilogRedstoneBlocks.GATE_AND_BLOCK,
                VerilogRedstoneBlocks.GATE_OR_BLOCK,
                VerilogRedstoneBlocks.LATCH_D_BLOCK);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(RedstoneToVerilog.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(RedstoneToVerilog.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks() {
        RedstoneToVerilog.LOGGER.info("Registering Mod Blocks for: " + RedstoneToVerilog.MOD_ID);
    }
}
