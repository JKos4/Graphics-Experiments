package graphics.components;

import math.vector.Vector3;

import java.awt.Color;
import java.awt.Color.*;

public class Polygon {
    private Vector3 _location;
    private Vector3[] _vertices;
    private int[][][] _faces;
    private Color[] _colors;

    private int _nTris = 0;

    private Tri[] _tris;

    public Polygon(Vector3 location, Vector3[] vertices, int[][][] faces, Color[] colors) {
        _location = location;
        _vertices = vertices;
        _faces = faces;
        _colors = colors;

        for (int[][] face : faces) {
            _nTris += face.length;
        }

        _tris = new Tri[_nTris];
        this.construct();
        GraphicsComponent.polygons.add(this);
    }

    private void construct() {
        int triIndex = 0;
        for (int faceIndex = 0; faceIndex < _faces.length; faceIndex++) {
            Vector3 faceNormal = new Vector3(0,0,0);
            for (int triIndexInFace = 0; triIndexInFace < _faces[faceIndex].length;triIndexInFace++) {
                Vector3[] triPoints = {null, null, null};
                for (int pointIndex = 0; pointIndex < _faces[faceIndex][triIndexInFace].length;pointIndex++){
                    int pointsIndex = _faces[faceIndex][triIndexInFace][pointIndex];
                    triPoints[pointIndex] = _vertices[pointsIndex].plus(_location);
                }
                _tris[triIndex] = new Tri(triPoints[0],triPoints[1],triPoints[2], _colors[faceIndex]);
                if (!(faceNormal.i() == 0 && faceNormal.j() == 0 && faceNormal.k() == 0)) {
                    _tris[triIndex].normal(faceNormal);
                    //System.out.println("Normal for face "+triIndex+" colored "+_col`ors[faceIndex]+": "+faceNormal);
                } else {
                    faceNormal = _tris[triIndex].normal();
                }
                triIndex+=1;
            }
        }
    }

    public Vector3 location() {
        return _location;
    }

    public void location(Vector3 location) {
        Vector3 transform = new Vector3 (_location, location);
        _location = location;

        for (int triIndex = 0; triIndex < _tris.length; triIndex++) {
            _tris[triIndex].transform(transform);
        }
    }
}
