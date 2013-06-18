package uibk.autonom.ps.navigation;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import uibk.autonom.ps.activity.MainActivity;
import uibk.autonom.ps.activity.R;
import uibk.autonom.ps.activity.SubProgramm;
import uibk.autonom.ps.robot.Robot;
import uibk.autonom.ps.selflocalisation.Locator;

public class Navigator extends Thread implements SubProgramm {

	private Robot robot;
	private Marker[] markers;
	private MainActivity activity;
	private Locator locator;

	private float factorX = 1; //150 / MAX_X;
	private float factorY = 1; //150 / MAX_Y;
	private static final int MAX_X = 100;
	private static final int MAX_Y = 100;

	private Point curPosition = new Point(0, 0);
	private double curDirection = 0;
	private int setBeacons = 0;
	private int beaconsCount = 2;
	
	private Button nextBtn;
	
	private FinishedNavigationAction finishedAction;

	// public enum States{START, SELECT_COLORS, CALIBRATE, };
	// public States curState = States.START;

	public Navigator(MainActivity activity, Locator locator, FinishedNavigationAction finishedAction) {
		this.activity = activity;
		this.locator = locator;
		this.finishedAction = finishedAction;
		
		markers = new Marker[beaconsCount];
		robot = new Robot();
		
		nextBtn = (Button) activity.findViewById(R.id.buttonNext);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonNext_click();
			}
		});
		nextBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		
		Marker[] markers = findMarkers();
		
		Point marker1Pos = locator.img2World(markers[0].curImgPosition);
		Point marker2Pos = locator.img2World(markers[1].curImgPosition);
		double dist1 = Locator.getDistance(marker1Pos)/factorX;
		double dist2 = Locator.getDistance(marker2Pos)/factorX;
		curPosition = findRobotPosition(markers[0], markers[1], dist1, dist2);
		curDirection = findRobotDirection(markers[0], marker1Pos);
		
		Log.i(MainActivity.DEBUG_TAG, "curPosition: " + curPosition);
		
		if(finishedAction != null){
			finishedAction.startAction(this);			
		}else{
			moveToPoint(new Point(100, 100));
		}
	}

	private void buttonNext_click() {
		Scalar curColor = activity.currentSelectedColor;
		
		if(setBeacons == 0){
			finishedAction.setTrackColor(curColor);
			setBeacons = 1;
		}else{
			selectColors(curColor);	
		}
	}

	public void setNextButtonState(int i) {
		if (i == 0)
			nextBtn.setVisibility(View.GONE);
		else
			nextBtn.setText("Next Beacon: " + i);
	}

	public void selectColors(Scalar color){
		double x, y;
		x = y = 0;

		switch (setBeacons) {
		case 1:
			x = 0;
			y = MAX_Y;
			break;
		case 2:
			x = MAX_X/2;
			y = MAX_Y;
			break;
		case 3:
			x = MAX_X;
			y = MAX_Y;
			break;
		case 4:
			x = MAX_X;
			y = 0;
			break;
		case 5:
			x = MAX_X/2;
			y = 0;
			break;
		case 6:
			x = 0;
			y = 0;
			break;
		}
		
		markers[setBeacons - 1] = new Marker(setBeacons - 1, color, new Point(x, y));
		
		setBeacons++;
		if(setBeacons > beaconsCount){
			setNextButtonState(0);
			start();	// start thread
		}
		else{
			setNextButtonState(setBeacons);			
		}

	}

	public Marker[] findMarkers() {
		return findMarkers(true);
	}

	/**
	 * finds two markers in current image and return them
	 */
	public Marker[] findMarkers(boolean toggleDirection) {
		Marker m1 = null;
		Marker m2 = null;
		Mat curImgFrame = activity.getCurrentImgFrame();
		
		for(Marker m : markers){
			m.calculateImgPosition(curImgFrame);
			Log.i("MARK", "Marker: " +m.curImgSize);
			if(m1 == null || m.curImgSize > m1.curImgSize){
				m1 = m;
			}else if(m2 == null || m.curImgSize > m2.curImgSize){
				m2 = m; 
			}
		}
		
		if(m1 != null && m2 == null){	// fixing sorting error if markers list is sorted
			m2 = markers[markers.length - 1];
		}

		if(m1 == null || m2 == null || m1 == m2 || !m1.isInImg() || !m2.isInImg()){
			// TODO turn and try again
			Log.i(MainActivity.DEBUG_TAG, "keine zwei punkte in view");
			
			if(toggleDirection){
				curDirection = -2.;
				robot.turn(2);
			}else{
				curDirection = 2.;
				robot.turn(-4);
			}
			
			robot.waitForFinishedMovement();
			findMarkers(!toggleDirection);
		}
		
		Marker[] returnMarkers = new Marker[2];
		returnMarkers[0] = m1;
		returnMarkers[1] = m2;
		return returnMarkers;
	}

	/**
	 * calculate current position
	 * @param m1 marker1
	 * @param m2 marker2
	 * @param r1 current distance from robot to marker1
	 * @param r2 current distance from robot to marker2
	 * @return current position of robot in virtual space
	 */
	public static Point findRobotPosition(Marker m1, Marker m2, double r1, double r2) {
        Point p1 = m1.getPosition();
        Point p2 = m2.getPosition();

        double d = Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
        //if (d > r1 + r2)
            //return null;

        double d1 = (Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(d, 2))/ (2 * d);
        double h = Math.sqrt(Math.pow(r1, 2) - Math.pow(d1, 2));

        Point p3 = new Point();
        p3.x = p1.x + (d1 * (p2.x - p1.x)) / d;
        p3.y = p1.y + (d1 * (p2.y - p1.y)) / d;

        Point p4 = new Point();
        p4.x = p3.x + (h * (p2.y - p1.y)) / d;
        p4.y = p3.y - (h * (p2.x - p1.x)) / d;

        Point p5 = new Point();
        p5.x = p3.x - (h * (p2.y - p1.y)) / d;
        p5.y = p3.y + (h * (p2.x - p1.x)) / d;
        
        
        if(between(p4.x, 0, MAX_X) && between(p4.y, 0, MAX_Y)) return p4;
        if(between(p5.x, 0, MAX_X) && between(p5.y, 0, MAX_Y)) return p5;
        
        return null;
    }
	
	private static boolean between(double val, double d1, double d2) {
        return (val >= d1 && val <= d2);        
    }
	
	/**
	 * 
	 * @param m
	 * @param p
	 * @return
	 */
	public static double findRobotDirection(Marker m, Point p){
		
		//TODO unused
		
		return 0;
	}

	/**
	 * moves robot to robot p
	 * 
	 * @param p Point p with cords corresponding to virtual net
	 */
	public void moveToPoint(Point p) {
		double xDist = curPosition.x - p.x;
		double yDist = curPosition.y - p.y;
		
		double angle = 90 - Math.tan(yDist / xDist);
		
		double distance = Math.sqrt(xDist * xDist + yDist * yDist); 
		
		robot.turn((int) - angle);
		robot.waitForFinishedMovement();
		curDirection = curDirection + angle;
		
		robot.moveFoward((int) distance);
		robot.waitForFinishedMovement();
		curPosition = p;
	}

	public Point getRealCords(Point p) {
		return new Point(p.x * factorX, p.y * factorY);
	}
	
	public Point getVirtualCords(Point p) {
		return new Point(p.x / factorX, p.y / factorY);
	}
	

	
	public void setChangedPosition(int distance, int angle){
		this.curDirection += angle;
		
		
		this.curPosition.x += Math.cos(this.curDirection) * distance;
		this.curPosition.y += Math.sin(this.curDirection) * distance;
	}

}
