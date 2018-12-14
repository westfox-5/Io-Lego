package utils;

import lejos.robotics.RegulatedMotor;

public class Rotations {		
	private static final int ROTATION_SPEED = 80;

	public static class Right extends Thread {
		private RegulatedMotor A, B;

		public Right(RegulatedMotor A, RegulatedMotor B) {
			this.A = A;
			this.B = B;
		}

		@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);

			
			while (!interrupted()) {
				A.forward();
				B.backward();
			}
			A.stop(true);
			B.stop(true);
		
		}
	}

	public static class Left extends Thread {
		private RegulatedMotor A, B;

		public Left(RegulatedMotor A, RegulatedMotor B) {
			this.A = A;
			this.B = B;
		}

		@Override
		public void run() {
			A.setSpeed(ROTATION_SPEED);
			B.setSpeed(ROTATION_SPEED);

			while (!interrupted()) {
				A.backward();
				B.forward();
			}
			A.stop(true);
			B.stop(true);
				
		}
	}

}
