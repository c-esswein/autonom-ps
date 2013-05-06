package uibk.autonom.ps.selflocalisation;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.util.Log;

import uibk.autonom.ps.colordetector.ColorDetector;

public class Locator {
	private Mat H = null;
	
	public void calibrate(Mat inputFrame, Scalar color){
		ColorDetector colorDetector = new ColorDetector();
		MatOfPoint2f srcPoints;
		MatOfPoint2f dstPoints;
		
		colorDetector.setHsvColor(color);
		inputFrame = colorDetector.detect(inputFrame);
		
		List<MatOfPoint> contours = colorDetector.getMaxContours(4);
		List<Point> imgPoints = new ArrayList<Point>();
		
		for(MatOfPoint contour : contours){
			imgPoints.add(colorDetector.getCenterPoint(contour));
		}
		
		srcPoints = sortSrcPoints(imgPoints);
		dstPoints = getDstPoints();
		
		H = Calib3d.findHomography(srcPoints, dstPoints);
		
		Log.i(MainActivity.DEBUG_TAG, "H: " + H.dump());
	}
	
	private MatOfPoint2f sortSrcPoints(List<Point> imgPoints){		
		Point[] points = new Point[4];
		
		for(Point point : imgPoints){
			if(points[0] == null || points[0].y > point.y){
				points[0] = point;
			}
			
			if(points[1] == null || points[1].x > point.x){
				points[1] = point;
			}
			
			if(points[2] == null || points[2].y < point.y){
				points[2] = point;
			}
			
			if(points[3] == null || points[3].x < point.x){
				points[3] = point;
			}
		}
		
		Log.i(MainActivity.DEBUG_TAG, "Fixed Points:");
		for(int i = 0; i < 4; i++){
			Log.i(MainActivity.DEBUG_TAG, "Point" + i + ": " + points[i]);
		}
		
		return new MatOfPoint2f(points);
	}
	
	private MatOfPoint2f getDstPoints(){
		Point[] points = new Point[4];
		
		points[0] = new Point(0., 17.);
		points[1] = new Point(-5.5, 23.);
		points[2] = new Point(0., 28.);
		points[3] = new Point(5.5, 23.);
		
		return new MatOfPoint2f(points);
	}
	
	public boolean isCalibrated(){
		return H == null;
	}
	
	public String img2World(Point p){		
		Mat srcMat = new Mat(1, 1, CvType.CV_32FC2);
		Mat destMat = new Mat(1, 1, CvType.CV_32FC2);

		srcMat.put(0, 0, new double[] {p.x, p.y});
		
		Log.i(MainActivity.DEBUG_TAG, srcMat.dump());

		Core.perspectiveTransform(srcMat, destMat, H);
		
		// TODO return point
		
		return destMat.dump();
	}
}
