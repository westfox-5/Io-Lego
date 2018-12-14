package utils;

import lejos.robotics.RegulatedMotor;

public class Reset extends Thread{
		private RegulatedMotor A, B;
		
		public Reset(RegulatedMotor A, RegulatedMotor B) {
			this.A = A; this.B = B;
		}
		
		@Override
		public void run() {
			A.setSpeed(60);
			B.setSpeed(60);
		
			A.startSynchronization();
			A.rotate(-100);
			B.rotate(-100);				
			A.endSynchronization();
			
			A.waitComplete();
			
			A.stop(true);
			B.stop(true);
		}
	
}
