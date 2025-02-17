package net.raymond.redstone2verilog.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

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

    /**
     * main function for writing the Verilog code from the Verilog netlist
     */
    public void exportVerilogCode() {
        try {
            //Get date for file name
            SimpleDateFormat dateFormatter = new SimpleDateFormat("_yyMMdd_HHmmss");

            // Generate file path
            String runDir = System.getProperty("user.dir");
            Path filePath = Paths.get(runDir, "exported_verilog", "generated_module" + dateFormatter.format(new Date()) + ".v");
            Path dlatchPath = Paths.get(runDir, "exported_verilog", "d_latch.v");

            // Create parent folder, no exception is thrown if it already exists
            File parentFolder = new File(filePath.getParent().toString());
            parentFolder.mkdirs();

            File outputVerilogFile = new File(filePath.toString());
            File dlatchFile = new File(dlatchPath.toString());

            // write dlatch file
            if (!dlatchFile.exists()) {
                try {
                    FileWriter dlatchFileWriter = new FileWriter(dlatchFile);
                    dlatchFileWriter.write(getDlatchString());
                    dlatchFileWriter.close();
                    System.out.println("File created successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // write to file
            FileWriter filewriter = new FileWriter(outputVerilogFile);
            RedstoneToVerilog.LOGGER.info("Generating Verilog...");
            filewriter.write(generateVerilog());
            filewriter.close();

            MinecraftClient.getInstance().player.sendMessage(Text.literal("Successfully saved generated Verilog to " + outputVerilogFile));

            Util.getOperatingSystem().open(filePath.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return string containing Verilog module for D-Latch
     */
    public String getDlatchString() {
        return """
                // D latch (gate model)
                module d_latch(
                  input d, e,
                  output q);

                  wire s, r, nd, nq;

                  nor g1(q, r, nq);
                  nor g2(nq, s, q);
                  and g3(r, e, nd);
                  and g4(s, e, d);
                  not g5(nd, d);

                endmodule""";
    }

    /**
     * calls on different functions to generate the Verilog from object's netlist
     * @return full generated Verilog string
     */
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

    /**
     * Checks for maximum number for wires and declares a wire until the number equals the max
     */
    private String buildWires() {
        StringBuilder wireString = new StringBuilder();

        List<Integer> foundNets = new ArrayList<>();

        // reads every net to find names with "net" and retrieve the number at the end
        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.net_name().contains("net")) {
                foundNets.add(Integer.parseInt(net.net_name().substring(3)));
            }
        }

        // Returns empty string if there are no intermediate wires
        if (foundNets.isEmpty()) {
            return "";
        }

        for (int i = 1; i <= Collections.max(foundNets); i++) {
            wireString.append("\twire net")
                    .append(i)
                    .append(";\n");
        }

        wireString.append("\n");

        RedstoneToVerilog.LOGGER.info("Generated wire declarations: " + wireString);

        return wireString.toString();
    }

    /**
     * build the logic of the object's netlist by looking at one net, then comparing to all the others to find any connections, making sure to skip any checked locations
     */
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

            boolean isGate = VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(net.finishing_block()) || VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(net.finishing_block());

            gate_counter++;

            logicString.append("\t")
                    .append(net.finishing_block().toString())
                    .append(" g")
                    .append(gate_counter)
                    .append(" (");

            if (!isGate) {
                logicString.append(".")
                        .append(block_outputs.get(0).startPort())
                        .append("(");
            }

            logicString.append(block_outputs.get(0).net_name());

            if (!isGate) {
                logicString.append(")");
            }

            logicString.append(", ");

            if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(net.finishing_block())) {
//                logicString.append(".")
//                        .append(block_inputs.get(0).endPort())
//                        .append("(")
                logicString.append(block_inputs.get(0).net_name())
                        .append(", ");
            }
            if (VerilogRedstoneBlocks.getLatchBlocksList().contains(net.finishing_block())) {
                logicString.append(".e(")
                        .append(block_clocks.get(0).net_name())
                        .append("), ");
            }

            if (!isGate) {
                logicString.append(".")
                        .append(net.endPort())
                        .append("(");
            }

            logicString.append(net.net_name());

            if (!isGate) {
                logicString.append(")");
            }

            logicString.append(");\n");

            block_inputs.clear();
            block_outputs.clear();
            block_clocks.clear();
        }

        RedstoneToVerilog.LOGGER.info("Generated logic string: " + logicString);

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

            // skips nets that end in clk, this d latch will be covered by the net that has input as endport
            if (net.endPort() == "clk") {
                continue;
            }

            if (net.endPos().pos().equals(checkedNet.endPos().pos())) {
                RedstoneToVerilog.LOGGER.info("found connected block!");
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

    /**
     * Builds input and output signals by looking through the netlist and building a port for any net names that have "input" and "output" in them
     */
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

        RedstoneToVerilog.LOGGER.info("Generated header text: " + header);

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
