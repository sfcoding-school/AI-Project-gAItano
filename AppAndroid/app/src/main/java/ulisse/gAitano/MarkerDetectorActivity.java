

package ulisse.gAitano;

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

import java.util.Vector;


import com.hoho.android.usbserial.driver.UsbSerialPort;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import ulisse.gAitano.MarkerDet.CameraParameters;
import ulisse.gAitano.MarkerDet.Code;
import ulisse.gAitano.MarkerDet.Marker;
import ulisse.gAitano.MovementLibrary.Movement;
import ulisse.gAitano.Utility.ServiceMovimento;

import static org.opencv.highgui.Highgui.imread;


public class MarkerDetectorActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private List<Marker> MarkerTrovati;
    private Mat mGray;
    private Mat frameCapture;
    Mat hierarchy;
    Button button;
    ImageView test;
    double COL;
    double ROW;

    private final static double MIN_DISTANCE = 10;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("sLog", "OpenCV loaded successfully");
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
    private Movement movementClass;
    private static UsbSerialPort sPort= null;

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }


    private CameraBridgeViewBase mOpenCvCameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        movementClass = new Movement(getApplicationContext(), sPort);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_marker_det);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        MarkerTrovati = new ArrayList<>();

        button = (Button) findViewById(R.id.button12);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MarkerTrovati.clear();
                for (int i=0; i<1;i++) {
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
        ulisse.gAitano.MarkerDet.MarkerDetector A = new ulisse.gAitano.MarkerDet.MarkerDetector();
        CameraParameters mCamParam = new CameraParameters();
        float markerSizeMeters = 0.034f;

        Vector<Marker> MarkerDetected= new Vector<>();

        A.detect(frameCapture, MarkerDetected, mCamParam.getCameraMatrix(), mCamParam.getDistCoeff(), markerSizeMeters, mGray);

        if (MarkerDetected.size() != 0 ){
            Log.e("Marker", "Sono esattamente: " + MarkerDetected.size());
            for (int i=0; i<MarkerDetected.size(); i++) {
                if (MarkerTrovati.size() == 0 || !near(MarkerTrovati.get(0).findCenter(), MarkerDetected.get(i).findCenter()))
                    MarkerTrovati.add(MarkerDetected.get(i));

            }
        }else{
            Log.e("Marker","Non ci sono marker");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        movementClass.pauseActivity();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    static void show(Context context) {
        final Intent intent = new Intent(context, MarkerDetectorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat(height, width, CvType.CV_8UC1);
        frameCapture = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        hierarchy = new Mat();


        frameCapture = inputFrame.rgba();

        ROW = frameCapture.rows();
        COL = frameCapture.cols();

        //Core.line(frameCapture,new Point(0,0),new Point(ROW/2,COL/2),new Scalar(255,255,0),10);

        return frameCapture;
    }


    private void stampa(){
        Log.e("Marker", " " + MarkerTrovati.size());
        for (int i=0; i<MarkerTrovati.size(); i++){
            MarkerTrovati.get(i).draw(frameCapture,new Scalar(255,0,0),10,true);
            executeCommand(MarkerTrovati);
        }

        Bitmap bm = Bitmap.createBitmap(frameCapture.cols(), frameCapture.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frameCapture, bm);
        //bm = Bitmap.createScaledBitmap(bm,frameCapture.cols(),frameCapture.rows(),false);
        test.setImageBitmap(bm);
    }

    private boolean near(Point p1, Point p2){
        boolean ris= false;
        float dis = euqlDist(p1, p2);
        if (dis <= 20){
            ris = true;
        }else {
            Log.e("Marker","Distanza tra i punti" +dis);
        }
        return ris;
    }


    private float euqlDist(Point a, Point b){
        double res = Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
        return (float) res;
    }

    private void executeCommand(List<Marker> m ){
        Intent intent=new Intent(this,ServiceMovimento.class);
        Bundle b=new Bundle();
        b.putString("gAitano", "QRProject");
        for (int i=0; i<m.size();i++){
            if (m.get(i).getMarkerId()==0) {
                b.putString("QR", "wakeUP");
                intent.putExtras(b);
                startService(intent);
            }
        }

    }

}

