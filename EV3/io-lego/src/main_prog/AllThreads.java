package main_prog;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import main_prog.main_p.Directions;

public class AllThreads {
	
	static int speed = 100;
	
	
	
	static class Gyro implements Runnable {
		private RotationMonitor m;
		
		Port S3;
		EV3GyroSensor sensor;

		SampleProvider sp;
		
		public Gyro(RotationMonitor m) {
			this.m = m;
			S3  = LocalEV3.get().getPort("S3");
			sensor = new EV3GyroSensor(S3);
			
			sp = sensor.getAngleMode();
		}
		
		public void run() {
			while(true) {
				/* legge giroscopio*/
				float [] sample = new float[sp.sampleSize()];
	            sp.fetchSample(sample, 0);
	            
	            this.m.setAngle((int)sample[0]);
	            
	            
			}
		}
	}

	static class Rotate implements Runnable {
		private RotationMonitor m;
		private Directions dir;

		public Rotate(RotationMonitor m, Directions dir) {
			this.m = m;
			
			this.dir = dir;	
		}
		
		public void run() {
			LCD.clear();
			LCD.drawString("start rotation", 0, 4);
			
			while(this.m.gira(this.dir) == -1) {}
	
		}
	}
	
		
	
	static Runnable A_avanza = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.setSpeed(speed);
			Motor.A.forward();
		}
	};
	
	static Runnable B_avanza = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.setSpeed(speed);
			Motor.B.forward();
			
		}
	};
	
	static Runnable A_indietro = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.setSpeed(speed);
			Motor.A.backward();
		}
	};
	
	static Runnable B_indietro = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.setSpeed(speed);
			Motor.B.backward();
			
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
	static Runnable D_start = new Runnable() {
		
		@Override
		public void run() {
			Motor.D.setSpeed(720);
			Motor.D.forward();
			
		}
	};
}
