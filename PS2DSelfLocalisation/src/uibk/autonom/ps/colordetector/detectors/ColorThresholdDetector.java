package uibk.autonom.ps.colordetector.detectors;

import android.annotation.TargetApi;
import android.os.Build;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import uibk.autonom.ps.colordetector.ColorConverter;
import uibk.autonom.ps.colordetector.ColorDetector;
import uibk.autonom.ps.navigation.Navigator;

public class ColorThresholdDetector implements ColorDetector {

	private Scalar lowerColorLimit = new Scalar(0);
	private Scalar upperColorLimit = new Scalar(0);

	protected Mat outputFrame;
	protected Scalar hsvColor;

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#detect(org.opencv.core.Mat)
	 */
	@Override
	public Mat detect(Mat inputFrame) {
		Mat calcFrame = new Mat();

		Imgproc.cvtColor(inputFrame, calcFrame, Imgproc.COLOR_RGB2HSV);

		outputFrame = new Mat();
		Core.inRange(calcFrame, lowerColorLimit, upperColorLimit, outputFrame);

		calcFrame.release();

		return outputFrame;
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getMaxContours(int)
	 */
	@Override
	public List<MatOfPoint> getMaxContours(int count) {
		Map<Double, MatOfPoint> map = getMaxContourSizes(count);
		
		return new ArrayList<MatOfPoint>(Arrays.asList(map.values().toArray(new MatOfPoint[0])));
	}
	
	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getMaxContourSizes(int)
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public NavigableMap<Double, MatOfPoint> getMaxContourSizes(int count) {
		List<MatOfPoint> contours = getContours();
		TreeMap<Double, MatOfPoint> maxAreaPoints = new TreeMap<Double, MatOfPoint>();

		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);

			maxAreaPoints.put(area, contour);
		}
		
		//remove values until map has size count
		while(maxAreaPoints.size() > count){
			maxAreaPoints.pollFirstEntry();
		}
		
		return maxAreaPoints.descendingMap();
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getContours()
	 */
	@Override
	public List<MatOfPoint> getContours() {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Mat calcFrame = outputFrame.clone();
		Imgproc.findContours(calcFrame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		calcFrame.release();

		return contours;
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getCenterPoints(int)
	 */
	@Override
	public List<Point> getCenterPoints(int count) {
		List<Point> tmp_list = new ArrayList<Point>();

		for (MatOfPoint p : getMaxContours(count)) {
			tmp_list.add(getCenterPoint(p));
		}

		return tmp_list;
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getBottomPoints(int)
	 */
	@Override
	public List<Point> getBottomPoints(int count) {
		List<Point> tmp_list = new ArrayList<Point>();

		for (MatOfPoint p : getMaxContours(count)) {
			tmp_list.add(getBotttomPoint(p));
		}

		return tmp_list;
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getCenterPoint(org.opencv.core.MatOfPoint)
	 */
	@Override
	public Point getCenterPoint(MatOfPoint contour) {
		float[] radius = null;
		Point centerPoint = new Point();

		MatOfPoint2f pointsList = new MatOfPoint2f(contour.toArray());

		Imgproc.minEnclosingCircle(pointsList, centerPoint, radius);

		return centerPoint;
	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#getBotttomPoint(org.opencv.core.MatOfPoint)
	 */
	@Override
	public Point getBotttomPoint(MatOfPoint contour) {
		float[] radius = new float[3];
		Point centerPoint = new Point();

		MatOfPoint2f pointsList = new MatOfPoint2f(contour.toArray());

		Imgproc.minEnclosingCircle(pointsList, centerPoint, radius);

		centerPoint.x = centerPoint.x + radius[0];

		return centerPoint;

	}

	/* (non-Javadoc)
	 * @see uibk.autonom.ps.colordetector.ColorDetector#setHsvColor(org.opencv.core.Scalar)
	 */
	@Override
	public void setHsvColor(Scalar hsvColor) {
		this.hsvColor = hsvColor;
		ColorConverter.getHsvColorRange(hsvColor, lowerColorLimit, upperColorLimit);
	}
}
