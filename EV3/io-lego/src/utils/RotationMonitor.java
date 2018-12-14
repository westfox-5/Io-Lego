package utils;

import lejos.hardware.lcd.LCD;
import main_prog.MainProgram.*;

public class RotationMonitor {
	private int angle;
	private boolean set, first=true;

	public RotationMonitor(){
		this.angle=0;
	}
	public synchronized void setAngle(int a) {
		angle = a % 360;
		set=true;
		notify();
	}
	
	public void setFirst() {
		this.first = true;
	}
	
	public synchronized int rotate(Direction dir, Rotations.Left sx, Rotations.Right dx) {
		
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
		switch(dir) {
		case REVERSE : 
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
//		LCD.clear();
//		LCD.drawInt(diff, 0, 4);
		
		if(diff < 0) {			
			// girare destra
			if(first) {
				dx.start();				
				first = false;
			}
		} 
		if(diff > 0) {			
			// girare sinistra
			if(first) {
				sx.start();
				first = false;
			}
			
		}
	
		if( diff == 0) {
			try {			
				LCD.clear();
				sx.interrupt();
				dx.interrupt();
			}catch(Exception e) {
				LCD.clear();
				LCD.drawString("fermata", 0, 4);
				
			}
			
			return 1; 
		} 
	else 
		return -1;
	
		
	}
}
