package utils;

import lejos.robotics.RegulatedMotor;

public class Forward extends Thread {
	
	private RegulatedMotor A, B;
	
	public Forward(RegulatedMotor A, RegulatedMotor B) {
		this.A = A; this.B=B;
	}
	
	@Override
	public void run() {
		A.setSpeed(150);
		B.setSpeed(150);
		
		while(!interrupted()) {
			A.forward();
			B.forward();
		}
		A.stop(true);
		B.stop(true);
	}
	
}
