package main_prog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lejos.hardware.Battery;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.Move;
import lejos.utility.Delay;

import main_prog.AllThreads;

public class main_p {
	
	enum Directions { UP, DOWN, LEFT, RIGHT	}
	
	enum Colors {
		BLACK (35);
			
		private final int colorCode;
		
		private Colors(int colorCode) {
			this.colorCode = colorCode;
		}
		
		private int getCode() {
			return this.colorCode;
		}
	}
	
	private final static int DIM = 5;
	private final static int SOGLIA_OSTACOLO = 1000;
	private static Boolean run=true;
	private static Cella[][] campo;
	private static RotationMonitor monitor;
	
	private static Port S4;
	private static EV3UltrasonicSensor uSensor;
	private static SampleProvider distance;
	private static int defaultDistance;
	private static char letto;
	private static int x;
	private static int y;
	
	public main_p() {
		defaultDistance=-1;
		x=0;
		y=0;
	}
	

	public static void setInitialPosition(int x, int y){
		campo[x][y].setPosition();
	}

	public static void setColors(int x, int y, Colors colore){
		campo[x][y].setColor(colore);
	}

	public static void move(int ix, int iy, int fx, int fy){
		int tmp= fx-ix;
		
		while(tmp != 0){

			campo[ix][iy].reset();
			if(tmp > 0){ // DEVO ANDARE DOWN
				if(checkObstacle(Directions.DOWN)){
					ix = crossObstacle(ix, iy, Directions.DOWN);
				}else{
					moveRobot(Directions.DOWN);
					ix++;
				}	
			}
			else if(tmp < 0){ //DEVO ANDARE UP
				if(checkObstacle(Directions.UP)){
					ix= crossObstacle(ix, iy, Directions.UP);
				}else{
					moveRobot(Directions.UP);
					ix--;	
				}
			}
			campo[ix][iy].setPosition();
			tmp= fx-ix;
		}

		tmp=fy-iy;

		while(tmp != 0){

			campo[ix][iy].reset();
			if(tmp > 0){ // DEVO ANDARE A RIGHT
				if(checkObstacle(Directions.RIGHT)){
					iy = crossObstacle(ix, iy, Directions.RIGHT);
				}else{
					moveRobot(Directions.RIGHT);
					iy++;
				}	
			}
			else if(tmp < 0){// DEVO ANDARE A LEFT
				if(checkObstacle(Directions.LEFT)){
					iy= crossObstacle(ix, iy, Directions.LEFT);
				}else{
					moveRobot(Directions.LEFT);
					iy--;	
				}
			}
			campo[ix][iy].setPosition();
			tmp= fy-iy;
		}
	}
	
	private static boolean checkObstacle(Directions dir) {
		
		distance=uSensor.getDistanceMode();
		
		float [] sample = new float[distance.sampleSize()];
		distance.fetchSample(sample, 0);
		
		if(sample[0]!=defaultDistance) {
			return true;
		}
			
		return false; }
	
	private static int crossObstacle(int ix, int iy, Directions dir) { return -1;}
	
	private static void moveRobot(Directions dir) {
			
		Thread t1=new Thread( new AllThreads.Rotate(monitor, dir) );
	
		t1.start();
		
		try {
			t1.join(); // aspetta che finisca la rotazione
		}
		catch(InterruptedException e) {
			
		}
		
		//avanza di una cella
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		campo= new Cella[DIM][DIM];
		for(int i=0; i<DIM; i++){
			campo[i]= new Cella[DIM];
			for(int j=0; j<DIM; j++){
				campo[i][j]= new Cella();
			}
		}
		
		new Thread(AllThreads.D_start).start();
		
		monitor = new RotationMonitor();
		Thread giroscopio = new Thread( new AllThreads.Gyro(monitor) );
		giroscopio.start();
		
		moveRobot(Directions.UP);
		Delay.msDelay(2000);
		moveRobot(Directions.DOWN);
		
		S4  = LocalEV3.get().getPort("S4");
		uSensor = new EV3UltrasonicSensor(S4);
		
		
		/*
		while(letto!='\0') {
			
	        String token = "";
			char c;
			try {
				while ( ( c = (char) stream.readByte() ) != '&' ) {
		            token += c;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			LCD.clearDisplay();
		
			
			int xt=token.charAt(0)-'0';
			int yt=token.charAt(1)-'0';
			int color=token.charAt(2)-'0';
			
			
			
			for rige
				for col
					if (cella i,j getColor
			
			move(x,y,xt,yt);
				
		}*/
		
		
		
		
		
		/* CONNESSIONE BLUETOOTH */
/*
		BTConnection setConn;
		BTConnector set=new BTConnector();
		
		setConn=set.waitForConnection(10,NXTConnection.RAW);
		
		InputStream in = setConn.openInputStream();
		DataInputStream stream = new DataInputStream(in);
*/
		
		/*
		Thread t = new Thread(AllThreads.A_close_open);
		t.start();
		
		Delay.msDelay(5000);
		*/
		
		

	/*	PROVA COLORA
		Port S1=LocalEV3.get().getPort("S1");
		EV3ColorSensor color= new EV3ColorSensor(S1);
		SampleProvider colorProvider;
		colorProvider=color.getRGBMode();
		float[] colorSample;
		
		colorSample =new float[colorProvider.sampleSize()];
		
		int r,g,b;
		
		Delay.msDelay(500);
		LCD.clear();
		
		Thread tA = new Thread(AllThreads.A_avanza);
		Thread tB = new Thread(AllThreads.B_avanza);
		
		tA.start();
		tB.start();
		LCD.drawString("Battery: " + Battery.getVoltage(), 0, 0);
		
		while(run) {
			colorProvider.fetchSample(colorSample, 0);

			LCD.clear(4);
			LCD.clear(5);
			LCD.clear(6);
			r = (int)(colorSample[0]*10*255);
			g = (int)(colorSample[1]*10*255);
			b = (int)(colorSample[2]*10*255);
			
			if (r+b+g < Colors.BLACK.getCode()) {
				(new Thread(AllThreads.A_stop)).start();
				(new Thread(AllThreads.B_stop)).start();
				
				Delay.msDelay(2000);
	
				(new Thread(AllThreads.A_avanza)).start();
				(new Thread(AllThreads.B_avanza)).start();

			}
		
			LCD.drawString("R  "+ r, 0, 4);
			LCD.drawString("G  "+ g, 0, 5);
			LCD.drawString("B  "+ b, 0, 6);
					
		
			Delay.msDelay(100);
		
		}
		
		
		Delay.msDelay(5000);
    */
	/*GIROSCOPIO*/
 /*
		Port S3 = LocalEV3.get().getPort("S3");
		EV3GyroSensor gyroSensor = new EV3GyroSensor(S3);

		//Configuration


		SampleProvider sp = gyroSensor.getAngleMode();
		int value = 0;
        
		
		
		//Control loop
        while(run) {
        	
        	LCD.clear();

        	float [] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            value = (int)sample[0];
            

            
			LCD.drawString("Gyro angle: " + value, 0, 4);
			Delay.msDelay(500);
        }

*/
	/*	
		while(run) {
		
	        String message = "";
			char c;
			try {
				while ( ( c = (char) stream.readByte() ) != '\0' ) {
		            message += c;
				}
			} catch (IOException e) {
				e.printStackTrace();
				run = false;
			}
			LCD.clearDisplay();
			LCD.drawString(message,0,4);
			
			if(message.equals("end")) {
				run=false;
			}
			
			Delay.msDelay(5000);
		}
	*/
	}			

}
