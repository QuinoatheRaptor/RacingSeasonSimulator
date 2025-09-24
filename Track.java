import csci1140.*;

public class Track{
    String name;
    String surface;
    int traction;
    int length;//length of one lap in km, should be about 6
    int laps;//roughly 50
    //Total length should be about 300km
    int numTurns;
    Turn[] turns;
    double firstStretchDistance;//the distance from the start line to the first turn
    double finalStretchDistance;//the distance from the last turn to the finish line

    double fastestLapTime = 1000;
    Car fastestLapDoer;

    public Track(){

    }
    public Track(String name, String surface, int length, int laps, int numTurns, double fSD){
        this.name = name;
        this.surface = surface;
        if(surface.equals("Asphalt")){
            traction = 1;
        }
        else if(surface.equals("City")){
            traction = 3;
        }
        else if(surface.equals("Night")){
            traction = 5;
        }
        else if(surface.equals("Dirt")){
            traction = 9;
        }
        else if(surface.equals("Desert")){
            traction = 7;
        }
        traction = 7;
        this.length = length;
        this.laps = laps;
        turns = new Turn[numTurns];
        for(int i = 0; i < numTurns; i++){
            turns[i] = new Turn(KeyboardReader.readDouble(), KeyboardReader.readInt(), i + 1);
        }
        this.numTurns = numTurns;
        firstStretchDistance = fSD;
        finalStretchDistance = turns[0].distance - firstStretchDistance;
    }

    public String toString(){
        return name + " - " + surface + ", " + (length * laps) + " km";
    }

    /*.txt file format:
     * Name
     * Surface
     * 1 Lap Length
     * Lap count
     * fsd
     * first side length
     * first turn measure
     * etc...
     */
}