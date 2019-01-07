package main_prog;

import main_prog.MainProgram.Color;

public class Cell {

    private Color color;
    public boolean isHere;
    public boolean visited;
    
    public int x, y;
    
    public Cell(int x, int y){
    	this.x = x; this.y = y;
        this.color = null;
        this.isHere = false;
        this.visited = false;
    }

    public void setPosition(){
        this.isHere= true;
    }

    public void setColor(Color c){
        this.color=c;
    }

    public void reset(){
        this.isHere = false;
    }

    public boolean isCorrectColor(Color c) {
    	return this.color==c;
    }
    
    public boolean hasColor() {
        return this.color!=null;
    }
    
    public boolean isVisited() {
    	return this.visited;
    }
    
    public void setVisited(boolean v) {
    	this.visited = v;
    }

}