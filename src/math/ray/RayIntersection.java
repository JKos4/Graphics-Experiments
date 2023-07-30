package math.ray;

import math.vector.Vector3;

import java.awt.*;

public class RayIntersection {
    float _depth;
    private Color _color;

    public RayIntersection(float depth, Color color) {
        _depth = depth;
        _color = color;
    }

    public Color color() {
        return _color;
    }

    public float depth() {
        return _depth;
    }
}
