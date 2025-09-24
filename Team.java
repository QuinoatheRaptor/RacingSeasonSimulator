public class Team {
    String name;
    int topSpeed;//in km/hr; range: 300 - 400
    double acceleration;//in sec to go from 0 - 100; range: 1.5 - 3.0 
    double tires;//measured on 0.1 - 10.0
    double pitCrew;//in sec, how long it takes crew to do stop; range: 1.8-2.5
    double braking;//in sec to go from 100 - 0; range: 1.1 - 1.5
    double aero;

    int points;
    int wins;
    int podiums;

    int pointsD2;
    int winsD2;
    int podiumsD2;

    public Team(String name, int tS, double acc, double tires, double pC, double brake, double aero){
        this.name = name;
        topSpeed = tS;
        acceleration = 100 / acc;//in km/hr/sec
        this.tires = tires;
        pitCrew = pC;
        braking = 100/brake;
        this.aero = aero;
        points = 0;
    }

    public boolean equals(Team other){
        return name.equals(other.name);
    }

    public String toString(){
        return name;
    }
}
