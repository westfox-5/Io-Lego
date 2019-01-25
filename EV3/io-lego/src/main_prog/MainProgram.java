package main_prog;

import java.io.IOException;

import lejos.hardware.Battery;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import motors.*;
import utils.*;

public class MainProgram {

	public enum Color {
		NOT_FOUND, RED, GREEN, BLUE, YELLOW, BLACK
	}

	enum Print {
		START, BT_CONN, MOVE, CHECK_COL, END
	}

	public enum Direction {
		UP, LEFT, RIGHT, DOWN
	}

	static boolean set;

	private final static int BATTERY_TIMER = 60, ROWS = 5, COLS = 4;
	private final static String END_STRING = "999", LEFT_MOTOR_PORT = "A", RIGHT_MOTOR_PORT = "B", COLOR_PORT = "S4",
			GYRO_PORT = "S3";

	private static Cell[][] map;
	private static EV3ColorSensor COLOR_SENSOR;
	private static EV3GyroSensor GYRO_SENSOR;
	private static RegulatedMotor LEFT_MOTOR, RIGHT_MOTOR;

	private static int robot_x, robot_y;
	private static SampleProvider colorProvider;
	
	private static BluetoothConnector bt;

	private static LegoGraphics g;

	/*---- MOVING FUNCTIONS --------------------------------------*/

//	public static void moveX_D(int distance) {
//		map[robot_x][robot_y].reset();
//
//		rotateTo(Direction.DOWN);
//
//		moveRobot(distance);
//		robot_x+=distance;
//
//		map[robot_x][robot_y].setPosition();
//
//	}
//
//	public static void moveX_Up(int distance) {
//		map[robot_x][robot_y].reset();
//
//		rotateTo(Direction.UP);
//
//		moveRobot(distance);
//		robot_x-=distance;
//		
//		map[robot_x][robot_y].setPosition();
//	
//	}
//
//	public static void moveY_Right(int distance) {
//
//		map[robot_x][robot_y].reset();
//
//		rotateTo(Direction.RIGHT);
//
//		moveRobot(distance);
//		robot_y+=distance;
//
//		map[robot_x][robot_y].setPosition();
//	}
//
//	public static void moveY_Left(int distance) {
//
//		map[robot_x][robot_y].reset();
//
//		rotateTo(Direction.LEFT);
//
//		moveRobot(distance);
//		robot_y-=distance;
//
//		map[robot_x][robot_y].setPosition();
//	}

	public static void moveTo(int fx, int fy) {

		int distance_x = fx-robot_x;
		int distance_y = fy-robot_y;
		
		
		robot_x+=distance_x;
		if(distance_x > 0)
			// riga giu
			rotateTo(Direction.DOWN);
		 else if(distance_x < 0)
			// riga su
			rotateTo(Direction.UP);
		
		moveRobot( Math.abs(distance_x)) ;
		
		
		robot_y+=distance_y;
		if(distance_y > 0) 
			// col destra
			rotateTo(Direction.RIGHT);
		else if(distance_y < 0) 
			// col sinistra
			rotateTo(Direction.LEFT);
		
		moveRobot( Math.abs(distance_y));

	}

	private static void rotateTo(Direction dir) {

		Direction currentDir = getDirection();

		switch (dir) {
		case DOWN:
			if (currentDir != Direction.DOWN) {
				rotate(currentDir);
			}
			break;
		case UP:

			switch (currentDir) {
			case DOWN:
				rotate(Direction.UP);
				break;
			case LEFT:
				rotate(Direction.RIGHT);
				break;
			case RIGHT:
				rotate(Direction.LEFT);
				break;
			case UP:
				break;
			}
			break;
		case RIGHT:
			switch (currentDir) {
			case DOWN:
				rotate(Direction.LEFT);
				break;
			case LEFT:
				rotate(Direction.UP);
				break;
			case UP:
				rotate(Direction.RIGHT);
				break;
			case RIGHT:
				break;
			}
			break;
		case LEFT:
			switch (currentDir) {
			case DOWN:
				rotate(Direction.RIGHT);
				break;
			case LEFT:
				break;
			case UP:
				rotate(Direction.LEFT);
				break;
			case RIGHT:
				rotate(Direction.UP);
				break;
			}
			break;
		}

		try {
			Thread.sleep(1000);
			center();
		}catch(InterruptedException e) {
			
		}
	}

	private static void rotate(Direction dir) {
		Thread t = new Thread(new Rotate(LEFT_MOTOR, RIGHT_MOTOR, dir));
		t.start();

		try {
			t.join();
			Thread.sleep(300);
			
		} catch (Exception e) {

		}
	}

	private static void moveRobot(int cells) {
		float[] colorSample;
		int r, g, b;
		Color currentColor = Color.NOT_FOUND;

		while (cells > 0) {
			Forward forward = new Forward(LEFT_MOTOR, RIGHT_MOTOR);
			Thread f = new Thread(forward, "forward");
			f.start();

			try {
				Thread.sleep(2000);

				do {
					colorSample = new float[colorProvider.sampleSize()];
					colorProvider.fetchSample(colorSample, 0);
					r = (int) (colorSample[0] * 10 * 255);
					g = (int) (colorSample[1] * 10 * 255);
					b = (int) (colorSample[2] * 10 * 255);

					currentColor = ColorsRGB.getColor(r, g, b);
				} while (currentColor != Color.BLACK);

				forward.stop();

				Thread.sleep(500);

			} catch (Exception e) {
				// do nothing
			}

			center();
			cells--;
		}
		
		try {
			reset();
		}catch(Exception e) {
			
		}

	}

	/*---- UTILS FUNCTIONS --------------------------------------*/

	private static Direction getDirection() {
		int range = 15;

		SampleProvider sp = GYRO_SENSOR.getAngleMode();
		float[] sample = new float[sp.sampleSize()];
		sp.fetchSample(sample, 0);
		int angle = ((int) sample[0] + 360) % 360;

		return (angle <= range || angle >= 360 - range) ? Direction.DOWN
				: (angle <= 90 + range && angle >= 90 - range) ? Direction.RIGHT
						: (angle <= 180 + range && angle >= 180 - range) ? Direction.UP
								: (angle <= 270 + range && angle >= 270 - range) ? Direction.LEFT : null;
	}

	public static Color checkColor() {

		float[] colorSample;
		int r, gg, b;
		Color currentColor = Color.NOT_FOUND;
		colorSample = new float[colorProvider.sampleSize()];
		
		try {
			// check the color
			colorProvider.fetchSample(colorSample, 0);
			r = (int) (colorSample[0] * 10 * 255);
			gg = (int) (colorSample[1] * 10 * 255);
			b = (int) (colorSample[2] * 10 * 255);
			currentColor = ColorsRGB.getColor(r, gg, b);

			Thread.sleep(300);
			// always check the centering
			center();

		} catch (Exception e) {}
		return currentColor;
	}

	public static void reset() {
		Reset reset = new Reset(LEFT_MOTOR, RIGHT_MOTOR);
		reset.start();
		try {
			reset.join();
		} catch (Exception e) {}
	}

	public static void center() {
	/*	Thread c = new Thread(new Center(LEFT_MOTOR, RIGHT_MOTOR, GYRO_SENSOR));
		c.start();

		try {
			c.join();
		} catch (Exception e) {}
	*/
		SampleProvider sp = GYRO_SENSOR.getAngleMode();

		int velocity_rot = 8;

		int range = 2;
		int rotRange = 25;
		
		
		LEFT_MOTOR.setSpeed(20);
		RIGHT_MOTOR.setSpeed(20);
		
		boolean corretto = false;
		while(!corretto) {

			float[] sample = new float[sp.sampleSize()];
			sp.fetchSample(sample, 0);
			int angle = ((int) sample[0] + 360) % 360;
	
			if (angle >= 90 - range - rotRange && angle <= 90 -  range
			|| angle >= 180 - range - rotRange && angle <= 180 - range
			|| angle >= 270 - range - rotRange && angle <= 270 - range
			|| angle >= 360 - range - rotRange && angle <= 360 - range) {
				
				// left
				LEFT_MOTOR.startSynchronization();
				LEFT_MOTOR.rotate(-velocity_rot, true);
				RIGHT_MOTOR.rotate(velocity_rot, true);
				LEFT_MOTOR.endSynchronization();
	
			} else if (angle <= 90 +  range + rotRange && angle >= 90 +  range
					|| angle <= 180 + range + rotRange && angle >= 180 + range
					|| angle <= 270 + range + rotRange && angle >= 270 + range
					|| angle <=       range + rotRange && angle >=       range) {
				
				// right
				LEFT_MOTOR.startSynchronization();
				LEFT_MOTOR.rotate(velocity_rot, true);
				RIGHT_MOTOR.rotate(-velocity_rot, true);
				LEFT_MOTOR.endSynchronization();
	
			}
			else {
				corretto= true;
			}
	
			LEFT_MOTOR.waitComplete();
			RIGHT_MOTOR.waitComplete();
	
			LEFT_MOTOR.startSynchronization();
			LEFT_MOTOR.stop(true);
			RIGHT_MOTOR.stop(true);
			LEFT_MOTOR.endSynchronization();
			
			try {
				Thread.sleep(300);
			}catch(InterruptedException e) {}
		}

	}

	/*---- BLUETOOTH FUNCTIONS ----------------------------------*/

	public static void readAndParse(final BluetoothConnector bt) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {

					String message;
					try {
						message = bt.read();
						g.receivedInput();
						
							
						boolean start = false;
						String[] tokens = message.split("&");
						for (String t : tokens) {
							
							if(t.substring(0,1).equals(" ")) {
								continue;
							}
							
							if (t.equals(END_STRING)) {
								//stopSearch();
								continue;
							}
							
							// robot position
							if(t.substring(0,1).equals("R")) {
								robot_x = Integer.parseInt(t.substring(1, 2));
								robot_y = Integer.parseInt(t.substring(2, 3));
								map[robot_x][robot_y].setPosition();
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
							map[xt][yt].setColor(col);
							start=true;
							
							
						}
						if(start) startSearch();
						
					} catch (IOException e) {
						message = null;
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
						double b = Math.round(Battery.getVoltage() * 10) / 10.0; // get battery as #.#
						bt.send(String.valueOf(b).concat("#"));
						Thread.sleep(BATTERY_TIMER * 1000);

					} catch (Exception e) {
						// do nothing
					}
				}
			}
		}).start();
	}

	/*---- SETUP ALL --------------------------------------------*/

	public static void setup() {
		map = new Cell[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			map[i] = new Cell[COLS];
			for (int j = 0; j < COLS; j++) {
				map[i][j] = new Cell(i, j);
			}
		}

		set = false;

		do {
			try {
				GYRO_SENSOR = new EV3GyroSensor(BrickFinder.getDefault().getPort(GYRO_PORT));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				COLOR_SENSOR = new EV3ColorSensor(BrickFinder.getDefault().getPort(COLOR_PORT));
				colorProvider = COLOR_SENSOR.getRGBMode();
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				LEFT_MOTOR = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort(LEFT_MOTOR_PORT));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		set = false;
		do {
			try {
				RIGHT_MOTOR = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort(RIGHT_MOTOR_PORT));
				set = true;
			} catch (Exception e) {

			}
		} while (!set);

		LEFT_MOTOR.synchronizeWith(new RegulatedMotor[] { RIGHT_MOTOR });
	}

	public static void startSearch() {

//	 	start searching
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (map[i][j].hasColor()) {

					g.movingTo(i, j);
					
					moveTo(i, j);

					try {
						Color color_checked = checkColor();
						g.displayColor(color_checked);
						bt.send(new StringBuilder().append(robot_x).append(robot_y)
								.append(map[i][j].isCorrectColor(color_checked) ? 1 : 0).append('&').toString());

						g.drawLogo();
					} catch (IOException e) {
						// do nothing
					}
				}
			}
		}

		g.ending();

	}
	
	/*---- MAIN -------------------------------------------------*/

	public static void main(String[] args) {
		g = new LegoGraphics();

		g.drawLogo();

		g.drawSetup();

		setup();

		g.setupComplete();

		g.btWait();
		bt = new BluetoothConnector();
		g.btConnect();

// 		send battery status periodically
		sendBatteryInfo(bt);

//		start thread for always listening at input from app
		readAndParse(bt);

//		BYE-BYE

		
		
	}

}
