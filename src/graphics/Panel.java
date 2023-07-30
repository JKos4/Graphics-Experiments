package graphics;

import graphics.components.GraphicsComponent;
import main.Main;
import math.ray.Ray;
import math.ray.RayIntersection;
import math.vector.Vector3;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;

import static java.awt.Color.*;

public class Panel extends JPanel {

    private BufferedImage _image;
    private Graphics2D _g2;

    Camera _c;

    public static int frame = 1;

    public static long totalTime = 0;
    public static long minFrameTime = 0;
    public static long maxFrameTime = 0;

    public static long timeSinceLastFrame = 0;
    public Panel() {
        _c = new Camera (new Vector3(0,0,0),new Vector3(1,1,0),800,600, 10);
        _image = _c.image();
        _g2 = (Graphics2D) _image.getGraphics();
        start();
    }

    public void start() {

    }

    public void camDraw() {
        _image = _c.image();
    }
    public void multiThreadedDraw() {
        int threads = 4;
        int rootNumThreads = (int)(Math.sqrt(threads));
        int threadSliceWidth = (int)(_c.resH()/rootNumThreads);
        int threadSliceHeight = (int)(_c.resV()/rootNumThreads);
        RayEvaluationThread[] threadPool = new RayEvaluationThread[threads];

        int screenLength = (int)(_c.resV()*_c.resH());
        RayIntersection[] intersections = new RayIntersection[screenLength];
        //for (int thread = 0; thread < threads; thread++) {
        //    threadPool[thread] = new RayEvaluationThread(_image, intersections);
        //}


        Ray[] rays = _c.rays();
        Ray r;
        for (int x = 0; x < Window.WIDTH; x++) {
            for (int y = 0; y < Window.HEIGHT; y++) {
                int index = y * Window.WIDTH + x;
                r = rays[index];

                int destinationThreadY = (rootNumThreads-1)-(int)(y/(_c.resV()/rootNumThreads));
                int destinationThreadX = (int)(x/(_c.resH()/rootNumThreads));
                int destinationThreadIndex = destinationThreadY*rootNumThreads+destinationThreadX;

                //threadPool[destinationThreadIndex].addRay(r,index,x,y);


            }
        }

        for (int thread = 0; thread < threads; thread++) {
            threadPool[thread].start();
        }
    }
    public void draw() {
        int screenLength = (int)(_c.resV()*_c.resH());
        Ray[] rays = _c.rays();
        RayIntersection[] intersections = new RayIntersection[screenLength];
        Ray r;
        for (int x = 0; x < Window.WIDTH; x++) {
            for (int y = 0; y < Window.HEIGHT; y++) {
                int index = y*Window.WIDTH+x;
                r = rays[index];
                for (GraphicsComponent gC : GraphicsComponent.components) {                 // For each graphics component
                    RayIntersection rayInt = gC.intersectsWith(r);                          // Check if intersection occurs
                    if (rayInt != null) {                                                   // If ray intersects
                        if (intersections[index] != null) {                                      // And there already exists an intersection at this point
                            RayIntersection previousIntersection = intersections[index];        // Get the pre-existing intersection
                            if (rayInt.depth() < previousIntersection.depth()) {                     // If the depth of the new intersection is less than the pre-existing
                                intersections[index] = rayInt;                                          // Override the current intersection
                                _image.setRGB(x, y, rayInt.color().getRGB());                             // Set the color of this pixel to the new intersection
                            } else {                                                            // Else
                                _image.setRGB(x, y, previousIntersection.color().getRGB());               // Set the color of this pixel to the previous intersection
                            }
                        } else {                                                                // If there does not already exist an intersection,
                            intersections[index] = rayInt;                                          // Override the current intersection
                            _image.setRGB(x, y, rayInt.color().getRGB());                             // Set the color of this pixel to the new intersection
                        }                                                                   //

                    } else if (GraphicsComponent.components.indexOf(gC) == GraphicsComponent.components.size()-1 && // If there is no intersect
                               intersections[index] == null) {                                                      // Previous or current for the final component
                        _image.setRGB(x, y, BLACK.getRGB());                                                        // There is no intersect at all, so mark the pixel black
                    }
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        //draw();
        long lastTime = System.currentTimeMillis();




        camDraw();

        super.paintComponent(g);
        g.drawImage(_image,0,0,null);
        long timeSinceLastFrame = (System.currentTimeMillis()-lastTime);
        totalTime = totalTime + timeSinceLastFrame;
        long averageTime = totalTime/frame;

        if (frame == 1) {
            minFrameTime = timeSinceLastFrame;
            maxFrameTime = timeSinceLastFrame;
        }

        if (timeSinceLastFrame < minFrameTime) {
            minFrameTime = timeSinceLastFrame;
        } else if (timeSinceLastFrame > maxFrameTime) {
            maxFrameTime = timeSinceLastFrame;
        }

        g.drawString("Frame: "+frame+" | FrameTime: "+timeSinceLastFrame+" | AVG: "+averageTime+" | Min: "+minFrameTime+" | Max: "+maxFrameTime, 200, 200);
        validate();

        //GraphicsComponent.polygons.get(0).location(GraphicsComponent.polygons.get(0).location().plus(new Vector3(0,0,0)));
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //if (frame % 3 == 0) {
            //_c.lookAt(GraphicsComponent.polygons.get(0).location().plus(new Vector3(0,0,0)));
        //g.drawString("\n<"+(float)(4*Math.cos(frame*.125))+", "+(float)(4*Math.sin(frame*.125))+", "+1+">", 200, 250);

        _c.goTo(new Vector3((float)(4*Math.cos(frame*.125)),(float)(4*Math.sin(frame*.125)),1));
        _c.lookAt(new Vector3(0,0,0));
        //}
        //Main.imageList[frame] = deepCopy(_image);

        /*if (frame < Main.BUFFERLENGTH-1)
            repaint();
        else {
            try {
                Main.bakeVideo("v1.gif","gif","libx264",12);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
        repaint();
        frame++;
    }



    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(), bi.getHeight());
    }
}
