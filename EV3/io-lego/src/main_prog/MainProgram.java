package main_prog;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.MathContext;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import lejos.hardware.Battery;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import utils.*;

public class MainProgram {

	public enum Color {
		NOT_FOUND, RED, GREEN, BLUE, YELLOW, BLACK
	}

	enum Print {
		START, BT_CONN, MOVE, CHECK_COL, END
	}

	public enum Direction {
		REVERSE, LEFT, RIGHT, DOWN
	}

	static boolean set, first;

	private final static int BATTERY_TIMEOUT = 60, ROWS = 5, COLS = 4;
	private final static double DEFAULT_DISTANCE = 0.15;
	private final static String 
		END_STRING = "999",
		COLOR_PORT = "S4", GYRO_PORT = "S3", US_PORT = "S2";
	

	
	
	private static Cell[][] campo;
	private static RotationMonitor rotationMonitor;

	private static EV3ColorSensor colorSensor;
	private static EV3UltrasonicSensor uSensor;
	private static EV3GyroSensor gyroSensor;
	

	private static int x, y;
	private static SampleProvider 
		colorProvider, distanceProvider;

	private static GyroscopeThread gyroThread;
	private static RegulatedMotor A, B;
	
	private static LegoGraphics g;		
	public static void moveTo(int fx, int fy){ 
		int tmp= fx-x;
	  
	  while(tmp != 0){ 
		  LCD.clear(); 
		  campo[x][y].reset(); 
		  if(tmp > 0){
			  LCD.drawString("GIU", 0, 4); 
			  if(checkObstacle()){  
				  x = crossObstacle(Direction.DOWN); 
			  }else{
				  moveRobot(); x++; 
			  } 
		  } else if(tmp < 0){ //DEVO ANDARE SU
			  LCD.drawString("GIRATI", 0, 4); rotate(Direction.REVERSE); 
			  if(checkObstacle()){
				  x= crossObstacle(Direction.REVERSE); 
			  }else{ 
				  moveRobot(); x--; 
			  } 
		  }
		  campo[x][y].setPosition(); 
		  tmp= fx-x; 
	  }
	  
	  tmp=fy-y; 
	  while(tmp != 0){ 
		  LCD.clear(); 
		  campo[x][y].reset(); 
		  if(tmp > 0){ 
			  LCD.drawString("DESTRA", 0, 4); rotate(Direction.RIGHT);
			  if(checkObstacle()){
				  y = crossObstacle(Direction.RIGHT);
			  }else {
				  moveRobot(); y++; 
			  }
		  } else if(tmp < 0){
			  LCD.drawString("SINISTRA", 0, 4); rotate(Direction.LEFT);
			  if(checkObstacle()){
				  y= crossObstacle(Direction.LEFT);
			  }else {
			  moveRobot(); y--;
			  }
		  } 
		  campo[x][y].setPosition(); 
		  tmp= fy-y; 
	  }
			  
  }

	public static Color checkColor() { 

		float[] colorSample;
		int r, g, b;
		Color currentColor = Color.NOT_FOUND;
		colorSample = new float[colorProvider.sampleSize()];

		colorProvider.fetchSample(colorSample, 0);
		r = (int) (colorSample[0] * 10 * 255);
		g = (int) (colorSample[1] * 10 * 255);
		b = (int) (colorSample[2] * 10 * 255);

//		Thread tForward_A = new Thread(AllThreads.A_color_forward);
//		Thread tForward_B = new Thread(AllThreads.B_color_forward);
//
//		tForward_A.start();
//		tForward_B.start();
//		try {
//			tForward_A.join();
//			tForward_B.join();

			while (currentColor == Color.NOT_FOUND) {
				currentColor = ColorsRGB.getColor(r, g, b);
			}

//			Thread tBack_A = new Thread(AllThreads.A_color_backward);
//			Thread tBack_B = new Thread(AllThreads.B_color_backward);
//
//			tBack_A.start();
//			tBack_B.start();
//			try {
//				tBack_A.join();
//				tBack_B.join();
//
//				return currentColor;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
			return null;
	}

	private static boolean checkObstacle() {
		distanceProvider = uSensor.getDistanceMode();

		float[] sample = new float[distanceProvider.sampleSize()];
		while(true) {
			distanceProvider.fetchSample(sample, 0);
	
			LCD.clear();
			LCD.drawString(""+sample[0], 0, 4);
			Delay.msDelay(300);
		}
//		return (sample[0] <= DEFAULT_DISTANCE);
	}

	// ritorna la riga o colonna dove va il robot
	// va nella cella immediatamente successiva all'ostacolo secondo la direzione.
	// se ostacolo si trova sul bordo, va sempre sulla cella sopra o a destra.
	
	private static int crossObstacle(Direction dir){
	  
	  LCD.clear(); LCD.drawString("CROSS", 0, 4);
	  
	  Delay.msDelay(1000);
	  
	  return 0;
	  /*
	  // calcola la cella finale dove arrivare 
	  int final_x = dir==Direction.DOWN ?
	  x+2 : dir==Direction.REVERSE ? x-2 : x; int final_y = dir==Direction.RIGHT ? y+2 :
	  dir==Direction.LEFT ? y-2 : y;
	  
	  // controllo casi di ostacolo sul bordo 
	  if( final_x < 0 ) { final_x = 0;
	  final_y++; } if( final_x > ROWS) { final_x = ROWS-1; final_y++; } if( final_y
	  < 0 ) { final_y = 0; final_x++; } if( final_y > COLS) { final_y = COLS-1;
	  final_x++; }
	  
	  
	  // sposta il robot facendo due step 'ad angolo'
	  
	  switch(dir) { case DOWN: case REVERSE: // se puoi vai a destra 
		  if(y<COLS-1){
	  rotate(Direction.RIGHT); if(checkObstacle()){ y=
	  crossObstacle(Direction.RIGHT); }else{ moveRobot(); y--; } } // altrimenti
	  else{ rotate(Direction.LEFT); if(checkObstacle()){ y=
	  crossObstacle(Direction.LEFT); }else{ moveRobot(); y--; } }
	  
	  break; case RIGHT: case LEFT: // se puoi vai giu 
		if(x<ROWS-1){
	  rotate(Direction.DOWN); if(checkObstacle()){ y=
	  crossObstacle(Direction.DOWN); }else{ moveRobot(); y--; } } // altrimenti vai
	  else{ rotate(Direction.REVERSE); if(checkObstacle()){ y=
	  crossObstacle(Direction.REVERSE); }else{ moveRobot(); y--; } } break; }
	  
	  rotate(dir);
	  
	  // secondo step 
	  if(checkObstacle()){ x = crossObstacle(dir); }else{
	  moveRobot(); x++; }
	  
	  //ora puoi far muovere il robot fino alla cella finale calcolata
	  moveTo(final_x, final_y);
	  
	  // ritornare la x se è andato a right/left // ritornare la y se è andato a
	  return dir==Direction.DOWN||dir==Direction.REVERSE ? final_y: final_x;
	  */
	}
	 

	private static void rotate(Direction dir) {
		gyroThread.reset();
		
		RotationController rotate = new RotationController(rotationMonitor, dir, A, B);
		rotate.start();

		try {
			rotate.join();
			Delay.msDelay(1000);
			return;
			
		} catch (InterruptedException e) {
			LCD.clear();
			LCD.drawString("Excetion rotation", 0, 3);
		}
	}

	private static void moveRobot() {
		float[] colorSample;
		int r, g, b;
		Color currentColor = Color.NOT_FOUND;

		Forward forward = new Forward(A,B);
		Thread f = new Thread(forward, "forward");
		f.start();

		try {
			Thread.sleep(3000);
		
			do {
				colorSample = new float[colorProvider.sampleSize()];
				colorProvider.fetchSample(colorSample, 0);
				r = (int) (colorSample[0] * 10 * 255);
				g = (int) (colorSample[1] * 10 * 255);
				b = (int) (colorSample[2] * 10 * 255);
	
				currentColor = ColorsRGB.getColor(r, g, b);
			} while (currentColor != Color.BLACK);

			forward.stop();
	
			reset();
			
			return;
				
		} catch (Exception e) {
			LCD.clear();
			LCD.drawString("Excetion moving", 0, 3);

		}
	}
	
	public static void reset() {
		Reset reset = new Reset(A, B);
		reset.start();
		try {
			reset.join();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readAndParse(final BluetoothConnector bt) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {

					String message;
					try {
						message = bt.read();
						g.receivedInput();

					} catch (IOException e) {
						message = null;

					}

					if (message == null)
						continue;

					String[] tokens = message.split("&");
					for (String t : tokens) {
						if (t.equals(END_STRING)) {
							// stopSearch();
							continue;
						}

						int xt = Integer.parseInt(t.substring(0, 1));
						int yt = Integer.parseInt(t.substring(1, 2));
						int c = Integer.parseInt(t.substring(2, 3));
						Color col;
						switch (c) {
						case 1:
							col = Color.YELLOW;
							break;
						case 2:
							col = Color.BLUE;
							break;
						case 3:
							col = Color.GREEN;
							break;
						case 4:
							col = Color.RED;
							break;
						default:
							col = null;
							break;
						}
						campo[xt][yt].setColor(col);
					}
				}

			}
		}).start();
	}

	private static void sendBatteryInfo(final BluetoothConnector bt) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						double b = Math.round(Battery.getVoltage()*10) /10.0; // get battery as #.#
						bt.send(String.valueOf(b).concat("#"));
						Thread.sleep(BATTERY_TIMEOUT * 1000);

					} catch (Exception e) {
						// do nothing
					}
				}
			}
		}).start();
	}
	
	public static void setup() {
		campo = new Cell[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			campo[i] = new Cell[COLS];
			for (int j = 0; j < COLS; j++) {
				campo[i][j] = new Cell();
			}
		}

		first = true;
		set = false;

		do {
			try {
				gyroSensor = new EV3GyroSensor(BrickFinder.getDefault().getPort(GYRO_PORT));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				uSensor = new EV3UltrasonicSensor(BrickFinder.getDefault().getPort(US_PORT));
				distanceProvider = uSensor.getDistanceMode();

				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				colorSensor = new EV3ColorSensor(BrickFinder.getDefault().getPort(COLOR_PORT));
				colorProvider = colorSensor.getRGBMode();
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				A = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("A"));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				B = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("B"));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		A.synchronizeWith(new RegulatedMotor[] { B });
		rotationMonitor = new utils.RotationMonitor();
		
		gyroThread = new GyroscopeThread(gyroSensor, rotationMonitor);
		gyroThread.start();
	}


	public static void main(String[] args) {
		g = new LegoGraphics();
		
		g.drawLogo();
		
		g.drawSetup();
		
		setup();	
		
		g.setupComplete();
		
		x = 0;
		y = 0;
		campo[x][y].setPosition();
		
		Delay.msDelay(3000);
		
		
//		moveRobot();
//		rotate(Direction.RIGHT);
//		moveRobot();
//		rotate(Direction.REVERSE);
//		moveRobot();
//		rotate(Direction.REVERSE);
//		rotate(Direction.RIGHT);
//		rotate(Direction.LEFT);
//		moveRobot();
//		rotate(Direction.REVERSE);	
		
//		moveTo(1,0);

		g.btWait();
		final BluetoothConnector bt = new BluetoothConnector();
		g.btConnect();
		
// 		send battery status periodically
		sendBatteryInfo(bt);

		try {
			Thread.sleep(10000);
		} catch(Exception e) {
			
		}
		
//		 start thread for always listening at input from app
//		readAndParse(bt);

		System.exit(0);
		/*
		 * 
		 * // start searching
		 * for(int i =0; i<ROWS; i++) { 
		 * for(int j =0; j<COLS; j++) {
		 * 
		 * if(campo[i][j].hasColor()) {
		 * 
		 * moveTo(i,j);
		 * 
		 * // ora bisogna controllare il colore se e giusto o no, 
		 * // si invia il colore all' app android e si andrebbe a controllare l'altro colore
		 * 
		 * 
		 * 
		 * bt.send(new StringBuilder() .append(x) .append(y) .append(
		 * campo[i][j].isCorrectColor( checkColor() ) ? 1 : 0) .append('&') .toString()
		 * ); } } }
		 * 
		 * 
		 */

		/*
		 * try { bt.send("001&"); Delay.msDelay(3000); bt.send("030&");
		 * Delay.msDelay(3000); bt.send("211&"); Delay.msDelay(3000); }
		 * catch(IOException e) { e.printStackTrace(); }
		 */

//	Thread t = new Thread(AllThreads.D_start);
//	t.start();
		// fa girare i motori
		/*
		 * monitor = new RotationMonitor(); Thread giroscopio = new Thread( new
		 * AllThreads.Gyro(monitor) ); giroscopio.start();
		 * 
		 * moveRobot(Directions.LEFT); Delay.msDelay(1000); moveRobot(Directions.UP);
		 * Delay.msDelay(1000); moveRobot(Directions.DOWN);
		 */
		// inizializzazione sensore ultrasuoni
		/*
		 * S2 = distanceProvider=uSensor.getDistanceMode(); float d;
		 */
		// inizializzazione motori
		/*
		 * Thread t1 = new Thread( AllThreads.A_avanza); Thread t2 = new Thread (
		 * AllThreads.B_avanza);
		 * 
		 * t1.start(); t2.start();
		 */
		/*
		 * 
		 * Delay.msDelay(1000);
		 * 
		 * while(run) { float[] sample = new float[distanceProvider.sampleSize()];
		 * distanceProvider.fetchSample(sample, 0);
		 * 
		 * d = sample[0]; System.out.println(d); // serve per far fe Thread
		 * rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) ); mare il robot
		 * ad una certa distanza
		 * 
		 * if( d < DEFAULT_DIS Thread rotateThread=new Thread( new
		 * AllThreads.Rotate(monitor, dir) ); ANCE) { System.out.prin Thread
		 * rotateThread=new Thread( new AllThreads.Rotate(monitor, dir) ); ln("STOP");
		 * 
		 * new Thread( All Thread rotateThread=new Thread( new
		 * AllThreads.Rotate(monitor, dir) ); hreads.A_stop) .start(); new Thread (
		 * AllThreads.B_stop) .start();
		 * 
		 * break; }
		 * 
		 * 
		 * Delay.msDelay(500); }
		 */
		/*
		 * while(letto!='\0') {
		 * 
		 * String token = ""; char c; try { while ( ( c = (char) stream.readByte() ) !=
		 * '&' ) { token += c; } } catch (IOException e) { e.printStackTrace(); }
		 * LCD.clearDisplay();
		 * 
		 * 
		 * int xt=token.charAt(0)-'0'; int yt=token.charAt(1)-'0'; int
		 * color=token.charAt(2)-'0';
		 * 
		 * 
		 * 
		 * for rige for col if (cella i,j getColor
		 * 
		 * move(x,y,xt,yt);
		 * 
		 * }
		 */

		/* CONNESSIONE BLUETOOTH */
		/*
		
		*/

		/*
		 * Thread t = new Thread(AllThreads.A_close_open); t.start();
		 * 
		 * Delay.msDelay(5000);
		 */

		// PROVA COLORE legge i colori se la linea nera

		/*
		 * 
		 * SampleProvider colorProvider; colorProvider=colorSensor.getRGBMode(); float[]
		 * colorSample;
		 * 
		 * colorSample =new float[colorProvider.sampleSize()];
		 * 
		 * int r,g,b;
		 * 
		 * Delay.msDelay(500); LCD.clear();
		 * 
		 * // Si usano quando si usa la funzione checkColor
		 * 
		 * 
		 * Thread tA = new Thread(AllThreads.A_next_cell); Thread tB = new
		 * Thread(AllThreads.B_next_cell);
		 * 
		 * tA.start(); tB.start();
		 * 
		 * while(true) { LCD.clear();
		 * 
		 * colorProvider.fetchSample(colorSample, 0);
		 * 
		 * r = (int)(colorSample[0]*10*255); g = (int)(colorSample[1]*10*255); b =
		 * (int)(colorSample[2]*10*255);
		 * 
		 * LCD.drawString("R "+r+" G "+g+" B "+b, 0, 4);
		 * 
		 * Delay.msDelay(300);
		 * 
		 * 
		 * }
		 */

		/* GIROSCOPIO */
		/*
		 * Port S3 = LocalEV3.get().getPort("S3"); EV3GyroSensor gyroSensor = new
		 * EV3GyroSensor(S3);
		 * 
		 * //Configuration
		 * 
		 * 
		 * SampleProvider sp = gyroSensor.getAngleMode(); int value = 0;
		 * 
		 * 
		 * 
		 * //Control loop while(run) {
		 * 
		 * LCD.clear();
		 * 
		 * float [] sample = new float[sp.sampleSize()]; sp.fetchSample(sample, 0);
		 * value = (int)sample[0];
		 * 
		 * 
		 * 
		 * LCD.drawString("Gyro angle: " + value, 0, 4); Delay.msDelay(500); }
		 * 
		 */

		/* LETTURA CARATTERI DA APP */
		/*
		 * while(run) {
		 * 
		 * String message = ""; char c; try { while ( ( c = (char) stream.readByte() )
		 * != '\0' ) { message += c; } } catch (IOException e) { e.printStackTrace();
		 * run = false; } LCD.clearDisplay(); LCD.drawString(message,0,4);
		 * 
		 * if(message.equals("end")) { run=false; }
		 * 
		 * Delay.msDelay(5000); }
		 */
	}
	
}
