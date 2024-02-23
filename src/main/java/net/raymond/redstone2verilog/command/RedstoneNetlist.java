package net.raymond.redstone2verilog.command;

import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

import java.util.ArrayList;
import java.util.List;

public class RedstoneNetlist {
    public List<RedstoneNet> getRedstone_netlist() {
        return redstone_netlist;
    }
    int input_size_counter = 0;
    int output_size_counter = 0;
    int net_size_counter = 0;

    List<RedstoneNet> redstone_netlist = new ArrayList<>();
    public RedstoneNetlist() {
    }

    public int getInputNetSize() {
        input_size_counter++;
        return input_size_counter;
    }
    public int getOutputNetSize() {
        output_size_counter++;
        return output_size_counter;
    }
    public int getNetSize() {
        net_size_counter++;
        return net_size_counter;
    }
    public void addRedstoneNet(RedstoneNet net) {
        this.redstone_netlist.add(net);
    }


    @Override
    public String toString() {
        StringBuilder full_netlist = new StringBuilder();
        for (RedstoneNet net : this.redstone_netlist) {
            full_netlist.append(net).append("\n");
        }
        return full_netlist.toString();
    }

    public VerilogNetlist generateVerilogNetlist() {

        return new VerilogNetlist(this);
    }
}
