package uibk.autonom.ps.navigation;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.annotation.TargetApi;
import android.os.Build;

import uibk.autonom.ps.colordetector.ColorCodeDetector;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Marker {
	private Scalar color1;
	private Scalar color2;

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

	public Marker(int index, Scalar color1, Scalar color2, Point position) {
		this.color1 = color1;
		this.color2 = color2;
		this.position = position;
	}

	public Scalar getColor1() {
		return color1;
	}

	public Scalar getColor2() {
		return color2;
	}

	public Point getPosition() {
		return position;
	}

	public void calculateImgPosition(Mat curImgFrame) {
		ColorCodeDetector detector = new ColorCodeDetector(color1, color2);
		Point tempPoint = detector.getBottomPoint(curImgFrame);
		if (tempPoint != null) {
			curImgSize = 1;
			curImgPosition=	tempPoint;		
		}
		else
		{
			curImgFrame=null;
			curImgSize=0;
		
		}

	}

	public boolean isInImg() {
		return curImgPosition != null;
	}

	/**
	 * TODO unused
	 * 
	 * @param p
	 *            current virtual cords of robot
	 * @return angle to x axe
	 */
	public double getAngle(Point p) {
		double angle = 0.;

		switch (index) {
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
