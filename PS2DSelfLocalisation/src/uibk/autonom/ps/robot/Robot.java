package uibk.autonom.ps.robot;

import ioio.lib.api.exception.ConnectionLostException;

public class Robot {
	protected RobotConnector robotConnector;
	
	public Robot()	{
		robotConnector = RobotConnector.getInstance();
	}
	
	/**
	 * @param distance in cm
	 */
	public void moveFoward(int distance){
		byte[] request = new byte[2];
		byte[] response = new byte[2];
		
		Integer distanceInt = Integer.valueOf(distance);
		
		request[0] = 0x1C;
		request[1] = distanceInt.byteValue();    // (forward in cm);
		
		robotConnector.writeRead(request, response);
	}
	
	/**
	 * @param degree 
	 */
	public void turn(int degree){
		byte[] request = new byte[2];
		byte[] response = new byte[2];
		
		Integer degreeInt = Integer.valueOf(degree);
		
		request[0] = 0x1D;
		request[1] = degreeInt.byteValue();
		
		robotConnector.writeRead(request, response);
	}
}
