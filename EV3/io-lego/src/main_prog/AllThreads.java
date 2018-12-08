package main_prog;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import main_prog.MainProgram.Direction;

public class AllThreads {
	
	private static final int DEFAULT_ROTATION = 700;
	private static final int ROTATION_SPEED = 100;
	private static final int ROTATION_CHECK_COLOR  = 500;
	
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
		private Direction dir;

		public Rotate(RotationMonitor m, Direction dir) {
			this.m = m;
			this.dir = dir;	
		}
		
		public void run() {
			while(this.m.rotate(this.dir) == -1) {}
		}
	}
	
	static Runnable A_rotation_forward = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.setSpeed(ROTATION_SPEED);
			Motor.A.forward();
		}
	};
	
	static Runnable B_rotation_forward = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.setSpeed(ROTATION_SPEED);
			Motor.B.forward();
			
		}
	};
	
	static Runnable A_rotation_backward = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.setSpeed(ROTATION_SPEED);
			Motor.A.backward();
		}
	};
	
	static Runnable B_rotation_backward = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.setSpeed(ROTATION_SPEED);
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

	static Runnable D_stop = new Runnable() {
		
		@Override
		public void run() {
			Motor.D.stop();
			
		}
	};
	
	static Runnable A_color_forward = new Runnable() {
		
		
		@Override
		public void run() {
		Motor.A.rotate(ROTATION_CHECK_COLOR);
		}
	};
	
	static Runnable B_color_forward = new Runnable() {
		
		
		@Override
		public void run() {
		Motor.B.rotate(ROTATION_CHECK_COLOR);
		}
	};
	
	static Runnable A_color_backward = new Runnable() {
		
		
		@Override
		public void run() {
		Motor.A.rotate(-ROTATION_CHECK_COLOR);
		}
	};
	
	static Runnable B_color_backward = new Runnable() {
		
		
		@Override
		public void run() {
		Motor.B.rotate(-ROTATION_CHECK_COLOR);
		}
	};

	static Runnable A_next_cell = new Runnable() {
		
		@Override
		public void run() {
			Motor.A.rotate(DEFAULT_ROTATION);
		}
	};
	
	static Runnable B_next_cell = new Runnable() {
		
		@Override
		public void run() {
			Motor.B.rotate(DEFAULT_ROTATION);
			
		}
	};
}
