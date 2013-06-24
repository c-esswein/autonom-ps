package uibk.autonom.ps.colordetector;

import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import uibk.autonom.ps.activity.MainActivity;
import uibk.autonom.ps.colordetector.detectors.ColorThresholdDetector;

public class ColorCodeDetector {

	private int horVariance = 50;

	private ColorDetector detector1 = new ColorThresholdDetector();
	private ColorDetector detector2 = new ColorThresholdDetector();

	public ColorCodeDetector(Scalar color1, Scalar color2) {
		detector1.setHsvColor(color1);
		detector2.setHsvColor(color2);
	}
	
	private void detect(Mat currentRgba)
	{
		detector1.detect(currentRgba);
		detector2.detect(currentRgba);
	}	
	
	//Returns the bottompoint of the beacon. If the beacon is not detected, null is returned.
	public Point getBottomPoint(Mat currentRgba )
	{
		detect(currentRgba);
		
		Point p1 = detector1.getCenterPoints(1).get(0);
		Point p2 = detector2.getCenterPoints(1).get(0);
		
		Log.i("ColorCode", "Point 1y: " + p1.y);
		Log.i("ColorCode", "Point 2y: " + p2.y);
		
		MainActivity.showMessage("Visible: " + p1.y + "-" + p2.y + "=" + Math.abs(p1.y-p2.y));
		
		if(Math.abs(p1.y-p2.y)<horVariance)
			return new Point(p2.x,p2.y);
		else
			return null;
				
	}

}
