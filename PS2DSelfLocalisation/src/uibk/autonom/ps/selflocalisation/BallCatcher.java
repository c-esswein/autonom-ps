package uibk.autonom.ps.selflocalisation;

import org.opencv.core.Point;

import uibk.autonom.ps.activity.SubProgramm;
import uibk.autonom.ps.colordetector.ColorDetector;
import uibk.autonom.ps.robot.Robot;

public class BallCatcher extends Thread implements SubProgramm {

	private ColorDetector colorDetector;
	private Locator locator;
	private Robot robot;
	
	//public enum States{START, TURN, MOVE, CHECK, CACH, CAGED};
	//public States curState = States.START;
	
	private final int minDistance = 8;
	
	public BallCatcher(Locator locator, ColorDetector colorDetector){
		this.colorDetector = colorDetector;
		this.locator = locator;
		
		robot = new Robot();	
	}


	@Override
	public void run() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {}

		turn2Point();
	}
	
	public void turn2Point(){
		Point objectPoint = getCurCenterPoint();
		int degree = locator.getAngle(objectPoint);
		
		robot.turn(degree);
		robot.waitForFinishedMovement();
		
		move2Point();
	}
	
	public void move2Point(){
		Point objectPoint = getCurCenterPoint();
		int distance = locator.getDistance(objectPoint);
		
		if(distance < minDistance){ 
			this.cageBall();
		}else{
			robot.moveFoward(distance / 2);
			robot.waitForFinishedMovement();
			
			turn2Point();
		}	
		
	}
	
	public Point getCurCenterPoint(){
		Point curCenterPoint = colorDetector.getCenterPoints(1).get(0);
		
		return locator.img2World(curCenterPoint);
	}
	
	public void cageBall(){
		robot.letDownCager();
	}
	
}

