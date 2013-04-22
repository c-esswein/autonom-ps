package uibk.autonom.ps.selflocalisation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorDetector {
	
	private Scalar lowerColorLimit = new Scalar(0);
    private Scalar upperColorLimit = new Scalar(0);
    
    private Mat outputFrame;
	
	public Mat detect(Mat inputFrame){
		Mat calcFrame = new Mat();
		
		Imgproc.cvtColor(inputFrame, calcFrame, Imgproc.COLOR_RGB2HSV);
		
		outputFrame = new Mat();
		Core.inRange(calcFrame, lowerColorLimit, upperColorLimit, outputFrame);
		
		calcFrame.release();
		
		return outputFrame;
	}
	
	public MatOfPoint getMaxContour() {
		List<MatOfPoint> contours = getContours();
		        
        // Find max contour area
        double maxArea = 0;
        MatOfPoint maxAreaPoints = new MatOfPoint();
        Iterator<MatOfPoint> each = contours.iterator();
        
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            
            if (area > maxArea){
                maxArea = area;
                maxAreaPoints = wrapper;
            }
        }
        
        return maxAreaPoints;
    }
	
	public List<MatOfPoint> getContours() {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Mat calcFrame = outputFrame.clone();
        Imgproc.findContours(calcFrame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        calcFrame.release();
        
        return contours;
    }
	
	
	public Point getCenterPoint(){
		return getCenterPoint(getMaxContour());
	}
	
	public Point getCenterPoint(MatOfPoint contour){
		float[] radius = null;
		Point centerPoint = new Point();
		
        MatOfPoint2f pointsList = new MatOfPoint2f(contour.toArray());
        
		Imgproc.minEnclosingCircle(pointsList, centerPoint, radius);
        
		return centerPoint;
	}
	
	public void setHsvColor(Scalar hsvColor){
		ColorConverter.getHsvColorRange(hsvColor, lowerColorLimit, upperColorLimit);
	}
}
