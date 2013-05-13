package uibk.autonom.ps.navigation;

import uibk.autonom.ps.activity.SubProgramm;
import uibk.autonom.ps.robot.Robot;

public class Navigator extends Thread implements SubProgramm {

	private Robot robot;
	
	//public enum States{START, TURN, MOVE, CHECK, CACH, CAGED};
	//public States curState = States.START;
	
	public Navigator(){
		
		robot = new Robot();	
	}


	@Override
	public void run() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {}

	}
	
	
}

