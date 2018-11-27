package main_prog;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import main_prog.main_p.Directions;

public class AllThreads {
	
	
	static class Gyro implements Runnable {
		private MotorMonitor m;
		
		Port S3 = LocalEV3.get().getPort("S3");
		EV3GyroSensor sensor = new EV3GyroSensor(S3);

		//Configuration


		SampleProvider sp = sensor.getAngleAndRateMode();
		int value = 0;
		
		public Gyro(MotorMonitor m) {
			this.m = m;
			
			//get initial angle
			float [] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            this.m.setInitAngle( (int)sample[0] );
            
			
		}
		
		public void run() {
			while(true) {
				/* legge giroscopio*/
				float [] sample = new float[sp.sampleSize()];
	            sp.fetchSample(sample, 0);
	            int angle = (int)sample[0];
	            this.m.setAngle(angle);
	            
	            
			}
		}
	}
	
	
	
	

	static class Rotate implements Runnable {
		private MotorMonitor m;
		private Directions dir;

		public Rotate(MotorMonitor m, Directions dir) {
			this.m = m;
			
			this.dir = dir;
            
			
		}
		
		public void run() {
			this.m.gira(this.dir);
		}
	}
	
		
	
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
	
	
	
	static Runnable giradx_A = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.rotate(20);
		}
	};
	static Runnable giradx_B = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.rotate(-20);
		}
	};
	
	static Runnable girasx_A = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.rotate(20);
		}
	};
	static Runnable girasx_B = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.rotate(-20);
		}
	};
}
