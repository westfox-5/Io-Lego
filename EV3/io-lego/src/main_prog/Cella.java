package main_prog;

import main_prog.main_p.Colors;

public class Cella {

    private Colors color;
    public boolean isHere;

    public Cella(){
        this.color = null;
        this.isHere = false;
    }

    public void setPosition(){
        this.isHere= true;
    }

    public void setColor(Colors c){
        this.color=c;
    }

    public void reset(){
        this.isHere = false;
    }

    public boolean isCorrectColor(Colors c) {
    	return this.color==c;
    }
    
    public boolean hasColor() {
        return this.color!=null;
    }

}