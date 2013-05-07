package uibk.autonom.ps.selflocalisation;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import uibk.autonom.ps.colordetector.ColorDetector;
import android.util.Log;

public class Calibrator {
	private Mat H = null;
	
	public Mat calibrate(Mat inputFrame, Scalar color){
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
		
		return H;
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
		
		float yOffset = 9;
		
		points[0] = new Point(0., 0. + yOffset);
		points[1] = new Point(-9.2, 13.7 + yOffset);
		points[2] = new Point(0., 27.5 + yOffset);
		points[3] = new Point(9.2, 13.7 + yOffset);
		
		return new MatOfPoint2f(points);
	}
}
