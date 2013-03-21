package uibk.autonom.ps.colorbasedtracking;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements CvCameraViewListener2 {
	private static final String DEBUG_TAG = "PS CBT:";
	
	//private Mat mRgba;
	
	private CameraBridgeViewBase mOpenCvCameraView;
	private ColorDetector colorDetector;
	
	public MainActivity(){
		Log.i(DEBUG_TAG, "class created");
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_view);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        colorDetector = new ColorDetector();
	}
	
	@Override
    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(DEBUG_TAG, "OpenCV loaded successfully");
                        mOpenCvCameraView.enableView();
                        //mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        });
    }

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		return colorDetector.detect(inputFrame.rgba());
	}
}
