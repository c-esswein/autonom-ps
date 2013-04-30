package uibk.autonom.ps.selflocalisation;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import uibk.autonom.ps.selflocalisation.R;
import uibk.autonom.ps.colordetector.ColorDetector;
import uibk.autonom.ps.colordetector.ColorSelector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MainActivity extends IOIOActivity implements OnTouchListener, CvCameraViewListener2 {
	private static final String DEBUG_TAG = "PS CBT:";
	
	private static Context context;
	
	private Mat currentRgba;
	private Scalar currentSelectedColor = null;
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private ColorDetector colorDetector;
	private ColorSelector colorSelector;
	private boolean showFiltered = false;
	
	private Locator locator;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		 @Override
         public void onManagerConnected(int status) {
             switch (status) {
                 case LoaderCallbackInterface.SUCCESS:
                 {
                     Log.i(DEBUG_TAG, "OpenCV loaded successfully");
                     mOpenCvCameraView.enableView();
                     mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                 } break;
                 default:
                 {
                     super.onManagerConnected(status);
                 } break;
             }
         }
    };
    

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this.getApplicationContext();
		
		setContentView(R.layout.main_view);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        colorDetector = new ColorDetector();
        locator = new Locator();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		switch (item.getItemId()) {
		case R.id.calibrate:
			locator.calibrate(currentRgba, currentSelectedColor);
			showMessage("Kamera wurde kalibriert!");
			return true;
		case R.id.settings:
			
			return true;
		case R.id.view_mode:
			showFiltered = !showFiltered;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
	
	 @Override
	 public void onPause(){
	     super.onPause();
	     if (mOpenCvCameraView != null){
	         mOpenCvCameraView.disableView();
	     }
	 }

	 public void onDestroy(){
	     super.onDestroy();
	     if (mOpenCvCameraView != null){
	         mOpenCvCameraView.disableView();
	     }
	 }

	@Override
	public void onCameraViewStarted(int width, int height){
		currentRgba = new Mat(height, width, CvType.CV_8UC4);
		
		int xOffset = (mOpenCvCameraView.getWidth() - width) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - height) / 2;
        
		colorSelector = new ColorSelector(width, height, xOffset, yOffset);
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		currentRgba = inputFrame.rgba();
		
		if (currentSelectedColor != null) {
			try {
				if(showFiltered){
					currentRgba = colorDetector.detect(currentRgba);
				}else{
					colorDetector.detect(currentRgba);					
				}
	
				//List<Point> centers = colorDetector.getBottomPoints(2);
				List<Point> centers = colorDetector.getCenterPoints(4);
	
				for (Point p : centers) {
					Core.rectangle(currentRgba, new Point(p.x - 10, p.y - 10),
							new Point(p.x + 10, p.y + 10), new Scalar(255, 0,
									255, 0));
				}
				
			} catch (Exception ex) {
				Log.i(DEBUG_TAG, "exception: " + ex.getMessage());
			}
		}
		
		return currentRgba;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		
		currentSelectedColor = colorSelector.Select(currentRgba, (int)event.getX(), (int)event.getY());
		if(currentSelectedColor != null){
			colorDetector.setHsvColor(currentSelectedColor);	
		}
		
		return false;
    }
	
	public static void showMessage(String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper()	{
		return new Looper();
	}
 

}
