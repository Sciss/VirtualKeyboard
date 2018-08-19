package de.sciss.virtualkeyboard;

import de.sciss.submin.Submin;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

public class Test implements Runnable {
    public static void main(String[] args) {
        Submin.install(true);
        EventQueue.invokeLater(new Test());
    }

    public void run() {
        final VirtualKeyboard vk = new VirtualKeyboard();
        final JPanel kp = new JPanel();
        final JFrame f = new JFrame("Test");
        final JPanel p = new JPanel(new BorderLayout());
        f.setContentPane(p);
        final JTextField tx1 = new JTextField(12);
        final JTextField tx2 = new JTextField(12);
        p.add(tx1, BorderLayout.NORTH);
        p.add(kp, BorderLayout.CENTER);
        p.add(tx2, BorderLayout.SOUTH);
        vk.show(f, kp);
        kp.setPreferredSize(new Dimension(320, 160));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
