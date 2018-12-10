package main_prog;

import lejos.hardware.lcd.LCD;
import main_prog.MainProgram.Direction;

public class RotationMonitor {
	private int angle;
	private boolean set;

	RotationMonitor(){
		this.angle=0;
	}
	
	public synchronized void setAngle(int a) {
		angle = a %360;
		set=true;
		notify();
	}
	
	public synchronized int rotate(Direction dir) {
		Thread[] gira_destra = new Thread[2];
		gira_destra[0] = new Thread(AllThreads.A_rotation_forward);
		gira_destra[1] = new Thread(AllThreads.B_rotation_backward);
		
		Thread[] gira_sinistra = new Thread[2];
		gira_sinistra[0] = new Thread(AllThreads.A_rotation_backward);
		gira_sinistra[1] = new Thread(AllThreads.B_rotation_forward);
		
		Thread stop_A= new Thread(AllThreads.A_stop);
		Thread stop_B= new Thread(AllThreads.B_stop);
		
		
		while(!set) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return -1;
			}
		}
		
		set=false;
		int correctAngle;
		int diff_positive=0;
		switch(dir) {
		case UP : 
			correctAngle = 180;
			break;
		case DOWN:
			correctAngle = 0;
			break;
		case LEFT:
			correctAngle = 90;
			break;
		case RIGHT:
			correctAngle = -90;
			break;
		default: 
			correctAngle = this.angle;
			break;
		}
		
		int diff = correctAngle-angle;
			
		if(diff < 0) {
			diff_positive= -1;
			
			// girare destra
			gira_destra[0].start();
			gira_destra[1].start();
			
		} 
		if(diff > 0) {			
			diff_positive = 1;
			// girare sinistra
			gira_sinistra[0].start();
			gira_sinistra[1].start();
			
		}
	
		if( diff == 0) {
		
			try {	
				stop_A.start();
				stop_B.start();			
				
				if(diff_positive==1) {
					gira_sinistra[0].join();
					gira_sinistra[1].join();
				} else if(diff_positive == -1) {
					gira_destra[0].join();
					gira_destra[1].join();
				}
							
				
			}catch(InterruptedException e) {
				LCD.drawString("fermata", 0, 4);
			}
			
			return 1; 
		} 
	else 
		return -1;
	
		
	}
}
