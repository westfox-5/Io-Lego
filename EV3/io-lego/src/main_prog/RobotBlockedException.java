package main_prog;

class RobotBlockedException extends Exception{
    @Override
    public String toString() {
        return "Robot blocked exception";
    }
}