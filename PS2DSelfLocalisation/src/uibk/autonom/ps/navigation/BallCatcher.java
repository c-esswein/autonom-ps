package uibk.autonom.ps.navigation;

import android.annotation.TargetApi;
import android.os.Build;
import java.util.NavigableMap;
import java.util.Map.Entry;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import uibk.autonom.ps.activity.MainActivity;
import uibk.autonom.ps.activity.SubProgramm;
import uibk.autonom.ps.colordetector.detectors.ColorThresholdDetector;
import uibk.autonom.ps.robot.Robot;
import uibk.autonom.ps.selflocalisation.Locator;

public class BallCatcher extends Thread implements SubProgramm, FinishedNavigationAction {

	private MainActivity activity;
	private Locator locator;
	private Robot robot;
	private Navigator navigator;
	
	private double curImgSize;
	
	private ColorThresholdDetector detector = new ColorThresholdDetector();
	
	//public enum States{START, TURN, MOVE, CHECK, CACH, CAGED};
	//public States curState = States.START;
	
	private final int MIN_DISTANCE = 8;
	private final double MIN_BALL_SIZE = 200.;
	
	public BallCatcher(Locator locator, MainActivity activity){
		this.activity = activity;
		this.locator = locator;
		
		robot = new Robot();	
	}


	@Override
	public void run() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {}

		findBall();
	}
	
	public void findBall(){
		Point ballPosition = getBallPosition();
		
		if(ballPosition != null && curImgSize > MIN_BALL_SIZE){
			turn2Point();
		}else{
			robot.turn(-15);
			navigator.setChangedPosition(0, 15);
			robot.waitForFinishedMovement();
			findBall();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public Point getBallPosition(){
		Point curImgPosition;
		detector.detect(activity.getCurrentImgFrame());
		NavigableMap<Double, MatOfPoint> contours = detector.getMaxContourSizes(1);
				
		if (contours.size() == 1) {
			Entry<Double, MatOfPoint> entry = contours.firstEntry();
			
			curImgSize = entry.getKey();
			curImgPosition=	detector.getBotttomPoint(entry.getValue());			
		}else{
			curImgSize = 0;
			curImgPosition = null;
		}
		
		return curImgPosition;
	}
	
	public void turn2Point(){
		Point objectPoint = getBallPosition();
		int degree = locator.getAngle(objectPoint);
		
		robot.turn(degree);
		navigator.setChangedPosition(0, -degree);
		robot.waitForFinishedMovement();
		
		move2Point();
	}
	
	public void move2Point(){
		Point objectPoint = getBallPosition();
		int distance = locator.getDistance(objectPoint);
		
		if(distance < MIN_DISTANCE){ 
			this.cageBall();
		}else{
			robot.moveFoward(distance / 2);
			navigator.setChangedPosition(distance / 2, 0);
			robot.waitForFinishedMovement();
			
			turn2Point();
		}	
		
	}
	
	public void cageBall(){
		robot.letDownCager();
		
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {}
		
		navigator.moveToPoint(new Point(100, 100));
	}


	@Override
	public void startAction(Navigator navigator) {
		this.navigator = navigator;
		
		start();
	}


	@Override
	public void setTrackColor(Scalar color) {
		this.detector.setHsvColor(color);
	}
}

