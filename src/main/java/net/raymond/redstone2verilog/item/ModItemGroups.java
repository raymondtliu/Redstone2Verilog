package net.raymond.redstone2verilog.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup VERILOG_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RedstoneToVerilog.MOD_ID, "verilog_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.verilog_group"))
                    .icon(() -> new ItemStack(ModItems.EXPORT_TOOL)).entries((displayContext, entries) -> {
                        entries.add(Items.REDSTONE);
                        entries.add(Items.REDSTONE_BLOCK);
                        entries.add(ModItems.EXPORT_TOOL);
                        entries.add(ModBlocks.VERILOG_INPUT_BLOCK);
                        entries.add(ModBlocks.VERILOG_OUTPUT_BLOCK);
                        entries.add(ModBlocks.NOT_GATE_BLOCK);
                    }).build());


    public static void registerItemGroups() {
        RedstoneToVerilog.LOGGER.info("Registering Item Groups for: " + RedstoneToVerilog.MOD_ID);
    }
}
