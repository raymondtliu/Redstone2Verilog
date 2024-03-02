package net.raymond.redstone2verilog.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class VerilogNetlist {

    RedstoneNetlist redstone_netlist;
    List<BlockPos> checkedPos = new ArrayList<>();
    public VerilogNetlist(RedstoneNetlist redstone_netlist) {
        this.redstone_netlist = redstone_netlist;
    }

    public void exportVerilogCode() {
        try {
            //Get date for file name
            SimpleDateFormat dateFormatter = new SimpleDateFormat("_yyMMdd_HHmmss");

            // Generate file path
            String runDir = System.getProperty("user.dir");
            Path filePath = Paths.get(runDir, "exported_verilog", "generated_module" + dateFormatter.format(new Date()));

            // Create parent folder, no exception is thrown if it already exists
            File parentFolder = new File(filePath.getParent().toString());
            parentFolder.mkdirs();

            File outputVerilogFile = new File(filePath.toString());
            FileWriter filewriter = new FileWriter(outputVerilogFile);
            filewriter.write(generateVerilog());
            filewriter.close();

            MinecraftClient.getInstance().player.sendMessage(Text.literal("Successfully saved generated Verilog to " + outputVerilogFile));

            Util.getOperatingSystem().open(filePath.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String generateVerilog() {
        String header = buildInputOutputSignals();
        String wires = buildWires();
        String logic = buildLogic();
        checkedPos.clear();


        return header +
                wires +
                logic +
                "endmodule";
    }

    private String buildWires() {
        StringBuilder wireString = new StringBuilder();

        List<Integer> foundNets = new ArrayList<>();

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.net_name().contains("net")) {
                foundNets.add(Integer.parseInt(net.net_name().substring(3)));
            }
        }

        for (int i = 1; i <= Collections.max(foundNets); i++) {
            wireString.append("\twire net")
                    .append(i)
                    .append(";\n");
        }

        wireString.append("\n");

        return wireString.toString();
    }

    private String buildLogic() {
        StringBuilder logicString = new StringBuilder();
        List<RedstoneNet> block_inputs = new ArrayList<>();
        List<RedstoneNet> block_outputs = new ArrayList<>();
        List<RedstoneNet> block_clocks = new ArrayList<>();
        int gate_counter = 0;

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            findConnectedNet(net, block_inputs, block_outputs, block_clocks);

            if (block_inputs.isEmpty() && block_outputs.isEmpty()) {
                continue;
            }

            gate_counter++;

            logicString.append("\t")
                    .append(net.finishing_block().toString())
                    .append(" g")
                    .append(gate_counter)
                    .append(" (.")
                    .append(block_outputs.get(0).startPort())
                    .append("(")
                    .append(block_outputs.get(0).net_name())
                    .append("), ");

            if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(net.finishing_block())) {
                logicString.append(".")
                        .append(block_inputs.get(0).endPort())
                        .append("(")
                        .append(block_inputs.get(0).net_name())
                        .append("), ");
            }
            if (VerilogRedstoneBlocks.getLatchBlocksList().contains(net.finishing_block())) {
                logicString.append(".e(")
                        .append(block_clocks.get(0).net_name())
                        .append("), ");
            }
            logicString.append(".")
                    .append(net.endPort())
                    .append("(")
                    .append(net.net_name())
                    .append("));\n");

                block_inputs.clear();
                block_outputs.clear();
                block_clocks.clear();
            }

        RedstoneToVerilog.LOGGER.info("logic string is: " + logicString.toString());
        return logicString.toString();
    }

    /**
     * Finds connected nets of each ending block of the net
     * {@return a list of nets which are connected}
     */
    private void findConnectedNet(RedstoneNet net, List<RedstoneNet> input_nets, List<RedstoneNet> output_nets, List<RedstoneNet> clk_nets) {
        List<BlockPos> tempCheckedPos = new ArrayList<>();

        RedstoneToVerilog.LOGGER.info("finding connected nets of: " + net);

        // skips over checked positions
        if (checkedPos.contains(net.endPos().pos())) {
            RedstoneToVerilog.LOGGER.info("checked pos " + checkedPos + " contains end pos: " + net.endPos().pos().toString());
            return;
        }

        for(RedstoneNet checkedNet:this.redstone_netlist.getRedstone_netlist()) {
            RedstoneToVerilog.LOGGER.info("checking against net: " + checkedNet);

            if (net == checkedNet) {
                RedstoneToVerilog.LOGGER.info("Skipping same net");
                continue;
            }

            RedstoneToVerilog.LOGGER.info("checking pos " + net.endPos().pos() + " against " + checkedNet.endPos().pos());
            if (net.endPos().pos().equals(checkedNet.endPos().pos())) {
                tempCheckedPos.add(net.endPos().pos());
                if (checkedNet.endPort() == "in" | checkedNet.endPort() == "i1" | checkedNet.endPort() == "i2" | checkedNet.endPort() == "d") {
                    input_nets.add(checkedNet);
                } else if (checkedNet.endPort() == "clk") {
                    clk_nets.add(checkedNet);
                }
            }

            if (net.endPos().pos().equals(checkedNet.startPos().pos())) {
                if (checkedNet.startPort() == "clk") {
                    clk_nets.add(checkedNet);
                } else if (checkedNet.startPort() == "out" | checkedNet.startPort() == "q") {
                    output_nets.add(checkedNet);
                }
            }
        }

        checkedPos.addAll(tempCheckedPos);

        RedstoneToVerilog.LOGGER.info("Looking for connected net of: " + net + " and found input net: " + input_nets + " and found output net: " + output_nets + " and found clk net: " + clk_nets);
    }

    private String buildInputOutputSignals() {
        StringBuilder header = new StringBuilder();
        String module_name = "generated_module";

        header.append("module ");
        header.append(module_name);
        header.append("(\n");

        List<BlockPos> checkedBlocks = new ArrayList<>();

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.starting_block() == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK & !checkedBlocks.contains(net.startPos().pos())) {
                header.append("\tinput ").append(net.net_name()).append(",\n");
                checkedBlocks.add(net.startPos().pos());
            }
        }
        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.finishing_block() == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK & !checkedBlocks.contains(net.endPos().pos())) {
                header.append("\toutput ").append(net.net_name()).append(",\n");
                checkedBlocks.add(net.endPos().pos());
            }
        }

        header.delete(header.length() - 2, header.length());
        header.append(");\n\n");

        return header.toString();
    }
    @Override
    public String toString() {
        return "module generated_netlist( input " + "a, b," + "\n" +
                "output " + "z" + ");\n" +
                redstone_netlist.toString() + "\n" +
                "endmodule";
    }
}
