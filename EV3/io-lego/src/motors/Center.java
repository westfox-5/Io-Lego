package motors;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

public class Center implements Runnable {

	private RegulatedMotor A, B;
	private SampleProvider sp;

	private int rot = 9;

	private int range = 2;
	private int rotRange = 10;

	public Center(RegulatedMotor A, RegulatedMotor B, EV3GyroSensor gyro) {
		this.A = A;
		this.B = B;
		this.sp = gyro.getAngleMode();
	}

	@Override
	public void run() {
		A.setSpeed(30);
		B.setSpeed(30);

		float[] sample = new float[sp.sampleSize()];
		sp.fetchSample(sample, 0);
		int angle = ((int) sample[0] + 360) % 360;

		if (angle >= 90 - range - rotRange && angle <= 90 -  range
		|| angle >= 180 - range - rotRange && angle <= 180 - range
		|| angle >= 270 - range - rotRange && angle <= 270 - range
		|| angle >= 360 - range - rotRange && angle <= 360 - range) {
			
			// left
			A.startSynchronization();
			A.rotate(-rot, true);
			B.rotate(rot, true);
			A.endSynchronization();

		} else if (angle <= 90 +  range + rotRange && angle >= 90 +  range
				|| angle <= 180 + range + rotRange && angle >= 180 + range
				|| angle <= 270 + range + rotRange && angle >= 270 + range
				|| angle <=       range + rotRange && angle >=       range) {
			
			// right
			A.startSynchronization();
			A.rotate(rot, true);
			B.rotate(-rot, true);
			A.endSynchronization();

		}

		A.waitComplete();
		B.waitComplete();

		A.startSynchronization();
		A.stop(true);
		B.stop(true);
		A.endSynchronization();

	}
}
