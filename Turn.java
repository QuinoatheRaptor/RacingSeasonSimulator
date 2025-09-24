public class Turn {
    int degree;//how sharp the turn is
    double distance;//how many km there are between this turn and the previous
    int turnNumber;

    public Turn(double d, int degree, int turnNumber){
        this.degree = degree;
        distance = d;
        this.turnNumber = turnNumber;
    }

    public String toString(){
        return "turn " + turnNumber;
    }
}
