package net.raymond.redstone2verilog.command;

import net.raymond.redstone2verilog.block.VerilogRedstoneBlocks;

import java.util.ArrayList;
import java.util.List;

public class RedstoneNetlist {
    public List<RedstoneNet> getRedstone_netlist() {
        return redstone_netlist;
    }

    List<RedstoneNet> redstone_netlist = new ArrayList<>();
    public RedstoneNetlist() {
    }

    public int getInputNetSize() {
        int size = 0;
        for (RedstoneNet net:this.redstone_netlist) {
            if (net.starting_block().equals(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK)) {
                size++;
            }
        }
        return size;
    }
    public int getOutputNetSize() {
        int size = 0;
        for (RedstoneNet net:this.redstone_netlist) {
            if (net.finishing_block().equals(VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK)) {
                size++;
            }
        }
        return size;
    }
    public int getNetSize() {
        int size = 0;
        for (RedstoneNet net:this.redstone_netlist) {
            if (!net.starting_block().equals(VerilogRedstoneBlocks.VERILOG_INPUT_BLOCK) & !net.finishing_block().equals(VerilogRedstoneBlocks.VERILOG_OUTPUT_BLOCK)) {
                size++;
            }
        }
        return size;
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
