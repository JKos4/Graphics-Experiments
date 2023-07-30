package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window {
    private int _sizeX, _sizeY;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private JFrame _frame;
    public Window() {
        Panel panel = new Panel();
        //panel.start();
        initFrame();
        _frame.add(panel);
    }

    private void initFrame() {
        _frame = new JFrame();
        _frame.setSize(WIDTH,HEIGHT);
        _frame.setResizable(false);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(true);
    }

    public void update(Graphics g) {

    }
}
