package net.raymond.redstone2verilog.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.raymond.redstone2verilog.RedstoneToVerilog;

public class VerilogRedstoneItems {
    public static final Item EXPORT_TOOL = registerItem("export_tool", new Item(new FabricItemSettings()));

    private static void addItemsToIngredientToItemGroup(FabricItemGroupEntries entries) {
        entries.add(EXPORT_TOOL);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RedstoneToVerilog.MOD_ID, name), item);
    }

    public static void registerModItems() {
        RedstoneToVerilog.LOGGER.info("Registering Mod Items for " + RedstoneToVerilog.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(VerilogRedstoneItems::addItemsToIngredientToItemGroup);
    }
}
