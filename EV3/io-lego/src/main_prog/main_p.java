package main_prog;

import java.io.BufferedReader;

import java.io.IOException;

import java.util.ArrayList;

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

import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.Move;
import lejos.utility.Delay;

import main_prog.AllThreads;
import main_prog.RobotBlockedException;

public class main_p {

	enum Prints { START, PARSE, BT_CONN, MOVE, CHECK_COL, END }
	enum Directions { UP, DOWN, LEFT, RIGHT	}
	enum Colors { NOT_FOUND, RED, GREEN, BLUE, YELLOW, BLACK }


	private final static int ROWS = 5;
	private final static int COLS = 4;
	private final static double DEFAULT_DISTANCE = 0.1;

	private static Boolean run=true;

	private static BluetoothConnector bt;

	private static Cella[][] campo;
	private static RotationMonitor monitor;
	
	private static Port S2;
	private static Port S4;
	private static EV3ColorSensor colorSensor;
	private static EV3UltrasonicSensor uSensor;
	private static SampleProvider distanceProvider;
	private static char letto;
	private static int x;
	private static int y;

	public static void moveTo(int fx, int fy){
		int tmp= fx-x;
		
		while(tmp != 0){

			campo[x][y].reset();
			if(tmp > 0){ // DEVO ANDARE DOWN
				if(checkObstacle(Directions.DOWN)){
					x = crossObstacle(Directions.DOWN);
				}else{
					moveRobot(Directions.DOWN);
					x++;
				}	
			}
			else if(tmp < 0){ //DEVO ANDARE UP
				if(checkObstacle(Directions.UP)){
					x= crossObstacle(Directions.UP);
				}else{
					moveRobot(Directions.UP);
					x--;	
				}
			}
			campo[x][y].setPosition();
			tmp= fx-x;
		}

		tmp=fy-y;
		while(tmp != 0){

			campo[x][y].reset();
			if(tmp > 0){ // DEVO ANDARE A RIGHT
				if(checkObstacle(Directions.RIGHT)){
					y = crossObstacle(Directions.RIGHT);					
				}else{
					moveRobot(Directions.RIGHT);
					y++;
				}
			}
			else if(tmp < 0){// DEVO ANDARE A LEFT
				if(checkObstacle(Directions.LEFT)){
					y= crossObstacle(Directions.LEFT);
				}else{
					moveRobot(Directions.LEFT);
					y--;	
				}
			}
			campo[x][y].setPosition();
			tmp= fy-y;
		}	
		
	}
	
	public static Colors checkColor() {
		// inizializzo variabili per la porta
		
		SampleProvider colorProvider;
		colorProvider=colorSensor.getRGBMode();
		float[] colorSample;
		int r,g,b;
		Colors currentColor= Colors.NOT_FOUND;
		colorSample =new float[colorProvider.sampleSize()];
			
		colorProvider.fetchSample(colorSample, 0);
		r = (int)(colorSample[0]*10*255);
		g = (int)(colorSample[1]*10*255);
		b = (int)(colorSample[2]*10*255);
		
		Thread tForward_A= new Thread(AllThreads.A_color_forward);
		Thread tForward_B= new Thread(AllThreads.B_color_forward);
		
		tForward_A.start();
		tForward_B.start();
		try {
			tForward_A.join();
			tForward_B.join();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		while(currentColor == Colors.NOT_FOUND) {
			currentColor= ColorsRGB.getColor(r, g, b);
		}
		
		Thread tBack_A= new Thread(AllThreads.A_color_backward);
		Thread tBack_B= new Thread(AllThreads.B_color_backward);
		
		tBack_A.start();
		tBack_B.start();
		try {
			tBack_A.join();
			tBack_B.join();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return currentColor;
	}
	
	private static boolean checkObstacle(Directions dir) {

		Thread rotate= new Thread( new AllThreads.Rotate(monitor, dir) );
		rotate.start();
		
		try {
			// aspetta che finisca la rotazione
			rotate.join(); 
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		distanceProvider=uSensor.getDistanceMode();
		
		float [] sample = new float[distanceProvider.sampleSize()];
		distanceProvider.fetchSample(sample, 0);
		
		if(sample[0]!=defaultDistance) {
			return true;
		}
			
		return false; 
	}
	
	// ritorna il numero di riga o colonna dove è arrivato il robot
	/*
		DOWN 				      UP		   RIGHT		   LEFT	
		|R| |          |1|2|     |*| |        |R|*|           |*|R|
		|*| |          |*|3|     |R| |        | | |		      | | |
		| | |  return->|5|4|
	*/

	// va nella cella immediatamente successiva all'ostacolo secondo la direzione.
	// se ostacolo è sul bordo, va sempre sulla cella sopra o a destra.
	private static int crossObstacle(Directions dir){ 
		
		// calcola la cella finale dove arrivare
		int final_x = dir==Directions.DOWN  ? x+2 : dir==Directions.UP   ? x-2 : x;
		int final_y = dir==Directions.RIGHT ? y+2 : dir==Directions.LEFT ? y-2 : y;

		// controllo casi di ostacolo sul bordo
		if( final_x < 0 ) 	{ final_x = 0;		final_y++; }
		if( final_x > ROWS)	{ final_x = ROWS-1; final_y++; }
		if( final_y < 0 )	{ final_y = 0;		final_x++; }
		if( final_y > COLS) { final_y = COLS-1; final_x++; }


		// sposta il robot facendo due step 'ad angolo'

		switch(dir) {
			case DOWN:
			case UP:
				// se puoi vai a destra
				if(y<COLS-1){
					if(checkObstacle(Directions.RIGHT)){
						y= crossObstacle(Directions.RIGHT);
					}else{
						moveRobot(Directions.RIGHT);
						y--;	
					}
				}
				// altrimenti vai a sinistra
				else{
					if(checkObstacle(Directions.LEFT)){
						y= crossObstacle(Directions.LEFT);
					}else{
						moveRobot(Directions.LEFT);
						y--;	
					}
				}

			break;
			case RIGHT:
			case LEFT:
				// se puoi vai giu
				if(x<ROWS-1){
					if(checkObstacle(Directions.DOWN)){
						y= crossObstacle(Directions.DOWN);
					}else{
						moveRobot(Directions.DOWN);
						y--;	
					}
				}
				// altrimenti vai su
				else{
					if(checkObstacle(Directions.UP)){
						y= crossObstacle(Directions.UP);
					}else{
						moveRobot(Directions.UP);
						y--;	
					}
				}
			break;
		}

		// secondo step
		if(checkObstacle(dir)){
			x = crossObstacle(dir);
		}else{
			moveRobot(dir);
			x++;
		}	

		//ora puoi far muovere il robot fino alla cella finale calcolata
		moveTo(final_x, final_y);

		// ritornare la x se è andato a right/left
		// ritornare la y se è andato a down/up
		return dir==Directions.DOWN||dir==Directions.UP ? final_y: final_x;

	}
	
	private static void moveRobot(Directions dir) {
			
		Thread rotate= new Thread( new AllThreads.Rotate(monitor, dir) );
		Thread nextCellA= new Thread( AllThreads.A_next_cell);
		Thread nextCellB= new Thread( AllThreads.B_next_cell);

		rotate.start();
		
		try {
			// aspetta che finisca la rotazione
			rotate.join(); 
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		//avanza di una cella
		nextCellA.start();
		nextCellB.start();
		
		try {
			// aspetta che finisca il movimento
			nextCellA.join(); 
			nextCellB.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static boolean parseInput() {
		String[] tokens = bt.parseInput();
		if(tokens==null) return false;
		for (String t : tokens) {
			int xt=t.charAt(0)-'0';
			int yt=t.charAt(1)-'0';
			int color=t.charAt(2)-'0';
			Colors col;
			switch(color) {
				case 1:
					col = Colors.YELLOW;
					break;
				case 2:
					col= Colors.BLUE;
					break;
				case 3:
					col= Colors.GREEN;
					break;
				case 4:
					col= Colors.RED;
					break;
				default: 
					col= null;
					break;
			}

			campo[xt][yt].setColor(col);
		}
		return true;
	}

	private static void print(Prints info, int x, int y) {
		switch(info) {
			case START: 
				LCD.clear();
				LCD.drawString("IO - LEGO", 0,1);
			break;
			case BT_CONN: 
				LCD.drawString("BT connected",1,1);
			break;
			case PARSE: 
				LCD.clear(0,4);
				LCD.drawString("Receiving data");
				break;
			case MOVE: 
				LCD.clear(0,4);
				LCD.clear(0,5);
				LCD.drawString("next:",0,4);
				LCD.drawString("X: "+x +" Y: "+y,0,5);
				break;
			case CHECK_COL: 
				LCD.clear(0,4);
				LCD.clear(0,5);
				LCD.drawString("Sending color...",0,4);
				break;
			case END: 
				LCD.clear(0,4);
				LCD.clear(0,5);
				LCD.drawString("Finish!",0,4);
		}
	}


	public static void main(String[] args) {
		campo= new Cella[ROWS][COLS];
		for(int i=0; i<ROWS; i++){
			campo[i]= new Cella[COLS];
			for(int j=0; j<COLS; j++){
				campo[i][j]= new Cella();
			}
		}
		
		x=0;
		y=0;		
		campo[x][y].setPosition();
		
		BluetoothConnector bt = new BluetoothConnector();
		print(Prints.BT_CONN,0,0);
		
		bt.sendBatteryInfo();

		print(Prints.PARSE, 0,0);
		parseInput();

		Colors colorControl;
		for(int i =0; i<ROWS; i++) {
			for(int j =0; j<COLS; j++) {

				if(campo[i][j].hasColor()) {

					print(Prints.MOVE, i, j);
					// spostamento robot fino a quella posizione
					moveTo(i,j);

					// ora bisogna controllare il colore se è giusto o no, 
					// si invia il colore all' app android e si andrebbe a controllare l'altro colore

					print(Prints.CHECK_COL, 0, 0);
					bt.sendColor( checkColor() );
				}
			}
		}

		print(Prints.END, 0,0);
		bt.sendCloseToken();

	

		
		
//	Thread t = new Thread(AllThreads.D_start);
//	t.start();
		// fa girare i motori
		/*
		monitor = new RotationMonitor();
		Thread giroscopio = new Thread( new AllThreads.Gyro(monitor) );
		giroscopio.start();
		
		moveRobot(Directions.LEFT);
		Delay.msDelay(1000);
		moveRobot(Directions.UP);
		Delay.msDelay(1000);
		moveRobot(Directions.DOWN);
		*/
		// inizializzazione sensore ultrasuoni
		/*
		S2  = LocalEV3.get().getPort("S2");
		uSensor = new EV3UltrasonicSensor(S2);
		distanceProvider=uSensor.getDistanceMode();
		float d;
		*/
		// inizializzazione motori
		/*
		Thread t1 = new Thread( AllThreads.A_avanza);
		Thread t2 = new Thread ( AllThreads.B_avanza);
		
		t1.start();
		t2.start();
		*/
	/*	
		
		Delay.msDelay(1000);
		
		while(run) {
			float[] sample = new float[distanceProvider.sampleSize()];
			distanceProvider.fetchSample(sample, 0);

			d = sample[0];
			System.out.println(d);
			// serve per far fe		Thread rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) );
	mare il robot ad una certa distanza
			
			if( d < DEFAULT_DIS		Thread rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) );
	ANCE) {
				System.out.prin		Thread rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) );
	ln("STOP");
				
				new Thread( All		Thread rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) );
	hreads.A_stop) .start();
				new Thread ( AllThreads.B_stop) .start();
				
				break;	
			}
			
		
			Delay.msDelay(500);
		}
	*/
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

*/
		
		/*
		Thread t = new Thread(AllThreads.A_close_open);
		t.start();
		
		Delay.msDelay(5000);
		*/
		
		
		
		//PROVA COLORE legge i colori se la linea � nera
	
		/*
		Port S4=LocalEV3.get().getPort("S4");
		EV3ColorSensor color= new EV3ColorSensor(S4);
		SampleProvider colorProvider;
		colorProvider=color.getRGBMode();
		float[] colorSample;
		
		colorSample =new float[colorProvider.sampleSize()];
		
		int r,g,b;
		
		Delay.msDelay(500);
		LTELEFONO CD.clear();
		
		// Si usano quando si usa la funzione checkColor
		
		S4= LocalEV3.get().getPort("S4");
		colorSensor= new EV3ColorSensor(S4);
	
		Thread tA = new Thread(AllThreads.A_avanza);
		Thread tB = new Thread(AllThreads.B_avanza);

		while(run) {
			LCD.clear();
			
			colorProvider.fetchSample(colorSample, 0);
			
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
			

			(new Thread(AllThreads.A_avanza)).start();
			(new Thread(AllThreads.B_avanza)).start();

			color= checkColor();
			
		
			
		}

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

/* LETTURA CARATTERI DA APP */
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
