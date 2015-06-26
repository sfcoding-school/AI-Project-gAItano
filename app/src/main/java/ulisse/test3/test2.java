package ulisse.test3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;


import com.hoho.android.usbserial.driver.UsbSerialPort;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static org.opencv.highgui.Highgui.imread;


public class test2 extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mOrig;
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

        Mat imgOrig = mGray;
        Mat veryOriginal = mOrig;

        Mat hsv_image;
        //Mat.cvtColor(bgr_image, hsv_image, cv::COLOR_BGR2HSV);
        // Threshold the HSV image, keep only the red pixels
        Mat lower_red_hue_range = new Mat();
        Mat upper_red_hue_range = new Mat();
        Core.inRange(imgOrig, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
        Core.inRange(imgOrig, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);



        Mat red_hue_image = new Mat();
        Core.addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);
        Imgproc.GaussianBlur(red_hue_image, red_hue_image, new Size(9, 9), 2, 2);

        //Mat circles = new Mat(); //sarebbe da usare un vettore ??
        //Imgproc.HoughCircles(red_hue_image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, red_hue_image.rows() / 8, 100, 20, 0, 0);

        Mat circles = new Mat();

        Imgproc.HoughCircles(red_hue_image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, red_hue_image.rows()/8, 100, 20, 0, 0);


        int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

        List<Point> proviamoAfareIlTriangolo = new ArrayList<>();


        for (int i=0; i<numberOfCircles; i++) {

            double[] circleCoordinates = circles.get(0, i);

            if (circleCoordinates != null) {

                int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

                Point center = new Point(x, y);
                proviamoAfareIlTriangolo.add(center);

                int radius = (int) circleCoordinates[2];


                Core.circle(veryOriginal, center, radius, new Scalar(0, 255, 0), 4);


                Core.rectangle(veryOriginal, new Point(x - 5, y - 5),
                        new Point(x + 5, y + 5),
                        new Scalar(0, 128, 255), -1);


            } else {
                Log.e("trovaCerchi", "NON HO TROVATO NIENTE");
            }
        }


            //Log.e("bitmap", canonicalMarker.cols() + " " + canonicalMarker.rows());
            Bitmap bm = Bitmap.createBitmap(veryOriginal.cols(), veryOriginal.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(veryOriginal, bm);
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
        final Intent intent = new Intent(context, test2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mOrig = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Imgproc.cvtColor(inputFrame.rgba(), mGray, Imgproc.COLOR_RGB2HSV);

        //Imgproc.cvtColor(inputFrame.rgba(), mOrig, Imgproc.COLOR_RGB2HSV);

        mOrig = inputFrame.rgba();

        return inputFrame.rgba();
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

/*       *//* convert bitmap to mat *//*
        Mat mat = mGray;
        Mat grayMat = new Mat(mGray.cols(), mGray.rows(), CvType.CV_8UC1);



*//* convert to grayscale *//*
        int colorChannels = (mat.channels() == 3) ? Imgproc.COLOR_BGR2GRAY
                : ((mat.channels() == 4) ? Imgproc.COLOR_BGRA2GRAY : 1);

        Imgproc.cvtColor(mat, grayMat, colorChannels);

*//* reduce the noise so we avoid false circle detection *//*
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(9, 9), 2, 2);

// accumulator value
        double dp = 1.2d;
// minimum distance between the center coordinates of detected circles in pixels
        double minDist = 100;

// min and max radii (set these values as you desire)
        int minRadius = 0, maxRadius = 0;

// param1 = gradient value used to handle edge detection
// param2 = Accumulator threshold value for the
// cv2.CV_HOUGH_GRADIENT method.
// The smaller the threshold is, the more circles will be
// detected (including false circles).
// The larger the threshold is, the more circles will
// potentially be returned.
        double param1 = 70, param2 = 72;

*//* create a Mat object to store the circles detected *//*
        Mat circles = new Mat();

*//* find the circle in the image *//*
        Imgproc.HoughCircles(grayMat, circles,
                Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                param2, minRadius, maxRadius);

*//* get the number of circles detected *//*
        int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

*//* draw the circles found on the image *//*
        for (int i=0; i<numberOfCircles; i++) {


*//* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
 * (x,y) are the coordinates of the circle's center
 *//*
            double[] circleCoordinates = circles.get(0, i);


            int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

            Point center = new Point(x, y);

            int radius = (int) circleCoordinates[2];

    *//* circle's outline *//*
            Core.circle(mat, center, radius, new Scalar(0,
                    255, 0), 4);

    *//* circle's center outline *//*
            Core.rectangle(mat, new Point(x - 5, y - 5),
                    new Point(x + 5, y + 5),
                    new Scalar(0, 128, 255), -1);
        }

*//* convert back to bitmap */