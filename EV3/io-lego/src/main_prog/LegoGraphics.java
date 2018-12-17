package main_prog;

import lejos.hardware.lcd.Font;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;

public class LegoGraphics {
	private GraphicsLCD g;
	private int W,H;
	
	public LegoGraphics() {
		this.g= BrickFinder.getDefault().getGraphicsLCD();
		this.W = g.getWidth();
		this.H = g.getHeight();
	}
	
	
	public void drawLogo() {
		g.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("IO-LEGO", W/2-45, 2, 0);
		g.drawLine(0, 20, W, 20);
	}
	
	public void drawSetup() {
		Button.LEDPattern(2);

		g.setFont(Font.getDefaultFont());
		g.drawString("Setting up",0,35,0);
		g.drawString("sensors..", 0,55,0);
		
	}
	
	public void setupComplete() {
		drawLogo();
		
		Button.LEDPattern(1);
		g.setFont(Font.getDefaultFont());
		g.drawString("Sensors enabled", 0,50,0);
	}
	
	
	
	public void receivedInput() {
		drawLogo();
		Button.LEDPattern(0);
		g.setFont(Font.getDefaultFont());
		g.drawString("Received     ", 0, 35,0);
		g.drawString("Instrunctions", 0, 55, 0);
	}

}
