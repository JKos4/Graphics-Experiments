package graphics.components;
import math.ray.Ray;
import math.ray.RayIntersection;
import math.vector.*;

import java.awt.*;
import java.util.ArrayList;

import static java.awt.Color.*;

public class Tri extends GraphicsComponent {

    private Color _color;
    private Vector3 _v0,_v1,_v2;
    private Vector3 _n, _v01, _v02;

    private float _D;

    public Tri(Vector3 v0, Vector3 v1, Vector3 v2, Color color) {
        _color = color;
        this.construct(v0,v1,v2);
        components.add(this);
    }

    public Tri(float x1, float y1, float z1,float x2, float y2, float z2,float x3, float y3, float z3, Color color) {
        _color = color;

        Vector3 v0 = new Vector3(x1,y1,z1);
        Vector3 v1 = new Vector3(x2,y2,z2);
        Vector3 v2 = new Vector3(x3,y3,z3);

        this.construct(v0,v1,v2);
        components.add(this);
    }

    public Tri(Vector3 v0, Vector3 v1, Vector3 v2) {
        _color = WHITE;
        this.construct(v0,v1,v2);
        components.add(this);
    }

    public Tri(float x1, float y1, float z1,float x2, float y2, float z2,float x3, float y3, float z3) {
        _color = WHITE;

        Vector3 v0 = new Vector3(x1,y1,z1);
        Vector3 v1 = new Vector3(x2,y2,z2);
        Vector3 v2 = new Vector3(x3,y3,z3);

        this.construct(v0,v1,v2);
        components.add(this);
    }

    private void construct(Vector3 v0,Vector3 v1,Vector3 v2) {
        _v0=v0;
        _v1=v1;
        _v2=v2;

        _v01 = new Vector3(v0,v1);
        _v02 = new Vector3(v0,v2);
        _n = _v01.cross(_v02);
        _D = -_n.dot(v0);
    }

    public Vector3 normal() {
        return _n;
    }
    public void normal(Vector3 nn) {_n=nn; _D = -_n.dot(_v0);}

    public RayIntersection intersectsWith(Ray ray) {
        RayIntersection output = null;
        boolean intersects = true;

        //Check if parallel
        float nDotRayDirection = _n.dot(ray.direction());
        if (Math.abs(nDotRayDirection) < .00001) {
            intersects = false;
        }

        // Compute t
        float t = -(_n.dot(ray.initial())+ _D)/nDotRayDirection;

        // Check if tri is behind ray
        if (t < 0) intersects = false;

        // Compute point of intersection
        Vector3 P = ray.initial().plus(ray.direction().times(t));

        // Used for inside-out test
        Vector3 C;

        //edge 0
        Vector3 e0 = new Vector3(_v0,_v1);
        Vector3 vp0 = P.minus(_v0);
        C = e0.cross(vp0);
        if (_n.dot(C) < 0) intersects = false; // P is on the right side

        //edge 1
        Vector3 e1 = new Vector3(_v1,_v2);
        Vector3 vp1 = P.minus(_v1);
        C = e1.cross(vp1);
        if (_n.dot(C) < 0) intersects = false; // P is on the right side

        //edge 2
        Vector3 e2 = new Vector3(_v2,_v0);
        Vector3 vp2 = P.minus(_v2);
        C = e2.cross(vp2);
        if (_n.dot(C) < 0) intersects = false; // P is on the right side

        if (intersects) {
            float distToLight = new Vector3(P, new Vector3(1,1,0)).magnitude();
            Vector3 lightVector = new Vector3((float).9, (float).9, 1);
            Vector3 darkVector = new Vector3((float).06,(float).06,(float).07);
            float distRad = distToLight;
            if (distRad > (Math.PI/2)) {
                distRad = (float)Math.PI/2;
            } else if (distRad < 0) {
                distRad = (float)0;
            }
            float lerpBrightness = distToLight/6;
            if (lerpBrightness <= 0) {
                lerpBrightness = 0;
            } else if (lerpBrightness > 1) {
                lerpBrightness = 1;
            }

            Vector3 lV = new Vector3(new Vector3(1,1,0),P);
            Vector3 pN = new Vector3(P,ray.initial());
            float dP = _n.dot(lV)/(lV.magnitude()*pN.magnitude());
            dP = Math.min(dP,0);

            lerpBrightness = 1-lerpBrightness*(1-dP);
            Vector3 colorVector = new Vector3(_color.getRed(), _color.getGreen(), _color.getBlue());
            colorVector.i(colorVector.i()*(lightVector.i()*lerpBrightness+(darkVector.i()*(1-lerpBrightness))));
            colorVector.j(colorVector.j()*(lightVector.j()*lerpBrightness+(darkVector.j()*(1-lerpBrightness))));
            colorVector.k(colorVector.k()*(lightVector.k()*lerpBrightness+(darkVector.k()*(1-lerpBrightness))));
            //System.out.println(colorVector);
            //output = new RayIntersection(t, _color);
            output = new RayIntersection(t, (new Color ((int)colorVector.i(), (int)colorVector.j(), (int)colorVector.k())));
        }

        return output;
    }

    public void transform(Vector3 transform) {
        _v0 = _v0.plus(transform);
        _v1 = _v1.plus(transform);
        _v2 = _v2.plus(transform);
        this.construct(_v0,_v1,_v2);
    }
}
