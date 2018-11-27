package main_prog;

import lejos.hardware.lcd.LCD;
import main_prog.main_p.Directions;

public class RotationMonitor {
	private int angle;
	private boolean set;

	
	RotationMonitor(){
		this.angle=0;
	}
	
	
	public synchronized void setAngle(int a) {
		angle = (a + 360 ) %360;
		set=true;
		notify();
	}
	
	
	public synchronized int gira(Directions dir) {
		Thread[] gira_destra = new Thread[2];
		gira_destra[0] = new Thread(AllThreads.A_avanza);
		gira_destra[1] = new Thread(AllThreads.B_indietro);
		
		Thread[] gira_sinistra = new Thread[2];
		gira_sinistra[0] = new Thread(AllThreads.A_indietro);
		gira_sinistra[1] = new Thread(AllThreads.B_avanza);
		
		while(!set) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		set=false;
		int correctAngle;
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
			correctAngle = 270;
			break;
		default: 
			correctAngle = this.angle;
			break;
		}
		
		int diff = correctAngle-angle;
	
		if(diff < 0) {
			// girare destra
			gira_destra[0].start();
			gira_destra[1].start();
			
		} 
		if(diff > 0) {
			// girare sinistra
			gira_sinistra[0].start();
			gira_sinistra[1].start();
			
		}
	
		if( diff == 0) {
			LCD.clear();
			LCD.drawInt(angle, 0, 4);
			gira_sinistra[0].interrupt();
			gira_sinistra[1].interrupt();
			gira_destra[0].interrupt();
			gira_destra[0].interrupt();
			try {
			gira_sinistra[0].join();
			gira_sinistra[1].join();
			gira_destra[0].join();
			gira_destra[0].join();
			}catch(InterruptedException e) {
				
			}
			
			
			new Thread(AllThreads.A_stop).start();
			new Thread(AllThreads.B_stop).start();
	
			return 1; 
		} 
	else 
		return -1;
	
		
	}
}
