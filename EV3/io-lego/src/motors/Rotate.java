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
		A.setSpeed(100);
		B.setSpeed(100);
		
		int rotA, rotB;
		switch(dir) {
		case RIGHT:
			rotA = 150;
			rotB = -150;
			break;
		case LEFT:
			rotA = -150;
			rotB = 150;
			break;
		case REVERSE:
			rotA = 300;
			rotB = -300;
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
