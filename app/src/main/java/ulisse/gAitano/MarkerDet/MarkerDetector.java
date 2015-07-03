package ulisse.gAitano.MarkerDet;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


public class MarkerDetector {
    public enum thresSuppMethod {FIXED_THRES,ADPT_THRES,CANNY};

    private double thresParam1, thresParam2;
    public thresSuppMethod thresMethod;
    private Mat grey, thres, thres2, hierarchy2;
    private List<MatOfPoint> contours2;

    private final static double MIN_DISTANCE = 10;

    public MarkerDetector(){
        thresParam1 = thresParam2 = 7;
        thresMethod = thresSuppMethod.ADPT_THRES;
        grey = new MatOfPoint();
        thres = new Mat();
        thres2 = new Mat();
        hierarchy2 = new Mat();
        contours2 = new ArrayList<>();
    }

    /**
     * Method to find markers in a Mat given.
     * @param in input color Mat to find the markers in.
     * @param detectedMarkers output vector with the markers that have been detected.
     * @param camMatrix --
     * @param distCoeff --
     * @param markerSizeMeters --
     * @param frameDebug used for debug issues, delete this
     */
    public void detect(Mat in, Vector<Marker> detectedMarkers, Mat camMatrix, Mat distCoeff,
                       float markerSizeMeters, Mat frameDebug){
        Vector<Marker> candidateMarkers = new Vector<Marker>();
        detectedMarkers.clear();


        Imgproc.cvtColor(in, grey, Imgproc.COLOR_RGB2GRAY);
        thresHold(thresMethod, grey, thres);
        grey = in;

        thres.copyTo(thres2);
        Imgproc.findContours(thres2, contours2, hierarchy2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(frameDebug, contours2, -1, new Scalar(255, 0, 0), 2);

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        List<Point> approxPoints = new ArrayList<Point>();
        for(int i=0;i<contours2.size();i++){
            MatOfPoint contourMat = contours2.get(i);
            // per prima cosa controlla se ha un numero sufficiente di punti
            int contourSize = (int)contourMat.total();
            if(contourSize > in.cols()/5){
                MatOfPoint2f contour = new MatOfPoint2f(contourMat.toArray());
                Imgproc.approxPolyDP(contour, approxCurve, contourSize*0.05, true);
                Converters.Mat_to_vector_Point(approxCurve, approxPoints);
                if(approxCurve.total()== 4){

                        //controlla che la distanza tra i punti sia abbastanza
                        double minDistFound = Double.MAX_VALUE;
                        int[] points = new int[8];// [x1 y1 x2 y2 x3 y3 x4 y4]
                    int boh = 0;
                    for (int r=0;r<8;r+=2) {
                        points[r] = (int) approxPoints.get(boh).x;
                        points[r+1] = (int) approxPoints.get(boh).y;
                        boh++;
                    }
                        //controlla la distanza
                        for(int j=0;j<=4;j+=2){
                            double d = Math.sqrt( (points[j]-points[(j+2)%4])*(points[j]-points[(j+2)%4]) +
                                    (points[j+1]-points[(j+3)%4])*(points[j+1]-points[(j+3)%4]));
                            if(d<minDistFound)
                                minDistFound = d;
                        }
                        if(minDistFound > MIN_DISTANCE){
                            //crea il marker candidato
                            candidateMarkers.add(new Marker(markerSizeMeters));
                            candidateMarkers.lastElement().add(new Point(points[0],points[1]));
                            candidateMarkers.lastElement().add(new Point(points[2],points[3]));
                            candidateMarkers.lastElement().add(new Point(points[4],points[5]));
                            candidateMarkers.lastElement().add(new Point(points[6],points[7]));
                        }
                    }
                //}
            }
        }
        int nCandidates = candidateMarkers.size();
        //ordina i punti in senso anti orario
        for(int i=0;i<nCandidates;i++){
            // tracciando una linea tra il primo e il secondo punto
            // se il terzo è dal lato destro allora i punti sono disposti in senso anti-orario
            Marker marker = candidateMarkers.get(i);
            double dx1 = marker.get(1).x - marker.get(0).x;
            double dy1 = marker.get(1).y - marker.get(0).y;
            double dx2 = marker.get(2).x - marker.get(0).x;
            double dy2 = marker.get(2).y - marker.get(0).y;
            double o = dx1*dy2 - dy1*dx2;
            if(o < 0.0) //il terzo punto è a sinistra: eseguiamo lo swap tra 1 e 3
                Collections.swap(marker, 1, 3);
        }

        //TODO costoso! testare i risultati eliminando questo controllo.
        //rimuovo i marker candidati i cui angoli sono troppo vicini tra loro
        Vector<Integer> tooNearCandidates = new Vector<Integer>();

        for(int i=0;i<nCandidates;i++){
            Marker toMarker = candidateMarkers.get(i);
            // calocolo la distanza tra ogni angolo di un marker e gli angoli di tutti gli altri.
            for(int j=i+1;j<nCandidates;j++){
                float dist=0;
                Marker fromMarker = candidateMarkers.get(j);

                dist+=Math.sqrt((fromMarker.get(0).x-toMarker.get(0).x)*(fromMarker.get(0).x-toMarker.get(0).x)+
                        (fromMarker.get(0).y-toMarker.get(0).y)*(fromMarker.get(0).y-toMarker.get(0).y));

                dist+=Math.sqrt((fromMarker.get(1).x-toMarker.get(1).x)*(fromMarker.get(1).x-toMarker.get(1).x)+
                        (fromMarker.get(1).y-toMarker.get(1).y)*(fromMarker.get(1).y-toMarker.get(1).y));

                dist+=Math.sqrt((fromMarker.get(2).x-toMarker.get(2).x)*(fromMarker.get(2).x-toMarker.get(2).x)+
                        (fromMarker.get(2).y-toMarker.get(2).y)*(fromMarker.get(2).y-toMarker.get(2).y));

                dist+=Math.sqrt((fromMarker.get(3).x-toMarker.get(3).x)*(fromMarker.get(3).x-toMarker.get(3).x)+
                        (fromMarker.get(3).y-toMarker.get(3).y)*(fromMarker.get(3).y-toMarker.get(3).y));
                dist = dist/4;
                if(dist < MIN_DISTANCE){
                    tooNearCandidates.add(i);
                    tooNearCandidates.add(j);
                }
            }
        }
        Vector<Integer> toRemove = new Vector<Integer>();// 1 means to remove
        for(int i=0;i<nCandidates;i++)
            toRemove.add(0);
        // elimino i marker con il perimetro minore
        for(int i=0;i<tooNearCandidates.size();i+=2){
            Marker first = candidateMarkers.get(tooNearCandidates.get(i));
            Marker second = candidateMarkers.get(tooNearCandidates.get(i+1));
            if(first.perimeter()<second.perimeter())
                toRemove.set(tooNearCandidates.get(i), 1);
            else
                toRemove.set(tooNearCandidates.get(i+1), 1);
        }

        // identifico i marker
        for(int i=0;i<nCandidates;i++){
            if(toRemove.get(i) == 0){
                Marker marker = candidateMarkers.get(i);
                Mat canonicalMarker = new Mat();
                warp(in, canonicalMarker, new Size(50, 50), marker);
                marker.setMat(canonicalMarker);
                marker.extractCode();

                String sum = "";
                for (int x=0;x<7;x++){
                    for (int y=0;y<7;y++){
                        sum = sum + String.valueOf(marker.code.get(x,y));
                    }
                    sum = sum +"-";
                }
                Log.e("Marker", "= " + sum);
                if(marker.checkBorder()){
                    //Log.e("Marker", "ok");
                    int id = marker.calculateMarkerId();
                    //Log.e("Marker", "bordo trovato" + id);
                    if(id ==0){
                        detectedMarkers.add(marker);
                        Log.e("Marker", "detectedMarkers" + detectedMarkers.size());
                        // ruotiamo i marker in modo da averli sempre nello stesso verso a prescindere dalla rotazione della camera
                        Collections.rotate(marker, 4-marker.getRotations());
                    }
                }
            }
        }
        // TODO si potrebbe raffinare utilizzando la pixel accuracy

        // ordiniamo tramite l'id e controlliamo che ogni marker sia trovato una sola volta
        /*
        Collections.sort(detectedMarkers);
        toRemove.clear();
        for(int i=0;i<detectedMarkers.size();i++)
            toRemove.add(0);

        for(int i=0;i<detectedMarkers.size()-1;i++){
            if(detectedMarkers.get(i).id == detectedMarkers.get(i+1).id)
                if(detectedMarkers.get(i).perimeter()<detectedMarkers.get(i+1).perimeter())
                    toRemove.set(i, 1);
                else
                    toRemove.set(i+1, 1);
        }

        for(int i=toRemove.size()-1;i>=0;i--)// done in inverse order in case we need to remove more than one element
            if(toRemove.get(i) == 1)
                detectedMarkers.remove(i);

        // detect the position of markers if desired
        for(int i=0;i<detectedMarkers.size();i++){
            detectedMarkers.get(i).calculateExtrinsics(camMatrix, distCoeff, markerSizeMeters);
        }
        */
    }


    public void thresHold(thresSuppMethod method, Mat src, Mat dst){
        switch(method){
            case FIXED_THRES:
                Imgproc.threshold(src, dst, thresParam1,255, Imgproc.THRESH_BINARY_INV);
                break;
            case ADPT_THRES:
                Imgproc.adaptiveThreshold(src,dst,255.0,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                        Imgproc.THRESH_BINARY_INV,(int)thresParam1,thresParam2);
                break;
            case CANNY:
                Imgproc.Canny(src, dst, 10, 220);
                break;
        }
    }


    private void warp(Mat in, Mat out, Size size, Vector<Point> points){
        Mat pointsIn = new Mat(4,1,CvType.CV_32FC2);
        Mat pointsRes = new Mat(4,1,CvType.CV_32FC2);
        pointsIn.put(0,0, points.get(0).x,points.get(0).y,
                points.get(1).x,points.get(1).y,
                points.get(2).x,points.get(2).y,
                points.get(3).x,points.get(3).y);
        pointsRes.put(0,0, 0,0,
                size.width-1,0,
                size.width-1,size.height-1,
                0,size.height-1);
        Mat m = new Mat();
        m = Imgproc.getPerspectiveTransform(pointsIn, pointsRes);
        Imgproc.warpPerspective(in, out, m, size);
    }
}
