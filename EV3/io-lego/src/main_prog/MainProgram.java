package main_prog;

import java.io.IOException;

import lejos.hardware.Battery;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import main_prog.AllThreads;

public class MainProgram {

	enum Color 	{ NOT_FOUND, RED, GREEN, BLUE, YELLOW, BLACK }
	enum Print 	{ START, BT_CONN, MOVE, CHECK_COL, END }
	enum Direction { UP, DOWN, LEFT, RIGHT	}


	private final static int	
		BATTERY_TIMEOUT = 60,
		ROWS = 5,
		COLS = 4;
	private final static double DEFAULT_DISTANCE = 0.1;
	private final static String END_STRING = "999";


	private static Cell[][] campo;
	private static RotationMonitor rotationMonitor;
	
	private static EV3ColorSensor 		colorSensor;
	private static EV3UltrasonicSensor 	uSensor;
	private static int x;
	private static int y;

	public static void moveTo(int fx, int fy){
		int tmp= fx-x;
		
		while(tmp != 0){

			campo[x][y].reset();
			if(tmp > 0){ // DEVO ANDARE GIU
				if(checkObstacle(Direction.DOWN)){
					x = crossObstacle(Direction.DOWN);
				}else{
					moveRobot(Direction.DOWN);
					x++;
				}	
			}
			else if(tmp < 0){ //DEVO ANDARE SU
				if(checkObstacle(Direction.UP)){
					x= crossObstacle(Direction.UP);
				}else{
					moveRobot(Direction.UP);
					x--;	
				}
			}
			campo[x][y].setPosition();
			tmp= fx-x;
		}

		tmp=fy-y;
		while(tmp != 0){

			campo[x][y].reset();
			if(tmp > 0){ // DEVO ANDARE A DESTRA
				if(checkObstacle(Direction.RIGHT)){
					y = crossObstacle(Direction.RIGHT);					
				}else{
					moveRobot(Direction.RIGHT);
					y++;
				}
			}
			else if(tmp < 0){// DEVO ANDARE A SINISTRA
				if(checkObstacle(Direction.LEFT)){
					y= crossObstacle(Direction.LEFT);
				}else{
					moveRobot(Direction.LEFT);
					y--;	
				}
			}
			campo[x][y].setPosition();
			tmp= fy-y;
		}	
		
	}
	
	public static Color checkColor() {
		// inizializzo variabili per la porta
		
		SampleProvider colorProvider;
		colorProvider=colorSensor.getRGBMode();
		float[] colorSample;
		int r,g,b;
		Color currentColor= Color.NOT_FOUND;
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
		
		while(currentColor == Color.NOT_FOUND) {
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
	
	private static boolean checkObstacle(Direction dir) {

		Thread rotate= new Thread( new AllThreads.Rotate(rotationMonitor, dir) );
		rotate.start();
		
		try {
			// aspetta che finisca la rotazione
			rotate.join(); 
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		SampleProvider distanceProvider=uSensor.getDistanceMode();
		
		float [] sample = new float[distanceProvider.sampleSize()];
		distanceProvider.fetchSample(sample, 0);
		
		if(sample[0]!=DEFAULT_DISTANCE) {
			return true;
		}

		return false; 
	}
	
	// ritorna la riga o colonna dove va il robot
	// va nella cella immediatamente successiva all'ostacolo secondo la direzione.
	// se ostacolo si trova sul bordo, va sempre sulla cella sopra o a destra.
	private static int crossObstacle(Direction dir){ 
		
		// calcola la cella finale dove arrivare
		int final_x = dir==Direction.DOWN  ? x+2 : dir==Direction.UP   ? x-2 : x;
		int final_y = dir==Direction.RIGHT ? y+2 : dir==Direction.LEFT ? y-2 : y;

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
					if(checkObstacle(Direction.RIGHT)){
						y= crossObstacle(Direction.RIGHT);
					}else{
						moveRobot(Direction.RIGHT);
						y--;	
					}
				}
				// altrimenti vai a sinistra
				else{
					if(checkObstacle(Direction.LEFT)){
						y= crossObstacle(Direction.LEFT);
					}else{
						moveRobot(Direction.LEFT);
						y--;	
					}
				}

			break;
			case RIGHT:
			case LEFT:
				// se puoi vai giu
				if(x<ROWS-1){
					if(checkObstacle(Direction.DOWN)){
						y= crossObstacle(Direction.DOWN);
					}else{
						moveRobot(Direction.DOWN);
						y--;	
					}
				}
				// altrimenti vai su
				else{
					if(checkObstacle(Direction.UP)){
						y= crossObstacle(Direction.UP);
					}else{
						moveRobot(Direction.UP);
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
		return dir==Direction.DOWN||dir==Direction.UP ? final_y: final_x;

	}
	
	private static void moveRobot(Direction dir) {
			
		Thread rotate= new Thread( new AllThreads.Rotate(rotationMonitor, dir) );
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

	public static void readAndParse(final BluetoothConnector bt) {
		
		new Thread ( new Runnable() {
			@Override
			public void run() {
				
				while(true) {

					String message;
					try {
						message = bt.read();
						
					} catch(IOException e) {
						message = null;
						
					}
					
					if(message==null) continue;
				
					String[] tokens = message.split("&");
					for (String t : tokens) {
						if(t.equals(END_STRING)) {
							stopSearch();
							continue;
						}
						
						int xt=	Integer.parseInt( t.substring(0,1) );
						int yt=	Integer.parseInt( t.substring(1,2) );
						int c=	Integer.parseInt( t.substring(2,3) );
						Color col;
						switch(c) {
							case 1:
								col = Color.YELLOW;
								break;
							case 2:
								col= Color.BLUE;
								break;
							case 3:
								col= Color.GREEN;
								break;
							case 4:
								col= Color.RED;
								break;
							default: 
								col= null;
								break;
						}
						campo[xt][yt].setColor(col);
					}
				}
				
			}
		} ).start();
	}

	private static void stopSearch() {
		new Thread(AllThreads.A_stop).start();
		new Thread(AllThreads.B_stop).start();
	}
	
	private static void sendBatteryInfo(final BluetoothConnector bt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						bt.send(String.valueOf((int)Battery.getVoltage())+"#");
						Thread.sleep(BATTERY_TIMEOUT*1000);
						
					} catch(Exception e) {
						
					}
				}
			}
		}).start();
	}
	
	private static void print(Print info, int x, int y) {
		switch(info) {
			case START: 
				LCD.clear();
				LCD.drawString("IO - LEGO", 0,0);
			break;
			case BT_CONN: 
				LCD.drawString("BT connected",0,2);
			break;
			case MOVE: 
				LCD.clear(0,4,20);
				LCD.clear(0,5,20);
				LCD.drawString("next:",0,4);
				LCD.drawString("X: "+x +" Y: "+y,0,5);
				break;
			case CHECK_COL: 
				LCD.clear(0,4,20);
				LCD.clear(0,5,20);
				LCD.drawString("Sending color...",0,4);
				break;
			case END: 
				LCD.clear(0,4,20);
				LCD.clear(0,5,20);
				LCD.drawString("Finish!",0,4);
		}
	}

	public static void main(String[] args) {
		campo= new Cell[ROWS][COLS];
		for(int i=0; i<ROWS; i++){
			campo[i]= new Cell[COLS];
			for(int j=0; j<COLS; j++){
				campo[i][j]= new Cell();
			}
		}
		
		x=0;
		y=0;		
		campo[x][y].setPosition();
/*		
		rotationMonitor = new RotationMonitor();
		new Thread( new AllThreads.Gyro(rotationMonitor) ).start();
		
		uSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
		colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S4"));
*/
		final BluetoothConnector bt = new BluetoothConnector();
		print(Print.BT_CONN,0,0);
		
		
		// send battery status periodically
		sendBatteryInfo(bt);
		
		// start thread for always listening at input from app
		readAndParse(bt);
		
		
		/*
		
		// start searching
		for(int i =0; i<ROWS; i++) {
			for(int j =0; j<COLS; j++) {

				if(campo[i][j].hasColor()) {

					print(Prints.MOVE, i, j);
					// spostamento robot fino a quella posizione
					moveTo(i,j);

					// ora bisogna controllare il colore se è giusto o no, 
					// si invia il colore all' app android e si andrebbe a controllare l'altro colore

					print(Prints.CHECK_COL, 0, 0);
					
					bt.send(new StringBuilder()
									.append(x)
									.append(y)
									.append( campo[i][j].isCorrectColor( checkColor() ) ? 1 : 0)
									.append('&')
									.toString()
								);
				}
			}
		}

		print(Prints.END, 0,0);
		bt.send("999&");
		
		*/
	
		
		
		try {
			bt.send("001&");
			Delay.msDelay(3000);
			bt.send("030&");		
			Delay.msDelay(3000);
			bt.send("211&");		
			Delay.msDelay(3000);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
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
		S2  = 
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
