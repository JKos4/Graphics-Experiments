package math.vector;
import graphics.components.*;

import java.awt.*;

public class Vector3 {
    public static final Vector3 ORIGIN = new Vector3(0,0,0), UP = new Vector3(0,0,1);
    private float _i,_j,_k,_magnitude;
    public Vector3(float i, float j, float k) {
        _i=i;
        _j=j;
        _k=k;
        _magnitude = (float)(Math.sqrt(_i*_i+_j*_j+_k*_k));
    }

    public Vector3(Vector3 initial, Vector3 terminal) {
        _i=terminal.i()-initial.i();
        _j=terminal.j()-initial.j();
        _k=terminal.k()-initial.k();
        _magnitude = (float)(Math.sqrt(_i*_i+_j*_j+_k*_k));
    }

    public float i() {
        return _i;
    }
    public void i(float i) {_i = i;}

    public float j() {
        return _j;
    }
    public void j(float j) { _j = j; }

    public float k() {
        return _k;
    }
    public void k(float k) { _k = k; }

    public float magnitude() {
        return _magnitude;
    }

    public float dot(Vector3 v) {
        return (_i*v.i()+_j*v.j()+_k*v.k());
    }
    public Vector3 times(float i) {return new Vector3(i*_i,i*_j,i*_k);}
    public Vector3 normalized() {float i =_i/_magnitude;
                                 float j =_j/_magnitude;
                                 float k =_k/_magnitude;
                                 return new Vector3(i,j,k);}
    public void normalize() {_i =_i/_magnitude;
                             _j =_j/_magnitude;
                             _k =_k/_magnitude;}
    public Vector3 plus(Vector3 v) {return new Vector3(_i+v.i(),
                                                      _j+v.j(),
                                                      _k+v.k());}
    public Vector3 minus(Vector3 v) {return new Vector3(_i-v.i(),
                                                        _j-v.j(),
                                                        _k-v.k());}
    public Vector3 negative() {
        return new Vector3(-_i,-_j,-_k);
    }
    public Vector3 cross(Vector3 v) {
        return new Vector3((_j*v.k()-_k*v.j()),
                          -(_i*v.k()-_k*v.i()),
                           (_i*v.j()-_j*v.i()));
    }
    public Vector3 project(Vector3 v){return (v.times(this.dot(v)/v.dot(v)));}
    public int colorize() {
        Vector3 toColorize = this.normalized();

        int r = Math.abs((int) (255*(toColorize.i())));
        int g = Math.abs((int) (255*(toColorize.j())));
        int b = Math.abs((int) (255*(toColorize.k())));
        return new Color(r,g,b).getRGB();
    }

    public String toString() {
        return "<"+_i+","+_j+","+_k+">";
    }
}
