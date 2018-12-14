package utils;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;

public class GyroscopeThread extends Thread{
	private RotationMonitor m;
	private SampleProvider sp;
	private EV3GyroSensor sensor;
	
	public GyroscopeThread(EV3GyroSensor gyroSensor, RotationMonitor m) {
		this.m = m;	
		this.sensor = gyroSensor;
		sp = gyroSensor.getAngleMode();
	}
	
	public void reset() {
		this.sensor.reset();
	}
	
	@Override
	public void run() {
		while(true) {
			/* legge giroscopio*/
			float [] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            this.m.setAngle((int)sample[0]);	            
		}
	}
}