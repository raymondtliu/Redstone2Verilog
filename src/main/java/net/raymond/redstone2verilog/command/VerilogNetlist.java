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

        return header +
                logic +
                "endmodule";
    }

    private String buildLogic() {
        StringBuilder logicString = new StringBuilder();

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (VerilogRedstoneBlocks.getOneInputGateBlocksList().contains(net.finishing_block())) {
                // e.g. not(
                logicString.append("\t")
                        .append(net.finishing_block().toString())
                        .append("(");

                List<RedstoneNet> connected_nets = findConnectedNet(net);

                assert connected_nets.size() == 1 : "connected nets for 1 input gate is more than 1";

                logicString.append(connected_nets.get(0).net_name())
                        .append(", ")
                        .append(net.net_name())
                        .append(");\n");
            }
        }

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (VerilogRedstoneBlocks.getTwoInputGateBlocksList().contains(net.finishing_block())) {
                List<RedstoneNet> connected_nets = findConnectedNet(net);

                if (connected_nets.isEmpty()) {
                    continue;
                }

                assert connected_nets.size() == 2 : "connected nets for 2 input gate is not 2";

                // e.g. not(
                logicString.append("\t")
                        .append(net.finishing_block().toString())
                        .append("(")
                        .append(connected_nets.get(0).net_name())
                        .append(", ")
                        .append(connected_nets.get(1).net_name())
                        .append(", ")
                        .append(net.net_name())
                        .append(");\n");
            }
        }
        return logicString.toString();
    }

    /**
     * Finds connected nets of each ending block of the net
     * {@return a list of nets which are connected}
     */
    private List<RedstoneNet> findConnectedNet(RedstoneNet net) {
        List<RedstoneNet> connected_nets = new ArrayList<>();
        List<RedstoneNet> connected_input_nets = new ArrayList<>();
        List<RedstoneNet> connected_output_nets = new ArrayList<>();

        List<BlockPos> tempCheckedPos = new ArrayList<>();

        RedstoneToVerilog.LOGGER.info("finding connected nets of: " + net);

        // skips over checked positions
        if (checkedPos.contains(net.endPos().pos())) {
            RedstoneToVerilog.LOGGER.info("checked pos " + checkedPos + " contains end pos: " + net.endPos().pos().toString());
            return connected_nets;
        }

        for(RedstoneNet checkedNet:this.redstone_netlist.getRedstone_netlist()) {
            RedstoneToVerilog.LOGGER.info("checking against net: " + checkedNet);

            if (net == checkedNet) {
                continue;
            }

            RedstoneToVerilog.LOGGER.info("checking pos " + net.endPos().pos() + " against " + checkedNet.endPos().pos());
            if (net.endPos().pos().equals(checkedNet.endPos().pos())) {
                connected_input_nets.add(checkedNet);
                tempCheckedPos.add(net.endPos().pos());
            }

            if (net.endPos().pos().equals(checkedNet.startPos().pos())) {
                connected_output_nets.add(checkedNet);
            }
        }

        connected_nets.addAll(connected_output_nets);
        connected_nets.addAll(connected_input_nets);

        checkedPos.addAll(tempCheckedPos);

        RedstoneToVerilog.LOGGER.info("Looking for connected net of: " + net + " and found connected net: " + connected_nets);
        return connected_nets;
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
