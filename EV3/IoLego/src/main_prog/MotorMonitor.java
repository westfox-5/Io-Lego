package main_prog;

import main_prog.main_p.Directions;

public class MotorMonitor {
	private int initAngle;
	private int angle;
	
	public synchronized void setInitAngle(int a) {
		this.initAngle = a;
	}
	
	public synchronized void setAngle(int a) {
		this.angle = a;	
	}
	
	public synchronized void gira(Directions dir) {
		Thread[] gira_destra = new Thread[2];
		gira_destra[0] = new Thread(AllThreads.giradx_A);
		gira_destra[1] = new Thread(AllThreads.giradx_B);
		
		Thread[] gira_sinistra = new Thread[2];
		gira_sinistra[0] = new Thread(AllThreads.girasx_A);
		gira_sinistra[1] = new Thread(AllThreads.girasx_B);
		
		
		switch(dir) {
		case UP : 
			
			break;
		case DOWN:
			while(angle > 0) {
				// girare destra
			} 
			while(angle < 0) {
				// girare sinistra
			}
			
			break;
		case LEFT:
			
			break;
		case RIGHT:
			
			break;
		}
		
	}
}
