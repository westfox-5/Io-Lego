package utils;

import lejos.robotics.RegulatedMotor;

public class Rotations2 {		
	private static final int ROTATION_SPEED = 80;
	private RegulatedMotor A,B;
	
	 public Rotations2(RegulatedMotor A, RegulatedMotor B) {
		 this.A=A; this.B=B;
	 }

	public class Right extends Thread {
				@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);

			A.forward();
			B.backward();
			while (!interrupted()) {
				Thread.yield();
			}
		}
	}

	public class Left extends Thread {
		@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);

			A.backward();
			B.forward();
			while (!interrupted()) {
				Thread.yield();
			}	
		}
	}
	
	public void stop() {
		A.stop(true);
		B.stop(true);
	}

}
