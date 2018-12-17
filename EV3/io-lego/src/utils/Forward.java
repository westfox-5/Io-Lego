package utils;

import lejos.robotics.RegulatedMotor;

public class Forward implements Runnable {
	
	private RegulatedMotor A, B;
	private volatile boolean exit = false;
	
	public Forward(RegulatedMotor A, RegulatedMotor B) {
		this.A = A; this.B=B;
	}
	
	public void stop() {
		this.exit = true;
	}
	
	@Override
	public void run() {
		A.setSpeed(150);
		B.setSpeed(150);
		
		while(!exit) {
			A.forward();
			B.forward();
		}
		
		A.startSynchronization();
		A.stop(true);
		B.stop(true);
		A.endSynchronization();
		
	}
	
	
}
