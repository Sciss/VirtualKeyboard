package de.sciss.virtualkeyboard;

import de.sciss.submin.Submin;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

public class Demo implements Runnable {
    public static void main(String[] args) {
        Submin.install(true);
        EventQueue.invokeLater(new Demo());
    }

    public void run() {
        final VirtualKeyboard vk = new VirtualKeyboard();
        final JFrame f = new JFrame("Demo");
        final JPanel p = new JPanel(new BorderLayout());
        f.setContentPane(p);
        final JTextField tx1 = new JTextField(12);
        final JTextField tx2 = new JTextField(12);
        p.add(tx1, BorderLayout.NORTH);
        p.add(vk , BorderLayout.CENTER);
        p.add(tx2, BorderLayout.SOUTH);
        vk.setPreferredSize(new Dimension(320, 160));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
