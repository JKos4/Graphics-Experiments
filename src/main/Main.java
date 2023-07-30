package main;

import graphics.*;
import graphics.Panel;
import graphics.Window;
import graphics.components.*;
import graphics.components.Polygon;
import math.vector.Vector3;

import java.awt.*;
import java.awt.image.BufferedImage;
import static java.awt.Color.*;

public class Main {
    public static final int BUFFERLENGTH = 10;
    public static int t = 0;
    public static BufferedImage[] imageList = new BufferedImage[BUFFERLENGTH];

    public static void main(String[] args) throws InterruptedException {
        Tri t = new Tri(3, 3, (float) 0.5, 3, 3, (float) -0.5, 2, 4, (float) -0.5);
        Tri t2 = new Tri(3, 3, (float) 0.5, 2, 4, (float) -0.5, 2, 4, 1);

        Vector3[] points = {new Vector3(0, 0, 0),
                new Vector3(1, 0, 0),
                new Vector3(1, 1, 0),
                new Vector3(0, 1, 0),

                new Vector3(0, 0, (float) .5),

                new Vector3(0, 1, 1),
                new Vector3(1, 0, 1),
                new Vector3(1, 1, 1),
                new Vector3((float) .5, 0, 1),
                new Vector3(0, (float) .5, 1)};
        int[][][] faces = {
                {{0, 6, 1},
                        {0, 8, 6},// Blue
                        {0, 4, 8},
                },

                {{0, 3, 5},
                        {0, 5, 9},// Yellow
                        {0, 9, 4},
                },

                {
                        {1, 2, 7},// Orange
                        {1, 7, 6},
                },

                {
                        {2, 3, 5},// Magenta
                        {2, 5, 7},
                },

                {
                        {7, 6, 5},// Green
                        {5, 6, 8},
                        {5, 8, 9},
                },

                {{4, 8, 9}} // Red
        };

        Color[] colors = {BLUE,
                YELLOW,
                ORANGE,
                MAGENTA,
                GREEN,
                RED};

        Plane pl = new Plane(0, 0, -2, 0, 1, -2, 1, 0, -2);

        Polygon notchedCube = new Polygon(new Vector3((float) -.5, -(float) .5, -1), points, faces, colors);
        Panel p = new Panel();
        Window w = new Window();

    }
}
