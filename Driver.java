public class Driver {
    //Not the main method, but rather the person driving the car
    String fName;
    String lName;
    double skill;//measured on 0.1 - 10.0 scale
    double defaultSkill;

    public Driver(String f, String l, double s){
        fName = f;
        lName = l;
        skill = s;
        defaultSkill = s;
    }

    public String toString(){
        return fName + " " + lName;
    }
}
