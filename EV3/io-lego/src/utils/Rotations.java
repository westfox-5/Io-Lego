package utils;

import lejos.robotics.RegulatedMotor;

public class Rotations {		
	private static final int ROTATION_SPEED = 60;

	public static class Right implements Runnable {
		private RegulatedMotor A, B;
		
		private volatile boolean exit = false;

		public Right(RegulatedMotor A, RegulatedMotor B) {
			this.A = A;
			this.B = B;
		}
		
		public void stop() {
			this.exit = true;
		}

		@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);
			
			while (!exit) {				
				A.forward();
				B.backward();
			}		
			
			A.startSynchronization();
			A.stop(true);
			B.stop(true);
			A.endSynchronization();
		}
	}

	public static class Left implements Runnable {
		private RegulatedMotor A, B;
		private volatile boolean exit = false;

		public Left(RegulatedMotor A, RegulatedMotor B) {
			this.A = A;
			this.B = B;
		}

		public void stop() {
			exit = true;
		}

		@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);
			
			while (!exit) {
				A.backward();
				B.forward();
			}
			
			A.startSynchronization();
			A.stop(true);
			B.stop(true);
			A.endSynchronization();
							
		}
	}

}
