package utils;

import lejos.hardware.lcd.LCD;
import main_prog.MainProgram.*;

public class RotationMonitor {
	private int angle;
	private boolean set;
	private volatile boolean first=true;

	public RotationMonitor(){
		this.angle=0;
	}
	public synchronized void setAngle(int a) {
		angle = a % 360;
		set=true;
		notify();
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
		System.out.println(diff);
		
		if(diff < 0) {			
			// girare destra
			if(first) {
				new Thread(dx, "DX").start();			
				first = false;
			}
		} 
		if(diff > 0) {			
			// girare sinistra
			if(first) {
				new Thread(sx, "SX").start();
				first = false;
			}
			
		}
	
		if( diff == 0) {
			try {			
				sx.stop();
				dx.stop();

				first = true;
			}catch(Exception e) {
				LCD.clear();
				LCD.drawString("Exception rotation monitor", 0, 4);
				
			}
			
			return 1; 
		} 
	else 
		return -1;
	
		
	}
}
