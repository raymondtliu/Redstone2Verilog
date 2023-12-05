package net.raymond.redstone2verilog.command;

import java.util.ArrayList;
import java.util.List;

public class RedstoneNetlist {
    public List<RedstoneNet> getRedstone_netlist() {
        return redstone_netlist;
    }

    public RedstoneNet getLastRedstoneNet() {
        return redstone_netlist.get(redstone_netlist.size() - 1);
    }

    List<RedstoneNet> redstone_netlist = new ArrayList<>();
    public RedstoneNetlist() {
    }

    public void addRedstoneNet(RedstoneNet net) {
        this.redstone_netlist.add(net);
    }


}
