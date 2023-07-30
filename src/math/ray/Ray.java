package math.ray;

import math.vector.Vector3;

public class Ray {
    private Vector3 _direction,_initial;
    public Ray(Vector3 initial, Vector3 terminal) {
        _initial = initial;
        _direction = new Vector3(initial,terminal);
        _direction.normalize();
    }

    public Vector3 direction() {
        return _direction;
    }

    public Vector3 initial() {
        return _initial;
    }

    public String toString() {
        return ("Ray at "+_initial+"pointing in the direction "+_direction);
    }
}
