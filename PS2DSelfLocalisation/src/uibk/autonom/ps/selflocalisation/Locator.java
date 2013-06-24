package uibk.autonom.ps.selflocalisation;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import uibk.autonom.ps.activity.MainActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

public class Locator {
	private Mat H = null;
	private Activity act;

	@TargetApi(Build.VERSION_CODES.FROYO)
	public void calibrate(Mat H) {
		this.H = H;
		// Store settings
		SharedPreferences settings = act.getSharedPreferences("caibration", 0);
		byte[] calibrationArray = SerializeMat.MatToByteArray(H);
		SharedPreferences.Editor editor = settings.edit();
		String calibrationString = Base64.encodeToString(calibrationArray,
				Base64.NO_WRAP);
		editor.putString("cv", calibrationString);
		editor.commit();
	}

	public void setCalibration(Mat calibration){
		H=calibration;
	}
	public Locator(Activity act) {
		this.act = act;
	}

	public boolean isCalibrated() {
		return H == null;
	}

	public Point img2World(Point p) {
		Mat srcMat = new Mat(1, 1, CvType.CV_32FC2);
		Mat destMat = new Mat(1, 1, CvType.CV_32FC2);

		srcMat.put(0, 0, new double[] { p.x, p.y });

		Log.i(MainActivity.DEBUG_TAG, srcMat.dump());

		Core.perspectiveTransform(srcMat, destMat, H);

		return new Point(destMat.get(0, 0));
	}

	public static int getDistance(Point p) {
		return (int) Math.sqrt(p.x * p.x + p.y * p.y);
	}

	public int getAngle(Point p) {
		int degree = (int) (((Math.tan(p.x / p.y) / Math.PI) + 0.5) * 180);
		degree -= 90;

		return degree;
	}
}
