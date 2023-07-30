package graphics;

import graphics.components.GraphicsComponent;
import math.ray.Ray;
import math.ray.RayIntersection;
import math.vector.Vector3;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import static java.awt.Color.BLACK;

public class RayEvaluationThread extends Thread {

    private BufferedImage _image;
    private Ray[] _rays;
    private RayIntersection[] _intersections;

    private Vector3 initial, location, lookAt, up, right;



    private int _width, _height, _startX, _startY, _id;
    private boolean _complete,_sleep;
    private Camera _cam;

    public RayEvaluationThread(int id, Camera camera, int width, int startX, int height, int startY, BufferedImage image, RayIntersection[] intersections) {
        _id = id;
        _cam = camera;
        _width = width;
        _height = height;
        _startX = startX;
        _startY = startY;

        _image = image;
        _intersections = intersections;

        _rays = new Ray[_width*_height];

        _complete = false;
        _sleep = true;
    }

    public void grabCameraParams() {
        initial = _cam.initial();
        location = _cam.location();
        lookAt = _cam.lookAt();
        up = _cam.up();
        right = _cam.right();
    }

    public void run() {

        while (true) {
            //System.out.println("Does Cam want Frame?");

            //System.out.println("Cam Does want frame");
            boolean fieldExists = _cam.fieldExists();
            _intersections = new RayIntersection[_width * _height];
            if (!fieldExists) {
                grabCameraParams();
                _intersections = new RayIntersection[_width * _height];

                initial = location.plus(lookAt.normalized().times(3)).plus(up.times(1)).plus(right.times(2));

                _rays = new Ray[(int) (_width * _height)];
            }
            Vector3 direction = null;
            //System.out.println("y= "+_startY+" y < "+(_startY+_height));
            //System.out.println("x= "+_startY+" x < "+(_startX+_width));
            for (int y = _startY; y < (_startY + _height); y++) {
                for (int x = _startX; x < (_startX + _width); x++) {
                    int index = (((y - _startY) * _width) + (x - _startX));
                    Ray r;
                    if (!fieldExists) {
                        direction = initial.minus(up.times((float) ((3 / _cam.resV()) * y))).minus(right.times((float) ((5 / _cam.resH()) * x)));
                        r = new Ray(location, direction);
                        _rays[index] = r;
                    } else {
                        r = _rays[index];
                    }
                    //System.out.println("x: "+(x+_startX)+"\ny: "+(y+_startY));
                    for (GraphicsComponent gC : GraphicsComponent.components) {                 // For each graphics component
                        RayIntersection rayInt = gC.intersectsWith(r);                          // Check if intersection occurs
                        if (rayInt != null) {                                                   // If ray intersects
                            if (_intersections[index] != null) {                                      // And there already exists an intersection at this point
                                RayIntersection previousIntersection = _intersections[index];        // Get the pre-existing intersection
                                if (rayInt.depth() < previousIntersection.depth()) {                     // If the depth of the new intersection is less than the pre-existing
                                    _intersections[index] = rayInt;                                          // Override the current intersection
                                    _image.setRGB(x, y, rayInt.color().getRGB());                             // Set the color of this pixel to the new intersection
                                } else {                                                            // Else
                                    _image.setRGB(x, y, previousIntersection.color().getRGB());               // Set the color of this pixel to the previous intersection
                                }
                            } else {                                                                // If there does not already exist an intersection,
                                _intersections[index] = rayInt;                                          // Override the current intersection
                                _image.setRGB(x, y, rayInt.color().getRGB());                             // Set the color of this pixel to the new intersection
                            }                                                                   //

                        } else if (GraphicsComponent.components.indexOf(gC) == GraphicsComponent.components.size() - 1 && // If there is no intersect
                                _intersections[index] == null) {                                                                // Previous or current for the final component
                                _image.setRGB(x, y, BLACK.getRGB());                                            // There is no intersect at all, so mark the pixel black
                        }
                    }
                }
            }
            //_complete = true;
            _cam.completeThread();
            //_cam.completeThread();
            while (_cam.completeThreads() >= _cam.threadCount()) {
                //System.out.println("cT: "+_cam.completeThreads()+"\ntC: "+_cam.threadCount());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } // Wait until camera requests frame
        }
    }

    public boolean complete() {
        return _complete;
    }

    public void complete(boolean shouldBeComplete) {
        _complete = shouldBeComplete;
    }
}
