package uibk.autonom.ps.colordetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public Mat detect(Mat inputFrame) {
		Mat calcFrame = new Mat();

		Imgproc.cvtColor(inputFrame, calcFrame, Imgproc.COLOR_RGB2HSV);

		outputFrame = new Mat();
		Core.inRange(calcFrame, lowerColorLimit, upperColorLimit, outputFrame);

		calcFrame.release();

		return outputFrame;
	}

	public List<MatOfPoint> getMaxContours(int count) {
		List<MatOfPoint> contours = getContours();
		Map<Double, MatOfPoint> maxAreaPoints = new TreeMap<Double, MatOfPoint>();

		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);

			maxAreaPoints.put(area, contour);
		}

		List<MatOfPoint> tmp_list = new ArrayList<MatOfPoint>();
		List<Double> keys = new ArrayList<Double>();

		// Add to temporary key list.
		for (Double d : maxAreaPoints.keySet())
			keys.add(d);

		// sort the keys
		Collections.sort(keys);

		for(int i=keys.size()-1; i > keys.size()-(count+1); i--)
			tmp_list.add(maxAreaPoints.get(keys.get(i)));

		return tmp_list;
	}

	public List<MatOfPoint> getContours() {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Mat calcFrame = outputFrame.clone();
		Imgproc.findContours(calcFrame, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		calcFrame.release();

		return contours;
	}

	public List<Point> getCenterPoints(int count) {
		List<Point> tmp_list = new ArrayList<Point>();

		for (MatOfPoint p : getMaxContours(count)) {
			tmp_list.add(getCenterPoint(p));
		}

		return tmp_list;
	}

	public List<Point> getBottomPoints(int count) {
		List<Point> tmp_list = new ArrayList<Point>();

		for (MatOfPoint p : getMaxContours(count)) {
			tmp_list.add(getBotttomPoint(p));
		}

		return tmp_list;
	}

	public Point getCenterPoint(MatOfPoint contour) {
		float[] radius = null;
		Point centerPoint = new Point();

		MatOfPoint2f pointsList = new MatOfPoint2f(contour.toArray());

		Imgproc.minEnclosingCircle(pointsList, centerPoint, radius);

		return centerPoint;
	}

	public Point getBotttomPoint(MatOfPoint contour) {
		float[] radius = new float[3];
		Point centerPoint = new Point();

		MatOfPoint2f pointsList = new MatOfPoint2f(contour.toArray());

		Imgproc.minEnclosingCircle(pointsList, centerPoint, radius);

		centerPoint.x = centerPoint.x + radius[0];

		return centerPoint;

	}

	public void setHsvColor(Scalar hsvColor) {
		ColorConverter.getHsvColorRange(hsvColor, lowerColorLimit, upperColorLimit);
	}
}
