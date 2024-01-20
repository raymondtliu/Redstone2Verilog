package net.raymond.redstone2verilog.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.raymond.redstone2verilog.RedstoneToVerilog;

public class VerilogRedstoneBlocks {
    public static final Block VERILOG_INPUT_BLOCK = registerBlock("verilog_input_block",
            new VerilogInputBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));
    public static final Block VERILOG_OUTPUT_BLOCK = registerBlock("verilog_output_block",
            new Block(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));
    public static final Block NOT_GATE_BLOCK = registerBlock("not_gate_block",
            new NotGateBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));
    public static final Block AND_GATE_BLOCK = registerBlock("and_gate_block",
            new AndGateBlock(FabricBlockSettings.copyOf(Blocks.COMPARATOR)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(RedstoneToVerilog.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(RedstoneToVerilog.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks() {
        RedstoneToVerilog.LOGGER.info("Registering Mod Blocks for: " + RedstoneToVerilog.MOD_ID);
    }
}
