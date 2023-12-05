package net.raymond.redstone2verilog.command;

import java.util.ArrayList;
import java.util.List;

public class RedstoneNetlist {
    public void setInput_signals(List<InputVerilogPort> input_signals) {
        this.input_signals = input_signals;
    }

    public void setOutput_signals(List<OutputVerilogPort> output_signals) {
        this.output_signals = output_signals;
    }

    public List<InputVerilogPort> getInput_signals() {
        return input_signals;
    }

    public List<OutputVerilogPort> getOutput_signals() {
        return output_signals;
    }

    private List<InputVerilogPort> input_signals;
    private List<OutputVerilogPort> output_signals;

    public List<RedstoneNet> getRedstone_netlist() {
        return redstone_netlist;
    }

    public RedstoneNet getLastRedstoneNet() {
        return redstone_netlist.get(redstone_netlist.size() - 1);
    }

    List<RedstoneNet> redstone_netlist = new ArrayList<>();
    public RedstoneNetlist() {
    }

    public int getNetlistLength() {
        return this.redstone_netlist.size();
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
