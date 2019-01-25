package motors;

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
			A.rotate(-120,true);
			B.rotate(-120,true);				
			A.endSynchronization();

			A.waitComplete();
			B.waitComplete();
			
			A.startSynchronization();
			A.stop(true);
			B.stop(true);
			A.endSynchronization();
		}
	
}
