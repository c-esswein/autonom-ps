package uibk.autonom.ps.robot;


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
	
	public void letDownCager(){
		robotConnector.setCager(50);
	}
	
	public void pullUpCager(){
		robotConnector.setCager(-40);
	}
	
	public void waitForFinishedMovement(){
		short[] curPosition = getPosition();
		short[] oldPosition = new short[3];
		
		do{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
			
			oldPosition = curPosition;
			curPosition = getPosition();
		}while(!eqalPositions(curPosition, oldPosition));
		
	}
	
	private boolean eqalPositions(short[] pos1, short[] pos2){
		
		for(int i = 0; i < 3; i++){
			if(pos1[i] != pos2[i])
				return false;
		}
		
		return true;
	}
	
	public short[] getPosition(){
		short[] curPosition = new short[3];
		byte[] request = new byte[2];
		byte[] response = new byte[6];
		
		request[0] = 0x1B;		//get position

		robotConnector.writeRead(request, response);
		
		curPosition[0] = (short) (((response[1] & 0xFF) << 8) | (response[0] & 0xFF));
		curPosition[1] = (short) (((response[3] & 0xFF) << 8) | (response[2] & 0xFF));
		curPosition[2] = (short) (((response[5] & 0xFF) << 8) | (response[4] & 0xFF));
		
		return curPosition;
	}
}
