package uibk.autonom.ps.robot;

import uibk.autonom.ps.activity.MainActivity;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class RobotConnector {
	protected IOIO ioio_;
	protected TwiMaster twi;
	protected int address = 0x69;
	protected PwmOutput servo_;
	
	private RobotConnector() {}
	
	private static class SingletonHolder {
		private final static RobotConnector INSTANCE = new RobotConnector();
	}
	public static RobotConnector getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void setIOIO(IOIO ioio){
		this.ioio_ = ioio;
		
		try {
			twi = ioio_.openTwiMaster(1, TwiMaster.Rate.RATE_100KHz, false);
			servo_ = ioio_.openPwmOutput(10, 50);
		} catch (ConnectionLostException e) {
			handleException(e);
		}
	}
	
	public void writeRead(byte[] request, byte[] response){
		try {
			twi.writeRead(address, false, request, request.length, response, response.length);
		} catch (ConnectionLostException e) {
			handleException(e);
		} catch (InterruptedException e) {
			handleException(e);
		}
	}
	
	public void setCager(int percent){
		try {
			servo_.setDutyCycle(0.0528f + percent * 0.0005f);
		} catch (ConnectionLostException e) {
			handleException(e);
		}		
	}
	
	public void closeConnection(){
		twi.close();
	}
	
	public void handleException(Exception e){
		MainActivity.showMessage(e.getMessage());
	}
}
