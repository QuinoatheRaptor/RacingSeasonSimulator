import java.io.*;
import java.util.Arrays;
import csci1140.KeyboardReader;

public class SeasonSimulator {
    public static void main(String[] args) {
        int raceCount = 20;//Number of races in a season
        KeyboardReader r = new KeyboardReader();
        InputStream keyboard = System.in;
        FileInputStream fis = null;
        Team[] teams = new Team[16];
        Car[] cars = new Car[32];
        Car[] d2Cars = new Car[32];
        try {
            //Make all cars and drivers here
            fis = new FileInputStream("JRCL_Cars.txt");
            System.setIn(fis);
            r.resetReader();
            r.readLine();
            for(int i = 0; i < 32; i++){
                int j = i;
                String teamName = r.readLine();
                int topSpeed = r.readInt();
                double acc = r.readDouble();
                double tires = r.readDouble();
                double pitCrew = r.readDouble();
                double brakes = r.readDouble();
                double aero = r.readDouble();
                teams[i / 2] = new Team(teamName, topSpeed, acc, tires, pitCrew, brakes, aero);
                //The team's d1 racers
                cars[i++] = new Car(teams[i / 2], r.readLine(), r.readLine(), r.readDouble());
                cars[i] = new Car(teams[i / 2], r.readLine(), r.readLine(), r.readDouble());
                //The team's d2 racers
                d2Cars[j++] = new Car(teams[i / 2], r.readLine(), r.readLine(), r.readDouble());
                d2Cars[j] = new Car(teams[i / 2], r.readLine(), r.readLine(), r.readDouble());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        // for(Car c : d2Cars){
        //     System.out.println(c);
        // }
        System.setIn(keyboard);
        r.resetReader();
        Car[] results = new Car[0];
        Car[] d2FinalResults = new Car[0];
        for(int race = 1; race <= raceCount; race++){//the loop to run every race
            //Do the race
            boolean d2 = race % 2 == 1;
            if(d2){
                results = RaceSimulator.simulateRace((race + 1) / 2, d2Cars, d2);
            }
            else{
                results = RaceSimulator.simulateRace(race / 2, cars, d2);
            }
            r.readLine();
            //Update points and do crash stuff
            for(int i = 0; i < results.length; i++){
                results[i].driver.skill = results[i].driver.defaultSkill;
                int place = i + 1;
                if(place == 1){
                    results[i].points += 45;
                    results[i].wins++;
                    results[i].podiums++;
                    if(!d2){
                        results[i].team.points += 45;
                        results[i].team.wins++;
                        results[i].team.podiums++;
                    }
                    else{
                        results[i].team.pointsD2 += 45;
                        results[i].team.winsD2++;
                        results[i].team.podiumsD2++;
                    }
                }
                else if(place == 2){
                    results[i].points += 36;
                    results[i].podiums++;
                    if(!d2){
                        results[i].team.points += 36;
                        results[i].team.podiums++;
                    }
                    else{
                        results[i].team.pointsD2 += 36;
                        results[i].team.podiumsD2++;
                    }
                }
                else if(place == 3){
                    results[i].points += 31;
                    results[i].podiums++;
                    if(!d2){
                        results[i].team.points += 31;
                        results[i].team.podiums++;
                    }
                    else{
                        results[i].team.pointsD2 += 31;
                        results[i].team.podiumsD2++;
                    }
                }
                else if(place >= 4 && place <= 7){
                    results[i].points += 27 - 3 * (place - 4);
                    if(!d2){
                        results[i].team.points += 27 - 3 * (place - 4);
                    }
                    else{
                        results[i].team.pointsD2 += 27 - 3 * (place - 4);
                    }
                }
                else if(place >= 8 && place <= 11){
                    results[i].points += 16 - 2 * (place - 8);
                    if(!d2){
                        results[i].team.points += 16 - 2 * (place - 8);
                    }
                    else{
                        results[i].team.pointsD2 += 16 - 2 * (place - 8);
                    }
                }
                else if(place >= 12 && place <= 19){
                    results[i].points += 9 - (place - 12);
                    if(!d2){
                        results[i].team.points += 9 - (place - 12);
                    }
                    else{
                        results[i].team.pointsD2 += 9 - (place - 12);
                    }
                }
                else if(place == 20){
                    results[i].points += 2;
                    if(!d2){
                        results[i].team.points += 2;
                    }
                    else{
                        results[i].team.pointsD2 += 2;
                    }
                }
                else if(!results[i].crashed){
                    results[i].points += 1;
                    if(!d2){
                        results[i].team.points += 1;
                    }
                    else{
                        results[i].team.pointsD2 += 1;
                    }
                }
                else{
                    //Update crash record
                    results[i].driver.skill -= results[i].crashSeverity / 4.0;
                    if(results[i].driver.skill <= 0){
                        results[i].driver.skill = 0.1;
                    }
                    //System.out.println(results[i].driver + " crashed with a severity of " + results[i].crashSeverity + " and now has an updated skill of " + results[i].driver.skill);
                    results[i].crashes++;
                    if(results[i].crashSeverity != 18){
                        results[i].crashed = false;
                        results[i].crashSeverity = 0;
                    }
                    else{
                        //With a really bad crash a driver misses the next race
                        r.readLine(results[i] + " suffered a severe crash and will miss the next race.");
                        results[i].crashSeverity = (int)(Math.random() * 10);
                    }
                }
            }
            //Sort the cars by points
            boolean sorted = false;
            while(!sorted){
                sorted = true;
                for(int i = 0; i + 1 < results.length; i++){
                    if(results[i].points < results[i + 1].points){
                        Car temp = results[i + 1];
                        results[i + 1] = results[i];
                        results[i] = temp;
                        sorted = false;
                    }
                }
            }
            //Print updated standings
            System.out.println("Standings:");
            for(int i = 0; i < results.length; i++){
                System.out.print((i + 1) + ": " + results[i] + " ");
                if(results[i].crashes > 0){
                    System.out.print("(");
                    for(int j = 0; j < results[i].crashes; j++){
                        System.out.print("X");
                    }
                    System.out.print(") ");
                }
                System.out.println("- " + results[i].points + ", W" + results[i].wins + ", P" + results[i].podiums);
            }
            //Sort the teams by points
            sorted = false;
            while(!sorted){
                sorted = true;
                for(int i = 0; i + 1 < teams.length; i++){
                    if(!d2){
                        if(teams[i].points < teams[i + 1].points){
                            Team temp = teams[i + 1];
                            teams[i + 1] = teams[i];
                            teams[i] = temp;
                            sorted = false;
                        }
                    }
                    else{
                        if(teams[i].pointsD2 < teams[i + 1].pointsD2){
                            Team temp = teams[i + 1];
                            teams[i + 1] = teams[i];
                            teams[i] = temp;
                            sorted = false;
                        }
                    }
                }
            }
            r.readLine();
            //Print team standings
            System.out.println("Team Standings:");
            for(int i = 0; i < teams.length; i++){
                System.out.print((i + 1) + ": " + teams[i] + " ");
                if(!d2){
                    System.out.println("- " + teams[i].points + ", W" + teams[i].wins + ", P" + teams[i].podiums);
                }
                else{
                    System.out.println("- " + teams[i].pointsD2 + ", W" + teams[i].winsD2 + ", P" + teams[i].podiumsD2);
                }
            }
            //Reset stats (tread, needToPit, currentSpeed, raceCompletionTime)
            for(int i = 0; i < results.length; i++){
                results[i].resetStats();
            }
            if(race == 19){
                d2FinalResults = results;
            }
            r.readLine();
        }
        try{
            FileWriter myWriter = new FileWriter("FinalStandings.txt", true);
            //Print final standings
            myWriter.append("Final Standings:\n");
            for(int i = 0; i < results.length; i++){
                myWriter.append((i + 1) + ": " + results[i] + " ");
                if(results[i].crashes > 0){
                    myWriter.append("(");
                    for(int j = 0; j < results[i].crashes; j++){
                        myWriter.append("X");
                    }
                    myWriter.append(") ");
                }
                myWriter.append("- " + results[i].points + ", W" + results[i].wins + ", P" + results[i].podiums + "\n");
            }
            myWriter.append("\n");
            //Print team standings
            myWriter.append("Final Team Standings:\n");
            for(int i = 0; i < teams.length; i++){
                myWriter.append((i + 1) + ": " + teams[i] + " - " + teams[i].points + ", W" + teams[i].wins + ", P" + teams[i].podiums + "\n");
            }
            myWriter.append("\n");

            //Print D2 Final Standings
            myWriter.append("D2 Final Standings:\n");
            for(int i = 0; i < d2FinalResults.length; i++){
                myWriter.append((i + 1) + ": " + d2FinalResults[i] + " ");
                if(d2FinalResults[i].crashes > 0){
                    myWriter.append("(");
                    for(int j = 0; j < d2FinalResults[i].crashes; j++){
                        myWriter.append("X");
                    }
                    myWriter.append(") ");
                }
                myWriter.append("- " + d2FinalResults[i].points + ", W" + d2FinalResults[i].wins + ", P" + d2FinalResults[i].podiums + "\n");
            }
            myWriter.append("\n");
            //Sort teams by D2 points
            boolean sorted = false;
            while(!sorted){
                sorted = true;
                for(int i = 0; i + 1 < teams.length; i++){
                    if(teams[i].pointsD2 < teams[i + 1].pointsD2){
                        Team temp = teams[i + 1];
                        teams[i + 1] = teams[i];
                        teams[i] = temp;
                        sorted = false;
                    }
                }
            }
            //Print team standings
            myWriter.append("D2 Final Team Standings:\n");
            for(int i = 0; i < teams.length; i++){
                myWriter.append((i + 1) + ": " + teams[i] + " - " + teams[i].pointsD2 + ", W" + teams[i].winsD2 + ", P" + teams[i].podiumsD2 + "\n");
            }
            myWriter.append("\n");
            myWriter.close();
        }
        catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

}
