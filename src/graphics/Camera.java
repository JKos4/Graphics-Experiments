package graphics;
import math.ray.Ray;
import math.ray.RayIntersection;
import math.vector.*;

import java.awt.image.BufferedImage;

public class Camera {
    private float _theta, _phi;
    private float _fovH, _fovV, _resH, _resV;
    private Ray[] _rays;

    private Vector3 _location,_up,_right,_lookingAt,_lookAt, _initial;
    private int _threadCount, _rootThreadCount, _threadSliceWidth, _threadSliceHeight, _screenLength;
    private RayIntersection[] _intersections;
    private RayEvaluationThread[] _threadPool;

    private BufferedImage _image;
    private boolean _fieldExists, _requestingFrame;

    private int _frames;
    private int _completeThreads;
    private boolean _complete;
    public Camera(Vector3 location, Vector3 lookAt, float resH, float resV, float viewDist) {
        _location = location;
        _lookingAt = lookAt;
        _lookAt = _lookingAt.normalized();
        this.buildRightUp();

        _resH = resH;
        _resV = resV;

        _image = new BufferedImage((int) _resH, (int) _resV,BufferedImage.TYPE_INT_RGB);

        _requestingFrame = false;
        _fieldExists = false;
        _frames = 0;

        _completeThreads = 0;

        _complete = false;

        this.buildThreads();
        this.drawField();
    }

    public void buildThreads() {
        _threadCount = 16;
        _rootThreadCount = (int)(Math.sqrt(_threadCount));
        _threadSliceWidth = (int)(_resH/_rootThreadCount);
        _threadSliceHeight = (int)(_resV/_rootThreadCount);
        _threadPool = new RayEvaluationThread[_threadCount];

        _screenLength = (int)(_resV*_resH);

        _intersections = new RayIntersection[_screenLength];
        for (int threadX = 0; threadX < _rootThreadCount; threadX++) {
            for (int threadY = 0; threadY < _rootThreadCount; threadY++){
                int index = (threadY*_rootThreadCount) + threadX;
                _threadPool[index] = new RayEvaluationThread(index,this, _threadSliceWidth, _threadSliceWidth*threadX, _threadSliceHeight, _threadSliceHeight*threadY, _image, _intersections);
                _threadPool[index].start();
            }
        }
    }

    public void drawField() {
        _completeThreads = 0;
        //_fieldExists = false;
        _complete = false;
        /*for (int thread = 0; thread < _threadCount && !_threadPool[thread].complete(); thread++) {
            _threadPool[thread].complete(false);
        }*/

        /*while (!allThreadsComplete) {
            allThreadsComplete = true;
            for (int thread = 0; thread < _threadCount; thread++) {
                //System.out.println("Addressing thread "+thread);
                if (!_threadPool[thread].complete()) {
                    allThreadsComplete = false;
                    //System.out.println("T");
                }
            }
        }*/
        while (_completeThreads < _threadCount) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        _complete = true;
        //System.out.println("Frame done!");
        _requestingFrame = false;
    }
    public void drawField2() {
        _completeThreads = 0;
        _intersections = new RayIntersection[_screenLength];

        Vector3 initial = null;
        Vector3 direction = null;
        if (!_fieldExists) {
            initial = _location.plus(_lookAt.normalized().times(3)).plus(_up.times(1)).plus(_right.times(2));

            _rays = new Ray[(int) (_resH * _resV)];
        }
        float spacingY = 3 / _resV;
        float spacingX = 4 / _resH;
        for (int y = 0; y < _resV;y++) {
            for (int x = 0; x < _resH;x++) {
                int index = (int) (y * _resH + x);
                if (!_fieldExists) {
                    direction = initial.minus(_up.times((spacingY) * y)).minus(_right.times((spacingX) * x));
                }
                Ray r;
                if (!_fieldExists) {
                    r = new Ray(_location, direction);
                    _rays[index]=r;
                } else {
                    r = _rays[index];
                }

                int destinationThreadY = (_rootThreadCount-1)-(int)(y/(_resV/_rootThreadCount));
                int destinationThreadX = (int)(x/(_resH/_rootThreadCount));
                int destinationThreadIndex = destinationThreadY*_rootThreadCount+destinationThreadX;

                if (destinationThreadX != (int)((x+1)/(_resH/_rootThreadCount)) && destinationThreadY != (_rootThreadCount-1)-(int)((y+1)/(_resV/_rootThreadCount))) {
                    _threadPool[destinationThreadIndex].complete();
                    //System.out.println(destinationThreadIndex+" complete for frame "+_frames);
                }
            }
        }

        //_threadsStarted = true;

        _fieldExists = true;

        _frames++;
    }

    public void completeThread() {
        _completeThreads++;
    }
    public int completeThreads() {
        return _completeThreads;
    }

    public int threadCount() {
        return _threadCount;
    }

    public boolean complete() {
        return _complete;
    }

    public BufferedImage image() {
        drawField();
        return _image;
    }

    public Vector3 up() {
        return _up;
    }

    public Vector3 right() {
        return _right;
    }

    public boolean fieldExists() {
        return _fieldExists;
    }

    public Vector3 location() {
        return _location;
    }

    public Vector3 initial() {
        return _initial;
    }

    public double resH() {
        return _resH;
    }

    public double resV() {
        return _resV;
    }

    public boolean requestingFrame() {
        return _requestingFrame;
    }

    public void buildRightUp() {
        _right = Vector3.UP.cross(_lookAt);
        _right.normalize();
        _up = _right.cross(_lookAt).negative();
        _up.normalize();
        _initial = _location.plus(_lookAt.normalized().times(3)).plus(_up.times(1)).plus(_right.times(2));
    }

    public Vector3 lookAt() {
        return _lookAt;
    }

    public void lookAt(Vector3 p) {
        _lookAt = new Vector3 (_location, p);
        _fieldExists = false;
        this.buildRightUp();
        this.drawField();

    }

    public void goTo(Vector3 p) {
        _location = p;
        this.lookAt(_lookAt);
    }

    public Ray[] rays() {
        return _rays;
    }


}
