package net.raymond.redstone2verilog.command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParentFrame extends JFrame implements ActionListener {
    ParentFrame() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.pack();
        this.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
