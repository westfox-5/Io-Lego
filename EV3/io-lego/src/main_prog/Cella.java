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

    public void setColor(Colors color){
        this.color=color;
    }

    public void reset(){
        this.isHere = false;
    }

}