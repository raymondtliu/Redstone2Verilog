package net.raymond.redstone2verilog.command;

public class VerilogNetlist {
    RedstoneNetlist redstone_netlist;
    public VerilogNetlist(RedstoneNetlist redstone_netlist) {
        this.redstone_netlist = redstone_netlist;
    }

    @Override
    public String toString() {
        return "module generated_netlist ( input " + "a, b," + "\n" +
                "output " + "z" + ");\n" +
                redstone_netlist.toString() + "\n" +
                "endmodule";
    }
}
