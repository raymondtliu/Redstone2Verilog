package net.raymond.redstone2verilog.command;

import net.minecraft.util.math.BlockPos;
import net.raymond.redstone2verilog.RedstoneToVerilog;
import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerilogNetlist {

    RedstoneNetlist redstone_netlist;
    List<BlockPos> checkedPos = new ArrayList<>();
    public VerilogNetlist(RedstoneNetlist redstone_netlist) {
        this.redstone_netlist = redstone_netlist;
    }

    public void exportVerilogCode() {
        try {
            File outputVerilogFile = new File("D:/onedrive/University of Warwick/Wachter, Eduardo - Raymond/code/generated_verilog.v");
            FileWriter filewriter = new FileWriter(outputVerilogFile);
            filewriter.write(generateVerilog());
            filewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String generateVerilog() {
        String header = buildInputOutputSignals();
        String logic = buildLogic();
        checkedPos.clear();


        return header +
                logic +
                "endmodule";
    }

    private String buildLogic() {
        StringBuilder logicString = new StringBuilder();
        List<RedstoneNet> block_inputs = new ArrayList<>();
        List<RedstoneNet> block_outputs = new ArrayList<>();

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(net.finishing_block())) {

                findConnectedNet(net, block_inputs, block_outputs);

                logicString.append("\t")
                        .append(net.finishing_block().toString())
                        .append("(")
                        .append(block_outputs.get(0).net_name())
                        .append(", ")
                        .append(net.net_name())
                        .append(");\n");

            }

            block_inputs.clear();
            block_outputs.clear();
        }

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(net.finishing_block())) {
                findConnectedNet(net, block_inputs, block_outputs);

                if (block_inputs.isEmpty() && block_outputs.isEmpty()) {
                    continue;
                }


                logicString.append("\t")
                        .append(net.finishing_block().toString())
                        .append("(")
                        .append(block_outputs.get(0).net_name())
                        .append(", ")
                        .append(block_inputs.get(0).net_name())
                        .append(", ")
                        .append(net.net_name())
                        .append(");\n");


                block_inputs.clear();
                block_outputs.clear();
            }
        }

        RedstoneToVerilog.LOGGER.info("logic string is: " + logicString.toString());
        return logicString.toString();
    }

    /**
     * Finds connected nets of each ending block of the net
     * {@return a list of nets which are connected}
     */
    private void findConnectedNet(RedstoneNet net, List<RedstoneNet> input_nets, List<RedstoneNet> output_nets) {
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
                input_nets.add(checkedNet);
                tempCheckedPos.add(net.endPos().pos());
            }

            if (net.endPos().pos().equals(checkedNet.startPos().pos())) {
                output_nets.add(checkedNet);
            }
        }

        checkedPos.addAll(tempCheckedPos);

        RedstoneToVerilog.LOGGER.info("Looking for connected net of: " + net + " and found input net: " + input_nets + " and found output net: " + output_nets);
        return;
    }

    private String buildInputOutputSignals() {
        StringBuilder header = new StringBuilder();
        String module_name = "generated_module";

        header.append("module ");
        header.append(module_name);
        header.append("(\n");

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.starting_block() == VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) {
                header.append("\tinput ").append(net.net_name()).append(",\n");
            }
        }
        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.finishing_block() == VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK) {
                header.append("\toutput ").append(net.net_name()).append(",\n");
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
