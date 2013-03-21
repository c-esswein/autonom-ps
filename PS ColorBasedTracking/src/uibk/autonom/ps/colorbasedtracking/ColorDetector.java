package uibk.autonom.ps.colorbasedtracking;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class ColorDetector {
	
	private Scalar trackColor;
	
	public ColorDetector(){
		trackColor = new Scalar(255, 0, 0);
	}
	
	public Mat detect(Mat inputFrame){
		Mat outputFrame = inputFrame.clone();
		
		double[] black = {0, 0, 0, 0};
		double[] white = {255, 255, 255, 255};
		
		int rows = inputFrame.rows();
		int cols = inputFrame.cols();
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				double[] color = inputFrame.get(i, j);
				
				double absVal = 255 - color[0];
				double absVal1 = 255 - color[1];
				double absVal2 = 255 - color[2];
				
				if(absVal > 30 || absVal1 > 30 || absVal2 > 30){
					outputFrame.put(i, j, black);
				}else{
					outputFrame.put(i, j, white);
				}
				
				
			}
		}
		
		return outputFrame;
	}
	
	public Mat detecdt(Mat inputFrame){
		Mat outputFrame = new Mat();
		Mat calcFrame = new Mat();
		//outputFrame = compress(inputFrame);
		
		//Imgproc.cvtColor(inputFrame, calcFrame, Imgproc.COLOR_RGB2HSV_FULL);
		
		Core.absdiff(inputFrame, trackColor, calcFrame);
		Imgproc.threshold(calcFrame, outputFrame, 0, 255, Imgproc.THRESH_BINARY);
		
		//outputFrame.get
		
		return outputFrame;
	}
	
	private Mat compress(Mat inputFrame){
		Mat outputFrame = new Mat();
		
		Imgproc.pyrDown(inputFrame, outputFrame);
        Imgproc.pyrDown(outputFrame, outputFrame);
        
        return outputFrame;
	}
	
	public void setTrackColor(Scalar trackColor){
		this.trackColor = trackColor;
	}
}
