package main_prog;

public class ColorsRGB {
	
	private static int[] R= {200, 90, 90};
	private static int[] G= {80, 100, 80};
	private static int[] B= {80, 80, 100};
	private static int[] Y= {200, 100, 70};
	private static int[] BLACK= {15,15,15};
	
	
	public static String getColor(int r, int g, int b) {
		
		if(r >= R[0] && g <= R[1] && b <= R[2]) {
			return "red";
		}
		else if(r <= G[0] && g >= G[1] && b <= G[2]) {
			return "green";
		}
		else if(r <= B[0] && g <= B[1] && b >= B[2]) {
			return "blue";
		}
		else if(r >= Y[0] && g >= Y[1] && b <= Y[2]) {
			return "yellow";
		}
		else if(r <= BLACK[0] && g <= BLACK[1] && b <= BLACK[2]) {
			return "black";
		}
		return "colore non rilevato";
	}
}
