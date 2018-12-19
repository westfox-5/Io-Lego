package utils;

import lejos.robotics.RegulatedMotor;
import main_prog.MainProgram.Direction;

public class Rotate implements Runnable {
	private RegulatedMotor A, B;
	private volatile boolean exit = false;
	private Direction dir;
	
	public Rotate(RegulatedMotor A, RegulatedMotor B, Direction dir) {
		this.A = A; this.B=B;
		this.dir = dir;
	}
	
	public void stop() {
		this.exit = true;
	}
	
	@Override
	public void run() {
		A.setSpeed(80);
		B.setSpeed(80);
		
		if(dir == Direction.RIGHT) {
			A.startSynchronization();
			A.rotate(147,true);
			B.rotate(-147,true);				
			A.endSynchronization();
		} else if(dir == Direction.LEFT) {
			A.startSynchronization();
			A.rotate(-147,true);
			B.rotate(147,true);				
			A.endSynchronization();
		} else if(dir == Direction.REVERSE) {
			A.startSynchronization();
			A.rotate(292,true);
			B.rotate(-292,true);				
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
