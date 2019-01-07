package utils;

import main_prog.MainProgram.Color;

public class ColorsRGB {
	
	private static int[] 
			RED=	{200, 90,  90},
			GREEN= 	{80,  100, 80},
			BLUE= 	{80,  100, 200},
			YELLOW= {200, 100, 70},
			BLACK=  {65,  65,  65};
	
	public static Color getColor(int r, int g, int b) {
		
		if(r >= RED[0] && g <= RED[1] && b <= RED[2]) {
			return Color.RED;
		}
		else if(r <= GREEN[0] && g >= GREEN[1] && b <= GREEN[2]) {
			return Color.GREEN;
		}
		else if(r <= BLUE[0] && g <= BLUE[1] && b >= BLUE[2]) {
			return Color.BLUE;
		}
		else if(r >= YELLOW[0] && g >= YELLOW[1] && b <= YELLOW[2]) {
			return Color.YELLOW;
		}
		else if(r <= BLACK[0] && g <= BLACK[1] && b <= BLACK[2]) {
			return Color.BLACK;
		}
		return Color.NOT_FOUND;
	}
	
	public static String getColorName(Color c) {
		return 
				c == Color.RED ? "red" :
				c == Color.GREEN ? "green" :
				c == Color.BLACK ? "black" :
				c == Color.BLUE ? "blue" :
				c == Color.YELLOW ? "yellow" :
				"not found";
	}
}
