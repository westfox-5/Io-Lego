package main_prog;

import main_prog.MainProgram.Color;

public class Cell {

    private Color color;
    public boolean isHere;

    public Cell(){
        this.color = null;
        this.isHere = false;
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

}