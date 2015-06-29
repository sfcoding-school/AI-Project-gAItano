

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
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;


import com.hoho.android.usbserial.driver.UsbSerialPort;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
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

import ulisse.test3.MarkerDet.CameraParameters;
import ulisse.test3.MarkerDet.Marker;
import ulisse.test3.MarkerDet.MarkerDetector;

import static org.opencv.highgui.Highgui.imread;


public class TicTacToe extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private List<Marker> MarkerTrovati;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat frameCapture;
    Mat hierarchy;
    List<MatOfPoint> contours;
    //List<Marker> MarkerList;
    List<Point> m_markerCorner;
    Button button;
    ImageView test;
    Code code;
    MatOfPoint mat;

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

        /*
        MarkerList = new ArrayList<Marker>();
        m_markerCorner = new ArrayList<>();
        m_markerCorner.add(new Point(0,0));
        m_markerCorner.add(new Point(6,0));
        m_markerCorner.add(new Point(6,6));
        m_markerCorner.add(new Point(0,6));
*/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_tic_tac_toe);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        MarkerTrovati = new ArrayList<>();

        button = (Button) findViewById(R.id.button12);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                for (int i=0; i<10;i++) {
                    if (MarkerTrovati.size() == 2) {
                        break;
                    } else {
                        doSomething();
                    }

                }
                stampa();
            }

        });

        test = (ImageView) findViewById(R.id.imageView);
    }

    private void doSomething() {
        MarkerDetector A = new MarkerDetector();
        CameraParameters mCamParam = new CameraParameters();
        float markerSizeMeters = 0.034f;

        Vector<Marker> MarkerDetected= new Vector<Marker>();

        A.detect(frameCapture, MarkerDetected, mCamParam.getCameraMatrix(), mCamParam.getDistCoeff(), markerSizeMeters, mGray);

        if (MarkerDetected.size() != 0 ){
            Log.e("nMarker", "Sono esattamente:" + MarkerDetected.size());
            for (int i=0; i<MarkerDetected.size(); i++)
                if (MarkerTrovati.size()==0 ||MarkerTrovati.get(0).findCenter()!=MarkerDetected.get(i).findCenter())
                MarkerTrovati.add(MarkerDetected.get(i));
        }else{
            Log.e("nMarker","Nun ce sò");
        }




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
        frameCapture = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        //Nuova
        //Imgproc.cvtColor(inputFrame.rgba(),mGray,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.adaptiveThreshold(mGray,mGray,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV,7,7);

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        //mGray = inputFrame.gray();


        frameCapture = inputFrame.rgba();

        /*double thresParam1, thresParam2;

        Mat grey, thres, thres2, hierarchy2;
        List<MatOfPoint> contours2;
        MatOfPoint mIntermediateMat;

        thresParam1 = thresParam2 = 7;

        grey = new MatOfPoint();
        thres = new Mat();
        thres2 = new Mat();
        hierarchy2 = new Mat();
        contours2 = new ArrayList<>();
        mIntermediateMat = new MatOfPoint();

        Imgproc.cvtColor(inputFrame.rgba(), mGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(mGray, thres, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV, (int) thresParam1, thresParam2);
        Imgproc.findContours(thres, contours2, hierarchy2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
        Mat frameDebug = new Mat();
        Imgproc.drawContours(mGray, contours2, -1, new Scalar(255, 0, 0), 2);

        */


/*

        MatOfKeyPoint points = new MatOfKeyPoint();
        MatOfKeyPoint marker_points = new MatOfKeyPoint();
        Mat matrix = mGray;

        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);

        fast.detect(matrix, points);


        Scalar redcolor = new Scalar(255, 0, 0);
        Core.line(mGray, new Point(100, 100), new Point(300, 300), new Scalar(0, 0, 255));

        Features2d.drawKeypoints(mGray, points, mGray, redcolor, 3);

*//*

        */
/*
        Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.markers));
        Mat matrix2 = imread("markers.jpg", 0);
        fast.detect(matrix2, marker_points);


*/

        return frameCapture;
    }


    private void stampa(){
        //Mat B = new Mat(frameCapture.cols(), mGray.rows(), CvType.CV_8UC1);
        Log.e("Marker", " " + MarkerTrovati.size());
        for (int i=0; i<MarkerTrovati.size(); i++){
            MarkerTrovati.get(i).draw(frameCapture,new Scalar(255,0,0),10,true);
        }

        Bitmap bm = Bitmap.createBitmap(frameCapture.cols(),frameCapture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frameCapture, bm);
        //bm = Bitmap.createScaledBitmap(bm,120,120,false);
        test.setImageBitmap(bm);
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

    private int hammDist(Code code){
        int ids[][] = {
                {1,0,0,0,0},
                {1,0,1,1,1},
                {0,1,0,0,1},
                {0,1,1,1,0}
        };
        int dist = 0;
        for(int y=0;y<5;y++){
            int minSum = Integer.MAX_VALUE;
            // hamming distance to each possible word
            for(int p=0;p<4;p++){
                int sum=0;
                for(int x=0;x<5;x++)
                    sum+= code.get(y+1,x+1) == ids[p][x]? 0:1;
                minSum = sum<minSum? sum:minSum;
            }
            dist+=minSum;
        }
        return dist;
    }

    public static class Code {// TODO check if the parameters are in range
        protected int[][] code;

        protected Code(){
            code = new int[7][7];
        }

        protected void set(int x, int y, int value){
            code[x][y] = value;
        }

        protected int get(int x, int y){
            return code[x][y];
        }

        public static Code rotate(Code in){
            Code out = new Code();
            for(int i=0;i<7;i++)
                for(int j=0;j<7;j++){
                    out.code[i][j] = in.code[6-j][i];
                }
            return out;
        }
    }




}

