package net.raymond.redstone2verilog.command;

import net.raymond.redstone2verilog.block.ModBlocks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VerilogNetlist {

    RedstoneNetlist redstone_netlist;
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
        StringBuilder logic = new StringBuilder();

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.finishing_block() == ModBlocks.NOT_GATE_BLOCK) {
                logic.append("\t").append("not(");
                logic.append(net.net_name()).append(", ");

                RedstoneNet connected_net = findConnectedNet(net);

                logic.append(connected_net.net_name());
                logic.append(");\n");
            }

        }
        return logic.toString();
    }

    private RedstoneNet findConnectedNet(RedstoneNet net) {
        RedstoneNet connected_net = null;
        for(RedstoneNet net1:this.redstone_netlist.getRedstone_netlist()) {
            if (net.endPos() == net1.startPos()) {
                connected_net = net1;
            }
        }
        return connected_net;
    }

    private String buildInputOutputSignals() {
        StringBuilder header = new StringBuilder();
        String module_name = "generated_module";

        header.append("module ");
        header.append(module_name);
        header.append("(\n");

        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.starting_block() == ModBlocks.VERILOG_INPUT_BLOCK) {
                header.append("\tinput ").append(net.net_name()).append(",\n");
            }
        }
        for (RedstoneNet net:this.redstone_netlist.getRedstone_netlist()) {
            if (net.finishing_block() == ModBlocks.VERILOG_OUTPUT_BLOCK) {
                header.append("\toutput ").append(net.net_name()).append(",\n");
            }
        }

//        for (int i = 0; i <= this.redstone_netlist.getInput_signals().size(); i++) {
//            header.append("\tinput input").append(i).append(",\n");
//        }
//
//        for (int i = 0; i <= this.redstone_netlist.getOutput_signals().size(); i++) {
//            header.append("\toutput output").append(i).append(",\n");
//        }

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
