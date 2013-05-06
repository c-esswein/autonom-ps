package uibk.autonom.ps.colordetector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorSelector {

	private int cameraWidth;
	private int cameraHeight;
	private int imageXOffset;
	private int imageYOffset;
	
	public ColorSelector(int width, int height, int xOffset, int yOffset){
		cameraWidth = width;
		cameraHeight = height;
		imageXOffset = xOffset;
		imageYOffset = yOffset;
	}
	
	public Scalar Select(Mat cameraRgba, int x, int y){
		Scalar blobColorHsv;
		
		x -= imageXOffset;
        y -= imageYOffset;

        if ((x < 0) || (y < 0) || (x > cameraWidth) || (y > cameraHeight)) return null;

        Rect touchedRect = new Rect();

        // 4x4 rechteck zum selektieren, checkt outerbound
        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cameraWidth) ? x + 4 - touchedRect.x : cameraWidth - touchedRect.x;
        touchedRect.height = (y+4 < cameraHeight) ? y + 4 - touchedRect.y : cameraHeight - touchedRect.y;
        
        Mat touchedRegionRgba = cameraRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        blobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < blobColorHsv.val.length; i++)
            blobColorHsv.val[i] /= pointCount;
        

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return blobColorHsv;
	}
	
	public int[] removeOffset(int x, int y){ // TODO title ^^
		return new int[] {x- imageXOffset, y - imageYOffset};
	}
	
}
