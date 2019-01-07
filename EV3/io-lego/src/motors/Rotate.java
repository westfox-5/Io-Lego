package motors;

import lejos.robotics.RegulatedMotor;
import main_prog.MainProgram.Direction;

public class Rotate implements Runnable {
	private RegulatedMotor A, B;
	private Direction dir;
	
	public Rotate(RegulatedMotor A, RegulatedMotor B, Direction dir) {
		this.A = A; this.B=B;
		this.dir = dir;
	}	
	
	@Override
	public void run() {
		A.setSpeed(80);
		B.setSpeed(80);
		
		int rotA, rotB;
		switch(dir) {
		case RIGHT:
			rotA = 147;
			rotB = -147;
			break;
		case LEFT:
			rotA = -147;
			rotB = 147;
			break;
		case REVERSE:
			rotA = 292;
			rotB = -292;
			break;
		default: 
			rotA=0;
			rotB=0;
			break;
		}
		
		if(dir!=Direction.DOWN) {
			A.startSynchronization();
			A.rotate(rotA,true);
			B.rotate(rotB,true);				
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
