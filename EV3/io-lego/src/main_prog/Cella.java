package main_prog;



public class Cella {

    private ColorsRGB color;
    public boolean isHere;

    public Cella(){
        this.color = null;
        this.isHere = false;
    }

    public void setPosition(){
        this.isHere= true;
    }

    public void setColor(ColorsRGB color){
        this.color=color;
    }

    public void reset(){
        this.isHere = false;
    }

}