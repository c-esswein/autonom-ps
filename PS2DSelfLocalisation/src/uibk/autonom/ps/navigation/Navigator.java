package uibk.autonom.ps.navigation;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

import uibk.autonom.ps.activity.MainActivity;
import uibk.autonom.ps.activity.SubProgramm;
import uibk.autonom.ps.robot.Robot;

public class Navigator extends Thread implements SubProgramm {

	private Robot robot;
	private Marker[] markers;
	private MainActivity activity;
	
	private float factorX = 300/100;
	private float factorY = 150/100;
	
	private Point curPosition;
	
	//public enum States{START, SELECT_COLORS, CALIBRATE, };
	//public States curState = States.START;
	
	public Navigator(MainActivity activity){
		this.activity = activity;
		
		robot = new Robot();
	}


	@Override
	public void run() {
		selectColors();
	}
	
	public void selectColors(){
		// TODO
		
		// repeat 6 times
			// show text: select color1 for point 1 (top left)
			// show button color selected --> current color from mainactivity will be selected
			// store into markers...
		
		findMarkers();
	}
	
	/**
	 * finds markers in current image
	 */
	public void findMarkers(){
		Marker m1 = new Marker(new Scalar(0), new Point());
		Marker m2 = m1;
		
		
		
		findRobotPosition(m1, m2);
	}
	
	public void findRobotPosition(Marker m1, Marker m2){
		// calculate current position and write into curPosition
	}
	
	/**
	 * moves robot to robot p
	 * @param p Point p with cords corresponding to virtual net
	 */
	public void moveToPoint(Point p){
		
	}
	
	public Point getRealCords(Point p){
		return new Point(p.x * factorX, p.y * factorY);
	}
	
	
}

