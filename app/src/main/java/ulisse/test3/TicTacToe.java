

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

import static org.opencv.highgui.Highgui.imread;


public class TicTacToe extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    @Override
    public void onCameraViewStarted(int width, int height) {
        
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
}

/*
    */
/*
    private Mat mIntermediateMat;
    private Mat mGray;
    Mat hierarchy;
    List<MatOfPoint> contours;
    List<Marker> MarkerList;
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

        MarkerList = new ArrayList<Marker>();
        m_markerCorner = new ArrayList<>();
        m_markerCorner.add(new Point(0,0));
        m_markerCorner.add(new Point(6,0));
        m_markerCorner.add(new Point(6,6));
        m_markerCorner.add(new Point(0,6));


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
        MarkerList.clear();
        Mat frameCapture = mGray.clone();

        Imgproc.Canny(frameCapture, mIntermediateMat, 80, 100);
        Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Imgproc.drawContours(frameCapture, contours, -1, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255)); //, 2, 8, hierarchy, 0, new Point())


        List<MatOfPoint2f> newContours = new ArrayList<>();
        List<MatOfPoint2f> approxContours = new ArrayList<>();
        List<Point> approxPoints = new ArrayList<Point>();
        List<Point> MarkerCandidato;
        List<Pair> tooNearMarker = new ArrayList<Pair>();
        List<Marker> MarkerDetected = new ArrayList<>();

        for (int i = 0; i < contours.size(); i++) {
            if (Imgproc.isContourConvex(contours.get(i))) {

            MatOfPoint2f newPoint = new MatOfPoint2f(contours.get(i).toArray());
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MarkerCandidato = new ArrayList<Point>();


            newContours.add(newPoint);

            double eps = (int) newPoint.total() * 0.05;

            Imgproc.approxPolyDP(newPoint, approxCurve, eps, true);
            Converters.Mat_to_vector_Point(approxCurve, approxPoints);

            //Log.e("testPair", "eps " + String.valueOf(eps) + " approxCurve.total() " + approxCurve.total() );
            if (approxCurve.total() == 4) {
                 //riconverto in Mat?
                    double minDistFound = Double.MAX_VALUE;
                    // look for the min distance
                    for (int j = 0; j < 4; j++) {
                        Point side = subsractPoint(approxPoints.get(j), approxPoints.get((j + 1) % 4));
                        double sqauredSideLenght = side.dot(side);
                        minDistFound = Math.min(minDistFound, sqauredSideLenght);
                    }


                    if (minDistFound > 0) {
                        // create a candidate marker
                        for (int j = 0; j < 4; j++) {
                            MarkerCandidato.add(new Point(approxPoints.get(j).x, approxPoints.get(j).y));
                        }
                    }


                    Point v1 = subsractPoint(MarkerCandidato.get(1), MarkerCandidato.get(0));
                    Point v2 = subsractPoint(MarkerCandidato.get(2), MarkerCandidato.get(0));

                    double o = (v1.x * v2.y) - (v1.y * v2.x);
                    if (o < 0.0) {
                        Point v3 = MarkerCandidato.get(3);
                        MarkerCandidato.set(3, v1);
                        MarkerCandidato.set(1, v3);
                    }
                    //Log.e("testPair", "aggiungo" + MarkerCandidato);
                    Marker m1 = new Marker(MarkerCandidato.size());
                    m1.add(MarkerCandidato.get(0));
                m1.add(MarkerCandidato.get(1));
                m1.add(MarkerCandidato.get(2));
                m1.add(MarkerCandidato.get(3));
                    MarkerList.add(m1);
                }
            }
        }
        Log.e("testSize", String.valueOf(MarkerList.size()));

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
                //Log.e("testPair", tooNearMarker.get(0).first.toString());
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
        //Mat canonicalMarker = new Mat();
        //Converters.vector_Point_to_Mat(m_markerCorner,m_markerCornerMat);





        Mat A = new Mat(5,5,CvType.CV_8UC1,new Scalar(0));
        //List<Point> prova = new ArrayList<>();

        A.put(0,0,0);A.put(0,1,0);A.put(0,2,0);A.put(0,3,0);A.put(0,4,0);
        A.put(1,0,0);A.put(1,1,255);A.put(1,2,0);A.put(1,3,0);A.put(1,4,0);
        A.put(2,0,0);A.put(2,1,0);A.put(2,2,255);A.put(2,3,255);A.put(2,4,0);
        A.put(3,0,0);A.put(3,1,0);A.put(3,2,255);A.put(3,3,0);A.put(3,4,0);
        A.put(4,0,0);A.put(4,1,0);A.put(4,2,0);A.put(4,3,0);A.put(4, 4, 0);

        Imgproc.threshold(A, A, 0, 255, Imgproc.THRESH_BINARY);

        List<Marker> detectedMarkers = new ArrayList<>();

        for(int i=0;i<MarkerDetected.size();i++){
                Marker marker = MarkerDetected.get(i);
                Mat canonicalMarker = new Mat();
                TicTacToe.warp(frameCapture, canonicalMarker, new Size(50, 50), marker);
                marker.setMat(canonicalMarker);
                marker.extractCode();
                if(marker.checkBorder()){
                    int id = marker.calculateMarkerId();
                    if(id != -1){
                        detectedMarkers.add(marker);
                        // rotate the points of the marker so they are always in the same order no matter the camera orientation
                        Collections.rotate(marker, 4 - marker.getRotations());
                    }
                }
            }

        Collections.sort(detectedMarkers);
        Vector<Integer> toRemove = new Vector<Integer>();;
        for(int i=0;i<detectedMarkers.size();i++)
            toRemove.add(0);

        for(int i=0;i<detectedMarkers.size()-1;i++){
            if(detectedMarkers.get(i).id == detectedMarkers.get(i+1).id)
                if(detectedMarkers.get(i).perimeter()<detectedMarkers.get(i+1).perimeter())
                    toRemove.set(i, 1);
                else
                    toRemove.set(i+1, 1);
        }

        for(int i=toRemove.size()-1;i>=0;i--) {// done in inverse order in case we need to remove more than one element
            if (toRemove.get(i) == 1)
                detectedMarkers.remove(i);
        }
        // detect the position of markers if desired
        for(int i=0;i<detectedMarkers.size();i++){
            detectedMarkers.get(i).calculateExtrinsics(camMatrix, distCoeff, markerSizeMeters);
        }







    Mat rotation[] = new Mat[4];
        int distance[] = new int[4];

        rotation[0] = A;
        distance[0] = 0;



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

            //Imgproc.warpPerspective(frameCapture, canonicalMarker, m, canonicalMarker.size());

            //Imgproc.threshold(canonicalMarker, canonicalMarker, 125, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        }

           /// Mat bitMatrix = Mat.zeros(5, 5, CvType.CV_8UC1);





        //Log.e("bitmap", canonicalMarker.cols() + " " + canonicalMarker.rows());
        */
/*Bitmap bm = Bitmap.createBitmap(canonicalMarker.cols(),canonicalMarker.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(canonicalMarker, bm);
        test.setImageBitmap(bm);*//*


        Bitmap bm = Bitmap.createBitmap(A.cols(),A.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(A, bm);
        bm = Bitmap.createScaledBitmap(bm,120,120,false);
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
        *//*



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


    public static void warp(Mat in, Mat out, Size size, Vector<Point> points){
        Mat pointsIn = new Mat(4,1,CvType.CV_32FC2);
        Mat pointsRes = new Mat(4,1,CvType.CV_32FC2);
        pointsIn.put(0,0, points.get(0).x,points.get(0).y,
                points.get(1).x,points.get(1).y,
                points.get(2).x,points.get(2).y,
                points.get(3).x,points.get(3).y);
        pointsRes.put(0, 0, 0, 0,
                size.width - 1, 0,
                size.width - 1, size.height - 1,
                0, size.height - 1);
        Mat m = new Mat();
        m = Imgproc.getPerspectiveTransform(pointsIn, pointsRes);
        Imgproc.warpPerspective(in, out, m, size);
    }

    public static Mat createMarkerImage(int id,int size) throws CvException {
        if (id>=1024)
            throw new CvException("id out of range");
        Mat marker = new Mat(size,size, CvType.CV_8UC1, new Scalar(0));
        //for each line, create
        int swidth=size/7;
        int ids[]={0x10,0x17,0x09,0x0e};
        for (int y=0;y<5;y++) {
            int index=(id>>2*(4-y)) & 0x0003;
            int val=ids[index];
            for (int x=0;x<5;x++) {
                Mat roi=marker.submat((x+1)*swidth, (x+2)*swidth,(y+1)*swidth,(y+2)*swidth);// TODO check
                if ( (( val>>(4-x) ) & 0x0001) != 0 )
                    roi.setTo(new Scalar(255));
                else
                    roi.setTo(new Scalar(0));
            }
        }
        return marker;
    }







}
*/
