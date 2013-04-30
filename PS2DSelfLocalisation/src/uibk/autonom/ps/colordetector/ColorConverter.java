package uibk.autonom.ps.colordetector;

import org.opencv.core.Scalar;

public class ColorConverter {

	// Color radius for range checking in HSV color space
	private static Scalar mColorRadius = new Scalar(40, 45, 45, 0);
    
    public static void getHsvColorRange(Scalar hsvColor, Scalar lowerBound, Scalar upperBound) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        lowerBound.val[0] = minH;
        upperBound.val[0] = maxH;

        lowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        upperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        lowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        upperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        lowerBound.val[3] = 0;
        upperBound.val[3] = 255;
    }
	
}
