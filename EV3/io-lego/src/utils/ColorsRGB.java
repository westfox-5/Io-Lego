package utils;

import main_prog.MainProgram.Color;

public class ColorsRGB {

	public static Color getColor(int r, int g, int b) {

		if(r < 100 && g < 100 && b < 100) {
			return Color.BLACK;
		}

		if(r > 300) {
			if(g > 200) {
				return Color.YELLOW;
			}
			else if(g <= 200) {
				return Color.RED;
			}
		}
		else if(r <= 300) {
			if (b > 170) {
				return Color.BLUE;
			}
		  else if(b <= 170) {
				return Color.GREEN;
			}
		}

		return Color.NOT_FOUND; // non dovrebbe mai succedere
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
