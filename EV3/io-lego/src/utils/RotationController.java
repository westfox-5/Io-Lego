package utils;

import lejos.hardware.lcd.LCD;
import lejos.robotics.RegulatedMotor;
import main_prog.MainProgram.*;

public class RotationController extends Thread {
	private RotationMonitor m;
	private Direction dir;

	private RegulatedMotor A, B;
	
	public RotationController(RotationMonitor m, Direction dir, RegulatedMotor A, RegulatedMotor B) {
		this.m = m;
		this.dir = dir;
		this.A = A; this.B = B;
	}
	
	@Override
	public void run() {			
		Rotations.Left l =  new Rotations.Left(A, B);
		Rotations.Right r = new Rotations.Right(A, B);
		
		while( this.m.rotate(this.dir, l, r ) == -1) {}
		
	}
}
