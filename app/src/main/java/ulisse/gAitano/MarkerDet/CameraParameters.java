package ulisse.gAitano.MarkerDet;


import org.opencv.core.CvType;
import org.opencv.core.Mat;




public class CameraParameters {

    // cameraMatrix will be of the form
    // | Fx 0  Cx |
    // | 0  Fy Cy |
    // | 0  0   1 |
    private Mat cameraMatrix;
    private Mat distorsionMatrix;


    public CameraParameters(){
        cameraMatrix = new Mat(3,3,CvType.CV_32FC1);
        cameraMatrix.put(0, 0,
                2.4634878668323222e+03,                     0., 1.3115898157817151e+03,
                0., 2.4515981478104272e+03, 7.7127636431427027e+02,
                0.,                     0.,                      1. );
        distorsionMatrix = new Mat(5,1,CvType.CV_32FC1);
        distorsionMatrix.put(0,0,
                1.2330078495750021e-01, -2.2284860800065850e-01,
                -2.5943373042754116e-05, -8.4022079973864469e-04,
                5.4942029257895009e-01 );
    }


    public Mat getCameraMatrix(){
        return cameraMatrix;
    }

    public Mat getDistCoeff(){
        return distorsionMatrix;
    }

}
