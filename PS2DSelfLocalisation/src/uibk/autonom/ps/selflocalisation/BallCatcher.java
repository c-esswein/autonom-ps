package uibk.autonom.ps.selflocalisation;

import org.opencv.core.Point;

import uibk.autonom.ps.robot.Robot;

public class BallCatcher implements SubProgramm {

	private CenterPointProvider centerPointProvider;
	private Locator locator;
	private Robot robot;
	
	//public enum States{START, TURN, MOVE, CHECK, CACH, CAGED};
	//public States curState = States.START;
	
	private final int minDistance = 8;
	
	public BallCatcher(Locator locator, CenterPointProvider centerPointProvider){
		this.centerPointProvider = centerPointProvider;
		this.locator = locator;
		
		robot = new Robot();
		
		turn2Point();
	}
	
	public void turn2Point(){
		Point objectPoint = centerPointProvider.getCenterPoint();
		int degree = locator.getAngle(objectPoint);
		
		robot.turn(degree);
		robot.waitForFinishedMovement();
		
		move2Point();
	}
	
	public void move2Point(){
		Point objectPoint = centerPointProvider.getCenterPoint();
		int distance = locator.getDistance(objectPoint);
		
		if(distance < minDistance){ 
			this.cageBall();
		}else{
			robot.moveFoward(distance / 2);
			robot.waitForFinishedMovement();
			
			turn2Point();
		}	
		
	}
	
	public void cageBall(){
		robot.letDownCager();
	}
	
}

