package graphics.components;

import math.ray.Ray;
import math.ray.RayIntersection;

import java.util.ArrayList;

import java.awt.Color;

public abstract class GraphicsComponent {
    public static ArrayList<Polygon> polygons = new ArrayList<>();
    public static ArrayList<GraphicsComponent> components = new ArrayList<>();

    public abstract RayIntersection intersectsWith(Ray ray);
}
