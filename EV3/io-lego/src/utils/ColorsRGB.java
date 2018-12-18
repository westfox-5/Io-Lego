package utils;

import main_prog.MainProgram.Color;

public class ColorsRGB {
	
	private static int[] R=		{200, 90,  90};
	private static int[] G= 	{80,  100, 80};
	private static int[] B= 	{80,  80,  100};
	private static int[] Y= 	{200, 100, 70};
	private static int[] BLACK= {20,  20,  20};
	
	
	public static Color getColor(int r, int g, int b) {
		
		if(r >= R[0] && g <= R[1] && b <= R[2]) {
			return Color.RED;
		}
		else if(r <= G[0] && g >= G[1] && b <= G[2]) {
			return Color.GREEN;
		}
		else if(r <= B[0] && g <= B[1] && b >= B[2]) {
			return Color.BLUE;
		}
		else if(r >= Y[0] && g >= Y[1] && b <= Y[2]) {
			return Color.YELLOW;
		}
		else if(r <= BLACK[0] && g <= BLACK[1] && b <= BLACK[2]) {
			return Color.BLACK;
		}
		return Color.NOT_FOUND;
	}
}