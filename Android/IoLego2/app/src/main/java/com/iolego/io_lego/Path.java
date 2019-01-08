package com.iolego.io_lego;

class Path {
    protected static int ROWS, COLS;
    protected static int matrix[][];

    public static boolean isPath() {
        boolean visited[][] = new boolean[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                // if matrix[i][j] is source
                // and it is not visited
                if (matrix[i][j] == 1 && !visited[i][j])

                    if (isPath(i, j, visited))
                        return true;
            }
        }
        return false;
    }


    public static boolean isSafe(int i, int j) {
        return (i >= 0 && i < ROWS && j >= 0 && j < COLS);
    }

    // Returns true if there is a path from a source (a
    // cell with value -1) to a destination (a cell with
    // value >0)
    public static boolean isPath(int i, int j, boolean visited[][]) {

        if (isSafe(i, j) && matrix[i][j] != 0
                && !visited[i][j]) {
            visited[i][j] = true;

            if (matrix[i][j] ==2)
                return true;

            // traverse up
            boolean up = isPath(i - 1, j, visited);

            // if path is found in up direction return true
            if (up)
                return true;

            // traverse left
            boolean left = isPath(i, j - 1, visited);

            // if path is found in left direction return true
            if (left)
                return true;

            //traverse down
            boolean down = isPath(i + 1, j, visited);

            // if path is found in down direction return true
            if (down)
                return true;

            // traverse right
            boolean right = isPath(i, j + 1, visited);

            // if path is found in right direction return true
            if (right)
                return true;
        }
        return false; // no path has been found
    }

    public static void print(){
        System.out.println("------------------------");
        for(int i =0; i<ROWS; i++){
            System.out.print(i+"-->");
            for(int j = 0; j<COLS; j++){
                System.out.print(" "+matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println("------------------------");
    }
}
