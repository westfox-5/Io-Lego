package main_prog;

import lejos.hardware.motor.Motor;

public class AllThreads {

	static Runnable A_avanza = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.setSpeed(720);
			Motor.A.forward();
		}
	};
	
	static Runnable B_avanza = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.setSpeed(720);
			Motor.B.forward();
			
		}
	};

	static Runnable B_stop = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.stop();
			
		}
	};
	
	static Runnable A_stop = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.stop();
			
		}
	};
	
	static Runnable A_open_close = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.rotate(125);
			Motor.A.rotate(-125);

		}
	};
	
	static Runnable A_close_open = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.rotate(-125);
			Motor.A.rotate(125);

		}
	};
	
}
