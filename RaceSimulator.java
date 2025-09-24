import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import csci1140.KeyboardReader;

public class RaceSimulator {
    //To-dos
    //Add lapping?
    //Make Season Simulator Code using this as Race Simulator
    public static Car[] simulateRace(int race, Car[] cars, boolean d2) {
        KeyboardReader r = new KeyboardReader();
        InputStream keyboard = System.in;
        FileInputStream fis = null;
        Track track = new Track();
        //Car[] cars = new Car[32];
        //Get track
        try {
            if(d2){
                fis = new FileInputStream("D2_Racetrack_List.txt");
            }
            else{
                fis = new FileInputStream("Racetrack_List.txt");
            }
            //System.setIn(keyboard);
            //r.resetReader();
            int trackSelection = race;//r.readInt("1 - MIA\n2 - MONT\n3 - LIS\n4 - PAR\n5 - BUCH\n6 - FLO\n7 - RIY\n8 - SYD\n9 - TOK\n10 - LV");
            System.setIn(fis);
            r.resetReader();
            String trackName = " ";
            for(int i = 0; i < trackSelection; i++){
                String temp = r.readLine();
                //System.out.println(temp);
                if(temp != null && temp.length() <= 8 && !temp.equals("KC25")){
                    i--;
                }
                else{
                    trackName = temp;
                }
            }
            //System.out.println(trackName);
            String surface = r.readLine();
            //System.out.println(surface);
            track = new Track(trackName, surface, r.readInt(), r.readInt(), r.readInt(), r.readDouble());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        System.setIn(keyboard);
        r.resetReader();
        System.out.println(track);
        //Car t1 = new Car("Ferrari", "Enzo", "Macchio", 9.5, 321, 1.98, 3.7, 2.1, 1.38, 1.13);
        //Car t5 = new Car("Ferrari", "Worse", "Driver", 7.5, 321, 1.98, 3.7, 2.1, 1.38, 1.13);
        //Car t6 = new Car("Alfa Romeo", "Worst", "Driver", 5, 322, 2.08, 3.4, 2.46, 1.23, 0.95);
        // Car t2 = new Car("Alfa Romeo", "Rafiq", "ad-Din", 7.8, 322, 2.08, 3.4, 2.46, 1.23, 0.95);
        // Car t3 = new Car("Alfa Romeo", "Rian", "MacQueen", 9.8, 322, 2.08, 3.4, 2.46, 1.23, 0.95);
        //Car t4 = new Car("Lamborghini", "Juan", "Estevez", 7, 320, 1.88, 0.9, 2.1, 1.13, 0.95);
        //cars = new Car[]{t2, t3};
        // cars = new Car[20];
        // for(int i = 0; i < 20; i++){
        //     cars[i] = new Car((i + 1) / 2.0);
        // }
        
        //The actual race
        int quickSim = KeyboardReader.readInt("Enter 1 to quick sim, 0 if not: ");
        Car leadCar = new Car();
        Car lastLeadCar = new Car();
        for(int l = 1; l <= track.laps; l++){
            System.out.println("Lap " + l + "/" + track.laps);
            if(quickSim != 1){
                System.out.println("Events: ");
            }
            for(int t = 0; t < track.turns.length; t++){
                for(int c = 0; c < cars.length; c++){
                    if(!cars[c].crashed){
                        cars[c].completeStretch(track, track.turns[t], cars, l, quickSim);
                    }
                }
                //System.out.println("Lap " + l + ", Turn " + t);
                if(l == 1 && t == 0){
                    int n = cars.length;
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (cars[j].lapCompletionTime > cars[j + 1].lapCompletionTime) {
                            // Swap cars[j] and cars[j + 1]
                            Car temp = cars[j];
                            cars[j] = cars[j + 1];
                            cars[j + 1] = temp;
                            }
                        }
                    }
                }
                leadCar = cars[0];
                for(int i = 0; i < cars.length; i++){
                    cars[i].setPosition(leadCar);
                }
                Arrays.sort(cars);
                if(cars[0].position > 0){//fixing bug where cars got positive postion
                    leadCar = cars[0];
                    for(int i = 0; i < cars.length; i++){
                        cars[i].setPosition(leadCar);
                    }
                }
                for(int i = 0; i < cars.length; i++){
                    cars[i].place = i + 1;
                }
            }
            //Tracking the fastest laps for world record purposes
            for(Car c : cars){
                if(!c.crashed && c.lapCompletionTime < track.fastestLapTime){
                    track.fastestLapTime = c.lapCompletionTime;
                    track.fastestLapDoer = c;
                }
            }
            //System.out.println("Fastest lap so far: " + track.fastestLapTime);
            if(lastLeadCar != leadCar && quickSim != 1){
                System.out.println("-> " + leadCar + " takes the lead!");
            }
            lastLeadCar = leadCar;
            //Printing and resetting all data after the lap finishes
            if(quickSim != 1){
                System.out.println();
            }
            
            for(int c = 0; c < cars.length; c++){
                int topCars = 10;
                double topSeconds = -20;
                if(quickSim != 1 && (c < topCars/* || cars[c].position > topSeconds*/)){//only print the top topCars cars for ease of viewing or every car under topSeconds
                    if(!cars[c].crashed){
                        if(cars[c].place < 10){
                            System.out.print(" ");//helping all numbers line up
                        }
                        System.out.print(cars[c].place + ":" + cars[c].movement(l) + cars[c] + "(" + cars[c].position + ")(");
                        if(c > 0){
                            System.out.print(Math.round((cars[c].position - cars[c - 1].position) * 100) / 100.0 + ")");
                        }
                        else{
                            System.out.print("0.0)");
                        } 
                        System.out.println(", Lap: " + cars[c].getLapCompletionTime());
                    }
                    else{
                        System.out.println("(Crashed) - " + cars[c]);
                    }
                }
                cars[c].lapCompletionTime = 0;
                cars[c].lastPlace = cars[c].place;
            }
            if(quickSim != 1){
            KeyboardReader.readLine();}
        }
        System.out.println(track);
        System.out.println(cars[0] + " is the winner!");
        System.out.println("Fastest lap: " + track.fastestLapTime + " sec - " + track.fastestLapDoer);
        for(Car c : cars){
            if(!c.crashed){
                System.out.println(c.place + ": " + c + ", Race Time: " + c.getRaceCompletionTime());
            }
            else{
                System.out.println("(Crashed) - " + c);
            }
        }
        return cars;
    }

}
