package ulisse.test3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.Vector;


import com.hoho.android.usbserial.driver.UsbSerialPort;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
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
import org.opencv.highgui.Highgui;
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
    List<Point> m_markerCorner;
    Button button;
    ImageView test;

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

        Log.i("PROVA tag 2", "called onCreate");
        super.onCreate(savedInstanceState);

        MarkerList = new ArrayList<List<Point>>();
        m_markerCorner = new ArrayList<>();
        m_markerCorner.add(new Point(0,0));
        m_markerCorner.add(new Point(6,0));
        m_markerCorner.add(new Point(6,6));
        m_markerCorner.add(new Point(0, 6));


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_tic_tac_toe);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        button = (Button) findViewById(R.id.button12);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               doSomething();
            }

        });

        test = (ImageView) findViewById(R.id.imageView);
    }

    private void doSomething() {
        Mat frameCapture = new Mat();
        frameCapture = mGray.clone();

        Imgproc.Canny(frameCapture, mIntermediateMat, 80, 100);
        Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(frameCapture, contours, -1, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255)); //, 2, 8, hierarchy, 0, new Point())


        List<MatOfPoint2f> newContours = new ArrayList<>();
        List<MatOfPoint2f> approxContours = new ArrayList<>();
        List<Point> approxPoints = new ArrayList<Point>();
        List<Point> MarkerCandidato;
        List<Pair> tooNearMarker = new ArrayList<Pair>();
        List<List<Point>> MarkerDetected = new ArrayList<>();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f newPoint = new MatOfPoint2f(contours.get(i).toArray());
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MarkerCandidato = new ArrayList<Point>();


            newContours.add(newPoint);

            double eps = (int) newPoint.total() * 0.05;

            Imgproc.approxPolyDP(newPoint, approxCurve, eps, true);
            Converters.Mat_to_vector_Point(approxCurve, approxPoints);

            //Log.e("testPair", "eps " + String.valueOf(eps) + " approxCurve.total() " + approxCurve.total() );
            if (approxCurve.total() == 4) {
                //if (Imgproc.isContourConvex(approxCurve)) { //riconverto in Mat?
                double minDistFound = Double.MAX_VALUE;
                // look for the min distance
                for (int j = 0; j < 4; j++) {
                    Point side = subsractPoint(approxPoints.get(j), approxPoints.get((j+1)%4));
                    double sqauredSideLenght = side.dot(side);
                    minDistFound = Math.min(minDistFound, sqauredSideLenght);
                }


                if (minDistFound > 0) {
                    // create a candidate marker
                    for (int j = 0; j < 4; j ++) {
                        MarkerCandidato.add(new Point(approxPoints.get(j).x, approxPoints.get(j).y));
                    }
                }


                Point v1 = subsractPoint( MarkerCandidato.get(1),MarkerCandidato.get(0));
                Point v2 = subsractPoint( MarkerCandidato.get(2),MarkerCandidato.get(0));

                double o = (v1.x * v2.y) - (v1.y * v2.x);
                if (o < 0.0) {
                    Point v3 = MarkerCandidato.get(3);
                    MarkerCandidato.set(3, v1);
                    MarkerCandidato.set(1, v3);
                }
                //Log.e("testPair", "aggiungo" + MarkerCandidato);
                MarkerList.add(MarkerCandidato);
            }
        }
        if (MarkerList.size() > 0) {

            for (int i = 0; i < MarkerList.size(); i++) {
                List<Point> m1 = MarkerList.get(i);
                for (int j = i+1; j < MarkerList.size(); j++){
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
                        tooNearMarker.add(new Pair(i,j));
                    }
                }
            }

            boolean[] MarkerMask = new boolean[MarkerList.size()];
            Arrays.fill(MarkerMask, false);
            //Log.e("testPair", String.valueOf(tooNearMarker.size()));
            for (int i=0;i<tooNearMarker.size();i++){
                float p1 = perimeter(MarkerList.get(Integer.parseInt(tooNearMarker.get(i).first.toString())));
                float p2 = perimeter(MarkerList.get(Integer.parseInt(tooNearMarker.get(i).second.toString())));
                Log.e("testPair", tooNearMarker.get(0).first.toString());
                int removalIndex;
                if (p1>p2){
                    removalIndex = Integer.parseInt(tooNearMarker.get(i).second.toString());
                }else {
                    removalIndex = Integer.parseInt(tooNearMarker.get(i).first.toString());
                }
                MarkerMask[removalIndex] = true;
            }

            for (int i = 0; i< MarkerList.size(); i++){
                if (!MarkerMask[i]){
                    MarkerDetected.add(MarkerList.get(i));
                }
            }

        } else {Log.e("testPair","MarkeCandidato è null");}


        Mat src = new Mat(4,1,CvType.CV_32FC2);
        Mat dst = new Mat(4,1,CvType.CV_32FC2);

        Mat m_markerCornerMat = new Mat();
        Mat canonicalMarker = new Mat();
        //Converters.vector_Point_to_Mat(m_markerCorner,m_markerCornerMat);


        Mat prova= new Mat(400,400,CvType.CV_8UC1);
        org.opencv.core.Rect r = new org.opencv.core.Rect(10,10,50,50);
        org.opencv.core.Mat subView = prova.submat(r);
        Mat zero = Mat.zeros(5, 5, CvType.CV_8UC1);// bitMatrix = new org.opencv.core.Mat.zeros(5,5,CvType.CV_8UC1);
        int cellsize = 50*50;

        for (int x=0; x<5; x++){
            for (int y=0; y<5; y++){
                int cellX = (x+1)*cellsize;
                int cellY = (y+1)*cellsize;
                //Mat cell =  new org.opencv.core.Rect(cellX,cellY,cellsize,cellsize);
            }
        }

        float data[][] = {{1,1,1,1,1},{1,0,1,1,1},{1,1,0,0,1},{1,1,0,1,1},{1,1,1,1,1}};

        for (int i=0; i<MarkerList.size(); i++){
            List<Point> MarkerProva = MarkerList.get(i);

            src.put(0, 0,   MarkerProva.get(0).x,
                    MarkerProva.get(0).y,
                    MarkerProva.get(1).x,
                    MarkerProva.get(1).y,
                    MarkerProva.get(2).x,
                    MarkerProva.get(2).y,
                    MarkerProva.get(3).x,
                    MarkerProva.get(3).y);
            dst.put(0, 0, 0.0, 0.0, 6.0, 0.0, 6.0, 6.0, 0.0, 6.0);

            Mat m = Imgproc.getPerspectiveTransform(src, dst);

            Imgproc.warpPerspective(frameCapture, canonicalMarker, m, canonicalMarker.size());

            Imgproc.threshold(canonicalMarker, canonicalMarker, 125, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        }

        Mat bitMatrix = Mat.zeros(5, 5, CvType.CV_8UC1);

        for (int y=0; y<5; y++){
            for (int x=0; x<5; x++){
                int cellX = (x+1)*cellsize;
                int cellY = (y+1)*cellsize;
                //Mat image = new Rect(cellX,cellY,cellsize,cellsize);
            }
        }



        Log.e("bitmap", canonicalMarker.cols() + " " + canonicalMarker.rows());
        Bitmap bm = Bitmap.createBitmap(canonicalMarker.cols(),canonicalMarker.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(canonicalMarker, bm);
        test.setImageBitmap(bm);


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

        //Nuova
        Imgproc.cvtColor(inputFrame.rgba(),mGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(mGray,mGray,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV,7,7);
        //Vecchia
        //Imgproc.threshold(inputFrame.gray(), inputFrame.gray(), 127, 255, Imgproc.THRESH_TOZERO);
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        //mGray = inputFrame.gray();







        /*

        MatOfKeyPoint points = new MatOfKeyPoint();
        MatOfKeyPoint marker_points = new MatOfKeyPoint();
        Mat matrix = mGray;

        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);

        fast.detect(matrix, points);


        Scalar redcolor = new Scalar(255, 0, 0);
        Core.line(mGray, new Point(100, 100), new Point(300, 300), new Scalar(0, 0, 255));

        Features2d.drawKeypoints(mGray, points, mGray, redcolor, 3);

*/
        /*
        Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.markers));
        Mat matrix2 = imread("markers.jpg", 0);
        fast.detect(matrix2, marker_points);
        */


        return mGray;
    }

    private Point subsractPoint(Point v1, Point v2){
        double v1x = v1.x - v2.x;
        double v1y = v1.y - v2.y;
        return new Point(v1x, v1y);

    }

    private float perimeter(List<Point> v){
        float peri = 0;
        for (int i=0; i<v.size(); i++){
            peri += euqlDist(v.get(i),v.get((i+1)%4));
        }
        return peri;
    }

    private float euqlDist(Point a, Point b){
        double res = Math.sqrt(Math.pow((a.x - b.x),2) + Math.pow((a.y - b.y),2));
        return (float) res;
    }


}
