package main_prog;

import lejos.hardware.lcd.Font;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import main_prog.MainProgram.Color;
import utils.ColorsRGB;

public class LegoGraphics {
	private GraphicsLCD g;
	private int W, H;
	
	public LegoGraphics() {
		this.g= BrickFinder.getDefault().getGraphicsLCD();
		this.W = g.getWidth();
		this.H = g.getHeight();
	}
	
	public void drawLogo() {
		g.clear();
		LCD.clear();
		g.setFont(Font.getDefaultFont());
		g.drawString("IO-LEGO", W/2-45, 2, 0);
		g.drawLine(0, 20, W, 20);
	}
	
	public void drawSetup() {
		Button.LEDPattern(2);

		g.setFont(Font.getDefaultFont());
		g.drawString("Setting up",0,35,0);
		g.drawString("sensors.. ",0,55,0);
		
	}
	
	public void setupComplete() {
		drawLogo();
		
		Button.LEDPattern(1);
		g.setFont(Font.getDefaultFont());
		g.drawString("Sensors enabled",0,50,0);
	}
	
	public void btWait() {
		g.clear();
		
		g.drawString("Waiting for",0,35,0);
		g.drawString("bluetooth  ",0,55,0);
	}
	
	public void btConnect() {
		LCD.clear();
		drawLogo();
		
		g.drawString("Bluetooth ",0,35,0);
		g.drawString("connected!",0,55,0);
	}
	
	public void receivedInput() {
		drawLogo();
		Button.LEDPattern(0);
		g.setFont(Font.getDefaultFont());
		g.drawString("Received     ",0,35,0);
		g.drawString("Instrunctions",0,55,0);
	}
	
	public void movingTo(int x, int y, int rx, int ry) {
		drawLogo();
		g.setFont(Font.getDefaultFont());
		g.drawString("Moving to",0,35,0);
		g.drawString(" "+x+" "+y,0,55,0);
		g.drawString(" "+rx+" "+ry,0,70,0);
	}

	public void displayColor(Color c) {
		drawLogo();
		int r, gg, b;
		String color;
		
		switch(c) {
		case RED:
			r = 255; gg = 0; b = 0 ;
			break;
		case BLUE: r = 255; gg = 0; b = 0 ;
		break;
		case YELLOW: r = 200; gg = 100; b = 70 ;
		break;
		case GREEN: r = 0; gg = 255; b = 0 ;
		break;
		case BLACK:
		case NOT_FOUND: r = 0; gg = 0; b = 0 ;
		break;
		default: break;
		}
		r = 0;
		gg = 0;
		b = 0;
		
		color = ColorsRGB.getColorName(c);
		g.setFont(Font.getDefaultFont());	
		g.drawString("Found color: ", 0, 35, 0);
		g.setColor(r,gg,b);
		g.drawString(color, 0, 55, 0);
		g.setColor(0,0,0);
		
	}

	public void ending() {
		drawLogo();
		
		g.setFont(Font.getDefaultFont());
		g.drawString("  END OF  ",0,35,0);
		g.drawString("THE SEARCH",0,55,0);		
	}
	
}
