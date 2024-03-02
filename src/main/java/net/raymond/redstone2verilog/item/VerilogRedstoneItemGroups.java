package net.raymond.redstone2verilog.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

public class VerilogRedstoneItemGroups {
    public static final ItemGroup VERILOG_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RedstoneToVerilog.MOD_ID, "verilog_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.verilog_group"))
                    .icon(() -> new ItemStack(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK)).entries((displayContext, entries) -> {
                        entries.add(Items.REDSTONE);
                        entries.add(Items.LEVER);
                        entries.add(Items.REPEATER);
                        entries.add(Items.REDSTONE_LAMP);

                        entries.add(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK);
                        entries.add(VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK);

                        entries.add(VerilogRedstoneBlocks.GATE_NOT_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_AND_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_OR_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_NAND_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_NOR_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_XNOR_BLOCK);
                        entries.add(VerilogRedstoneBlocks.GATE_XOR_BLOCK);

                        entries.add(VerilogRedstoneBlocks.LATCH_D_BLOCK);

                        entries.add(VerilogRedstoneBlocks.REDSTONE_WIRE_CROSS_BLOCK);
                    }).build());


    public static void registerItemGroups() {
        RedstoneToVerilog.LOGGER.info("Registering Item Groups for: " + RedstoneToVerilog.MOD_ID);
    }
}
