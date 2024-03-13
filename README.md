[7seg_minecraft]: https://github.com/raymondtliu/Redstone2Verilog/blob/master/screenshots/7seg_minecraft.jpg?raw=true 
[7seg_sim]: https://github.com/raymondtliu/Redstone2Verilog/blob/master/screenshots/7seg_sim.png?raw=true 
[redstone2verilog_blocks]: https://github.com/raymondtliu/Redstone2Verilog/blob/master/screenshots/redstone2verilog_blocks.jpg?raw=true 

# Redstone2Verilog
A Minecraft Mod to convert combinational logic circuits from Minecraft redstone to Verilog HDL

## About
Redstone2Verilog is an undergraduate project in the University of Warwick investigating the use of GBL (Game Based Learning) for electronics, specifically at teaching HDL (Hardware Description Language). The deliverable of the project was to create a mod that can generate Verilog code from a circuit within Minecraft.

## Usage
To run the tool, run "extract_verilog" within the game.
This will search the nearby area for any input blocks and then perform a search to find all connected nets, then generate Verilog code for the circuit in the game.

![redstone2verilog_blocks][redstone2verilog_blocks] 

## Example
To demonstrate the tool, a 7-segment display was created in Minecraft.

![7seg_minecraft][7seg_minecraft]

This generates the following Verilog code:

``` verilog 
module generated_module(
	input in1,
	input in2,
	input in3,
	input in4,
	output out1,
	output out2,
	output out3,
	output out4,
	output out5,
	output out6,
	output out7);

	wire net1;
	wire net2;
	wire net3;
	wire net4;
	wire net5;
	wire net6;
	wire net7;
	wire net8;
	wire net9;
	wire net10;
	wire net11;
	wire net12;
	wire net13;
	wire net14;
	wire net15;
	wire net16;
	wire net17;
	wire net18;
	wire net19;
	wire net20;
	wire net21;
	wire net22;
	wire net23;
	wire net24;

	or g1 (net2, net18, in1);
	or g2 (net3, net8, in1);
	or g3 (net4, in3, in1);
	or g4 (net5, net7, in1);
	not g5 (net6, in2);
	and g6 (net7, net10, in2);
	and g7 (net8, net14, in2);
	or g8 (out1, net16, in2);
	and g9 (net9, in4, in2);
	not g10 (net10, in3);
	and g11 (net11, net14, in3);
	and g12 (net12, net6, in3);
	and g13 (net13, in4, in3);
	not g14 (net14, in4);
	and g15 (net15, net7, in4);
	or g16 (net16, net10, in4);
	or g17 (net17, net11, net2);
	or g18 (out2, net20, net3);
	or g19 (out3, net21, net4);
	or g20 (out4, net23, net5);
	and g21 (net18, net14, net6);
	or g22 (net19, net22, net6);
	or g23 (net20, net22, net7);
	or g24 (net21, net18, net9);
	and g25 (net22, net14, net10);
	or g26 (out5, net18, net11);
	or g27 (net23, net12, net11);
	or g28 (net24, net15, net12);
	or g29 (out6, net19, net13);
	or g30 (out7, net24, net17);
endmodule
```

And testing it in Xilinx Vivado provides the following result:
![7seg_sim][7seg_sim] 

## License
MIT License
