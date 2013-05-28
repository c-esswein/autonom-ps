package uibk.autonom.ps.navigation;

import java.util.NavigableMap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.annotation.TargetApi;
import android.os.Build;

import uibk.autonom.ps.colordetector.ColorDetector;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Marker {
	private Scalar color;
	/**
	 * Point corresponding to virtual system
	 * 
	 * 			100
	 * -------------------------------------|
	 * |1               2                  3|
	 * |                                    | 100
	 * |6               5                  4|
	 * -------------------------------------
	 */
	private Point position;
	private int index;
	
	public Point curImgPosition;
	public double curImgSize = 0;
	
	public static double MIN_SIZE = 0.;
	
	public Marker(int index, Scalar color, Point position){
		this.color = color;
		this.position = position;
	}
	
	public Scalar getColor(){
		return color;
	}

	public Point getPosition() {
		return position;
	}
		
	public void calculateImgPosition(Mat curImgFrame){
		ColorDetector colorDetector = new ColorDetector();
		colorDetector.setHsvColor(getColor());
		colorDetector.detect(curImgFrame);
		
		NavigableMap<Double, MatOfPoint> result = colorDetector.getMaxContourSizes(1);

		if(result.size() > 0){
			java.util.Map.Entry<Double, MatOfPoint> entry = result.pollFirstEntry();
			curImgSize = entry.getKey();
			curImgPosition = colorDetector.getCenterPoint(entry.getValue());
		}
	}
	
	public boolean isInImg(){
		return curImgPosition != null && curImgSize > MIN_SIZE;
	}
	
	/**
	 * TODO unused
	 * @param p current virtual cords of robot
	 * @return angle to x axe
	 */
	public double getAngle(Point p){
		double angle = 0.;
		
		switch(index){
		case 0:
			return angle + 180.;
		case 1:
			return angle + 180.;
		case 2:
			return angle + 180.;
		case 3:
			return angle + 180.;
		case 4:
			return angle + 180.;
		case 5:
			return angle + 180.;
		default:
			return angle;
		}
	}
	
}
