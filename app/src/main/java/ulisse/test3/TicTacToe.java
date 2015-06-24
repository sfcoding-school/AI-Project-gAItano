package ulisse.test3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Vector;


import com.hoho.android.usbserial.driver.UsbSerialPort;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.Highgui.imread;


public class TicTacToe extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Mat mIntermediateMat;
    private Mat mGray;
    Mat hierarchy;
    List<MatOfPoint> contours;
    List<List<Point>> MarkerList;

    private final static double MIN_DISTANCE = 10;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("sono qui", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    private ClassLoader context;

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }


    private CameraBridgeViewBase mOpenCvCameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ;

        Log.i("PROVA tag 2", "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_tic_tac_toe);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    static void show(Context context, UsbSerialPort port) {
        final Intent intent = new Intent(context, TicTacToe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.threshold(inputFrame.gray(), inputFrame.gray(), 127, 255, Imgproc.THRESH_TOZERO);
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        mGray = inputFrame.gray();

        Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
        Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(mGray, contours, -1, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255)); //, 2, 8, hierarchy, 0, new Point())


        List<MatOfPoint2f> newContours = new ArrayList<>();
        List<MatOfPoint2f> approxContours = new ArrayList<>();
        List<Point> approxPoints = new ArrayList<Point>();
        List<Point> MarkerCandidato = null;



        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f newPoint = new MatOfPoint2f(contours.get(i).toArray());
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MarkerCandidato = new ArrayList<Point>();


            newContours.add(newPoint);
            double eps = (int) newPoint.total() * 0.05;

            Imgproc.approxPolyDP(newPoint, approxCurve, eps, true);
            Converters.Mat_to_vector_Point(approxCurve, approxPoints);

            if (approxContours.size() == 4) {
                //if (Imgproc.isContourConvex(approxCurve)) { //riconverto in Mat?
                double minDistFound = Double.MAX_VALUE;
                int[] points = new int[8];// [x1 y1 x2 y2 x3 y3 x4 y4]
                approxCurve.get(0, 0, points);
                // look for the min distance
                for (int j = 0; j <= 4; j += 2) {
                    double d = Math.sqrt((points[j] - points[(j + 2) % 4]) * (points[j] - points[(j + 2) % 4]) +
                            (points[j + 1] - points[(j + 3) % 4]) * (points[j + 1] - points[(j + 3) % 4]));
                    if (d < minDistFound)
                        minDistFound = d;
                }

                if (minDistFound > MIN_DISTANCE) {
                    // create a candidate marker
                    MarkerCandidato.add(new Point(points[0], points[1]));
                    MarkerCandidato.add(new Point(points[2], points[3]));
                    MarkerCandidato.add(new Point(points[4], points[5]));
                    MarkerCandidato.add(new Point(points[6], points[7]));
                }

                double v1x = MarkerCandidato.get(1).x - MarkerCandidato.get(0).x;
                double v1y = MarkerCandidato.get(1).y - MarkerCandidato.get(0).y;
                Point v1 = new Point(v1x, v1y);
                double v2x = MarkerCandidato.get(2).x - MarkerCandidato.get(0).x;
                double v2y = MarkerCandidato.get(2).y - MarkerCandidato.get(0).y;
                Point v2 = new Point(v2x, v2y);

                double o = (v1.x * v2.y) - (v1.y * v2.x);
                if (o < 0.0) {
                    Point v3 = MarkerCandidato.get(3);
                    MarkerCandidato.set(3, v1);
                    MarkerCandidato.set(1, v3);
                }

                MarkerList.add(MarkerCandidato);
            }
        }
            if (MarkerList != null) {

                Array[][] Marker;
                for (int i = 0; i <= MarkerList.size(); i++) {
                    List<Point> m1 = MarkerList.get(i);
                    for (int j = i+1; j <= MarkerList.size(); j++){
                        List<Point> m2 = MarkerList.get(j);
                        float disSquared = 0;
                        for (int c=0; c<4;c++){
                            double vX = m1.get(c).x - m2.get(c).x;
                            double vY = m1.get(c).y - m2.get(c).y;
                            Point v = new Point(vX,vY);
                            disSquared += v.dot(v);
                        }
                        disSquared /= 4;
                        if (disSquared<100){
                            //List<Pair> tooNearMarker.add(i,j);
                        }
                    }
                }
            } else {Log.e("TicTacToe","MarkeCandidato è null");}



        MatOfKeyPoint points = new MatOfKeyPoint();
        MatOfKeyPoint marker_points = new MatOfKeyPoint();
        Mat matrix = mGray;

        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);

        fast.detect(matrix, points);


        Scalar redcolor = new Scalar(255, 0, 0);
        Core.line(mGray, new Point(100, 100), new Point(300, 300), new Scalar(0, 0, 255));

        Features2d.drawKeypoints(mGray, points, mGray, redcolor, 3);


        /*
        Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.markers));
        Mat matrix2 = imread("markers.jpg", 0);
        fast.detect(matrix2, marker_points);
        */


        return mGray;
    }


}
