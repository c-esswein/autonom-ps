package uibk.autonom.ps.colordetector;

import java.util.List;
import java.util.NavigableMap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.annotation.TargetApi;
import android.os.Build;

public interface ColorDetector {

	public abstract Mat detect(Mat inputFrame);

	public abstract List<MatOfPoint> getMaxContours(int count);

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public abstract NavigableMap<Double, MatOfPoint> getMaxContourSizes(
			int count);

	public abstract List<MatOfPoint> getContours();

	public abstract List<Point> getCenterPoints(int count);

	public abstract List<Point> getBottomPoints(int count);

	public abstract Point getCenterPoint(MatOfPoint contour);

	public abstract Point getBotttomPoint(MatOfPoint contour);

	public abstract void setHsvColor(Scalar hsvColor);

}