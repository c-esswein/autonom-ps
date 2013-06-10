package uibk.autonom.ps.colordetector.detectors;

import java.util.ArrayList;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

// http://docs.opencv.org/doc/tutorials/imgproc/histograms/back_projection/back_projection.html
// http://code.opencv.org/projects/opencv/repository/revisions/master/raw/samples/cpp/tutorial_code/Histograms_Matching/calcBackProject_Demo2.cpp
//http://code.google.com/p/javacv/source/browse/OpenCV2_Cookbook/src/opencv2_cookbook/chapter04/ContentFinder.scala?repo=examples&r=c101e12e53ce911dce11ea70a47189f688d400ba

public class ColorHistogramDetector extends ColorThresholdDetector {
	
	//private cvHistogram histogram;
	
	//histogram resolution for hue and saturation
	static final int hbins = 30;//, sbins = 32;


	public ColorHistogramDetector() {
		// TODO Auto-generated constructor stub
	}

	public synchronized Mat detect(Mat inputFrame) {
		Mat calcFrame = new Mat();

		Imgproc.cvtColor(inputFrame, calcFrame, Imgproc.COLOR_RGB2HSV);
		
		String wtff=calcFrame.dump();
		
		//extract hue channel
		Mat hue = calcFrame;/*new Mat();
		MatOfInt ch = new MatOfInt(0,0);
		ArrayList<Mat> src = new ArrayList<Mat>();
		src.add(calcFrame)*/;
		ArrayList<Mat> dst = new ArrayList<Mat>();
		dst.add(hue);
		//Core.mixChannels(src, dst, ch);
		String wtf3=dst.get(0).dump();
		
		Mat fillImg = new Mat(16, 16, CvType.CV_8UC3);
		fillImg.setTo(hsvColor);
		String fillDmp=fillImg.dump();
		MatOfInt histSize=new MatOfInt(hbins,hbins);
		
		// hue varies from 0 to 179, see cvtColor
		// saturation varies from 0 (black-gray-white) to
		// 255 (pure spectrum color)
		MatOfFloat ranges = new MatOfFloat( 0,180,0,256 );

		Mat hist = new Mat();

		// we compute the histogram from the 0-th and 1-st channels
		MatOfInt channels = new MatOfInt(0, 1);
		
		ArrayList<Mat> fillImgs=new ArrayList<Mat>();
		fillImgs.add(fillImg);
		Imgproc.calcHist(fillImgs, channels, new Mat(), hist, histSize, ranges);
		String histDmp=hist.dump();
		outputFrame = new Mat();
		String wtf1=dst.get(0).dump();
		Imgproc.calcBackProject(dst, channels, hist, calcFrame, ranges, 1);
		String wtf2=dst.get(0).dump();
		String wtf = calcFrame.dump();
		int w = inputFrame.cols(); int h = inputFrame.rows();
		int bin_w = (int) Math.round( (double) w / hbins );
		Mat histImg = new Mat( w, h, CvType.CV_8UC3 );

		for( int i = 0; i < hbins; i ++ )
		   { Core.rectangle( histImg, new Point( i*bin_w, h ), new Point( (i+1)*bin_w, h - Math.round( hist.get(0, i)[0]*h/255.0 ) ), new Scalar( 0, 0, 255 ), -1 ); }


		hist.release();
		fillImg.release();
		
		Imgproc.cvtColor(histImg, calcFrame, Imgproc.COLOR_RGB2HSV);

		return calcFrame;
	}
}
