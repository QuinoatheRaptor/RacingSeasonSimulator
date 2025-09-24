import csci1140.*;

public class Car implements Comparable<Car> {
    //Consistent stats
    Team team;
    Driver driver;
    int topSpeed;//in km/hr; range: 300 - 400
    double acceleration;//in sec to go from 0 - 100; range: 1.5 - 3.0 
    double tires;//measured on 0.1 - 10.0
    double pitCrew;//in sec, how long it takes crew to do stop; range: 1.8-2.5
    double braking;//in sec to go from 100 - 0; range: 1.1 - 1.5
    double aero;

    //Race relevant stats
    double currentSpeed;
    double lapCompletionTime;
    double raceCompletionTime;
    double position;//where the car is in relation to the lead car (measured in sec)
    int place;//the car's place in the race(1st, 2nd, 3rd, etc)
    int lastPlace;//the car's place in the last lap (used for printing movement)
    double tread = 10;//as the car rounds turns, the tread wears down (based on tires stat), but teams can take pit stops to change the tires
    boolean crashed = false;
    int crashSeverity;
    boolean needToPit = false;

    //Season relevant stats
    int points;
    int crashes;
    int wins;
    int podiums;

    public Car(){

    }
    public Car(Team team){//testing constructor
        driver = new Driver("Driver", "#" +  + (int)(Math.random() * 100), 5.0);
        topSpeed = 313;
        acceleration = 100 / 2.0;
        tires = 5;
        pitCrew = 2.5;
        currentSpeed = 0;
        braking = 100/1.25;
        aero = 1.1;
        this.team = team;
    }
    // public Car(String team, String fName, String lName, double skill, int tS, double acc, double tires, double pC, double brake, double aero){
    //     this.team = team;
    //     driver = new Driver(fName, lName, skill);
    //     topSpeed = tS;
    //     acceleration = 100 / acc;//in km/hr/sec
    //     this.tires = tires;
    //     pitCrew = pC;
    //     currentSpeed = 0;
    //     braking = 100/brake;
    //     this.aero = aero;
    // }
    public Car(Team team, String fName, String lName, double skill){
        this.team = team;
        driver = new Driver(fName, lName, skill);
        topSpeed = team.topSpeed;
        acceleration = team.acceleration;
        tires = team.tires;
        pitCrew = team.pitCrew;
        braking = team.braking;
        aero = team.aero;
        currentSpeed = 0;
    }

    public void completeStretch(Track track, Turn turn, Car[] cars, int lap, int quickSim){//complete one straightaway between turns
        double time = 0;//measured in seconds
        if(turn.turnNumber == 1 && (/*lap == track.laps - 10 || */needToPit) && lap < track.laps - 2){
            double pitTime = 1.9 + (Math.random() * (11 - pitCrew) * 0.3);
            if(pitTime >= 4.6){//messed up pit stop
                pitTime += Math.random() * 7;
            }
            pitTime = (int)(pitTime * 100) / 100.0;
            if(quickSim != 1){
                System.out.println("-> (Pit) " + this + " (" + pitTime + ")");
            }
            time += 19 + pitTime;
            tread = 10.0;
            needToPit = false;
            //driver.lName += "*";
        }
        double distance = 0.0;
        double distanceToGo = turn.distance;
        if(lap == 1 && turn.turnNumber == 1){
            distanceToGo = track.firstStretchDistance;
        }
        else if(lap == track.laps && turn.turnNumber == track.turns.length){
            distanceToGo = track.finalStretchDistance;
        }
        while(distance <= distanceToGo){//accelerating up to the turn
            if(!shouldBeDecelerating(turn, track.traction, distanceToGo - distance)){
                //System.out.println(driver.lName + " is accelerating, currentSpeed: " + currentSpeed + "Time: " + time);
                currentSpeed += acceleration * 0.01 * Math.random() - 0.01 * (track.traction - tread);
                if(lap > 1 && (place == 1 || cars[place - 2].position - position > 1.0)){//drag (can be avoided by drafting)
                    currentSpeed -= 0.001 * currentSpeed * aero * Math.random();
                }
                if(currentSpeed > topSpeed){
                    currentSpeed = topSpeed;
                }
            }
            else{
                currentSpeed -= braking * 0.01 - 0.05 * (track.traction - tread);
                tread -= 0.0002 - (tires / 1E5);
                //System.out.println(driver.lName + " is decelerating, current speed: " + currentSpeed + " Time: " + time);
            }
            distance += (currentSpeed / 3600.0) * 0.01;
            time += 0.01;
        }
        lapCompletionTime += time;
        raceCompletionTime += time;
        //System.out.println(driver.lName + " current time: " + lapCompletionTime);
        roundTurn(turn, track.traction, cars, lap);
        //Then we check every car ahead of this one to see if it can pass
        for(int i = place - 2; i >= 0 && lap > 1 && !crashed; i--){
            if(lapCompletionTime - position < cars[i].lapCompletionTime - cars[i].position){
                if(attemptToPass(cars[i], cars, lap)){
                    cars[place - 1] = cars[i];
                    cars[i] = this;
                    cars[place - 1].place = place;
                    place = i + 1;
                    if(place == 1){
                        //System.out.println(driver.lName + " takes the lead!");
                    }
                    else{
                        lapCompletionTime = cars[i].lapCompletionTime + 0.1;
                        position = cars[i].position - 0.1;
                        break;//once you get cut off you can't keep passing
                    }
                }
            }
            else{
                break;
            }
        }
        if(tread <= 3.5 && !(position > -30 && lap > track.laps - 10)){
            //If a car is close to first, they avoid pitting
            needToPit = true;
        }
        else if(tread <= 1.0){
            needToPit = true;
        }
    }

    //When a car completels a lap faster than a car in front of it, it catches up to that car. if it catches up enough, it has a chance to pass
    //Passing will be a method where the car behind passes and maintains its momentum or is blocked and doesn't complete the lap as fast as it was supposed to
    //Passes and turns also hold the greatest opportunity for wrecking
    public boolean attemptToPass(Car otherCar, Car[] cars, int lap){
        if(currentSpeed < otherCar.currentSpeed){//can't pass if you're going slower
            return false;
        }
        if(otherCar.team != null && otherCar.team.equals(team)){
            return true;//teammates don't try to block each other
        }
        double thisPassSkill = Math.random() * driver.skill;
        double passSkill = thisPassSkill - Math.random() * otherCar.driver.skill;
        if(currentSpeed - otherCar.currentSpeed > 4){//fast pass
            if(passSkill > 5){//good pass
                //Nothing, you get the pass and are going too fast to cut off
            }
            else if(passSkill > 0){//weak pass
                if(thisPassSkill < 5 && Math.random() * passSkill <= 0.001){
                    crash(cars, lap, 2);
                    otherCar.crash(cars, lap, 2);
                    System.out.println("-> " + this + " collided with " + otherCar + "!");
                }
            }
            else if(thisPassSkill < 5 && passSkill > -5){//weak block
                if(Math.random() * passSkill >= -0.002){
                    crash(cars, lap, 2);
                    otherCar.crash(cars, lap, 2);
                    System.out.println("-> " + this + " collided with " + otherCar + "!");
                }
                else{//This car needs to hit the brakes (but not much)
                    currentSpeed -= Math.random() * 10;
                    tread -= 0.4 - tires/50;
                }
            }
            else{//strong block (cut off)
                if(passSkill < -8){//spins out
                    System.out.print("-> " + this + " was forced off the track by " + otherCar);
                    if(Math.random() < 0.65){
                        crash(cars, lap, 1);
                        System.out.println(" and crashed!");
                    }
                    else{
                        System.out.println(" but managed to avoid crashing!");
                        double timeAddition = 10 + Math.random() * (10 - driver.skill);//better drivers can recover more quickly
                        lapCompletionTime += timeAddition;
                        raceCompletionTime += timeAddition;
                        currentSpeed = Math.random() * 50 + driver.skill * 2;
                    }
                }
                else{//slam brakes
                    currentSpeed = otherCar.currentSpeed;
                    currentSpeed -= 5 + Math.random() * 20;
                    tread -= 0.65 - tires/50;
                }
            }
        }
        else{//slow pass
            if(passSkill > 5){//good pass (cut him off)
                if(Math.random() * passSkill > 8){//spins out
                    System.out.print("-> " + otherCar + " was forced off the track by " + this);
                    if(Math.random() < 0.65){
                        otherCar.crash(cars, lap, 1);
                        System.out.println(" and crashed!");
                    }
                    else{
                        System.out.println(" but managed to avoid crashing!");
                        double timeAddition = 10 + Math.random() * (10 - driver.skill);//better drivers can recover more quickly
                        otherCar.lapCompletionTime += timeAddition;
                        otherCar.raceCompletionTime += timeAddition;
                        otherCar.currentSpeed = Math.random() * 50 + driver.skill * 2;
                    }
                }
                else{//slam brakes
                    otherCar.currentSpeed -= 5 + Math.random() * 20;
                    tread -= 0.65 - tires/50;
                }
            }
            else if(passSkill > 0){//weak pass
                //Nothing, you kinda just coast by
            }
            else if(passSkill > -5){//weak block
                if(thisPassSkill < 5 && Math.random() * passSkill >= -0.001){
                    crash(cars, lap, 2);
                    otherCar.crash(cars, lap, 2);
                    System.out.println("-> " + this + " collided with " + otherCar + "!");
                }
                else{//This car needs to hit the brakes (but not much)
                    currentSpeed -= Math.random() * 5;
                    tread -= 0.2 - tires/50;
                }
            }
            else{//strong block
                if(Math.random() * passSkill >= -0.01){
                    crash(cars, lap, 2);
                    otherCar.crash(cars, lap, 2);
                    System.out.println("-> " + this + " collided with " + otherCar + "!");
                }
                else{
                    currentSpeed = otherCar.currentSpeed;
                    currentSpeed -= Math.random() * 20;
                }
            }
        }
        return passSkill > 0;
    }

    public void roundTurn(Turn turn, int traction, Car[] cars, int lap){
        double idealSpeed = turn.degree * 1.3 + driver.skill * 2;//mildly arbitrary conversion
        double turnDegree = turn.degree;//the degree the car turns
        if(currentSpeed > idealSpeed){
            if(Math.random() < 0.5){
                turnDegree += (currentSpeed - idealSpeed) / 2;
            }
            else{
                turnDegree -= (currentSpeed - idealSpeed) / 2;
            }
        }
        turnDegree = adjustDegree(turnDegree, Math.random() * (tread - traction) + 0.5 * (tread - traction), turn);
        turnDegree = adjustDegree(turnDegree, driver.skill + Math.random() * driver.skill, turn);
        //Now we finally round the turn
        if(turnDegree > turn.degree - 1 && turnDegree < turn.degree + 1 && currentSpeed <= idealSpeed + 10 && currentSpeed >= idealSpeed - 10){//a perfect turn
            currentSpeed += 0.2 * acceleration * Math.random();//accelerate out of the turn
            //System.out.println("Perfect turn!");
        }
        else if((turnDegree > turn.degree - 10 && turnDegree < turn.degree + 10) && currentSpeed < idealSpeed){//over or under turned going too slow
            //System.out.println("Hit turn too slow");
        }
        else if((turnDegree > turn.degree - 10 && turnDegree < turn.degree + 10) && currentSpeed < idealSpeed + 30){//over or under turned going too fast
            tread -= 0.65 - tires/50;
            currentSpeed = idealSpeed - (5 + Math.random() * 10);
            //System.out.println("Hit turn too fast");
        }
        else{//over or under turned out of plausible bounds or went way too fast
            if(turnDegree <= turn.degree && currentSpeed < idealSpeed + 35){//turned too hard
                System.out.print("-> " + driver + " spun out at " + turn);
                if(Math.random() + driver.skill * 0.02 < 0.7){
                    crash(cars, lap, 1);
                    System.out.println(" and crashed!");
                }
                else{
                    System.out.println(" but managed to avoid crashing!");
                    double timeAddition = 10 + Math.random() * (10 - driver.skill);//better drivers can recover more quickly
                    lapCompletionTime += timeAddition;
                    raceCompletionTime += timeAddition;
                    currentSpeed = Math.random() * 50 + driver.skill * 2;
                }
            }
            else if(turnDegree <= turn.degree){//turned too hard and too fast
                crash(cars, lap, 3);
                System.out.println("-> " + driver + " flipped out at " + turn + "!");
            }
            else if(turnDegree > turn.degree && currentSpeed < idealSpeed + 35){//didn't turn hard enough
                System.out.print("-> " + driver + " couldn't make " + turn + " and drove off the track");
                if(Math.random() + driver.skill * 0.02 < 0.8){
                    crash(cars, lap, 1);
                    System.out.println(" and is out of the race!");
                }
                else{
                    System.out.println(" but managed to avoid crashing!");
                    double timeAddition = 10 + Math.random() * (10 - driver.skill);//better drivers can recover more quickly
                    lapCompletionTime += timeAddition;
                    raceCompletionTime += timeAddition;
                    currentSpeed = Math.random() * 50 + driver.skill * 2;
                }
            }
            else{//didn't turn hard enough too fast
                crash(cars, lap, 3);
                System.out.println("-> " + driver + " collided into the wall at " + turn + "!");
            }
        }
    }

    private boolean shouldBeDecelerating(Turn turn, int traction, double distance/*the amount of distance left on the straight */){
        double idealSpeed = turn.degree * 1.3 + driver.skill * 2;//should be higher for better driver
        if(currentSpeed < idealSpeed){
            return false;
        }
        double timeToSlowDown = (currentSpeed - idealSpeed) / (braking - (traction - tread) * 5);//in seconds
        //Worse drivers have margin of error that makes them slow down sooner or later
        double marginOfError = (Math.random() * 2 * (10.1 - driver.skill) - (10.1 - driver.skill)) / 8;
        double distanceToSlow = ((currentSpeed + idealSpeed) / 2) * (timeToSlowDown / 3600) + marginOfError + 0.1 + 0.005 * traction;//how much distance it will take the car to slow down
        return distanceToSlow >= distance;
    }
    private double adjustDegree(double turnDegree, double adjustment, Turn turn){
        //System.out.println(driver.lName + " is adjusting degree");
        if(adjustment < 0){
            if(turnDegree < turn.degree){
                return turnDegree + adjustment;
            }
            return turnDegree - adjustment;
        }
        for(int i = 0; i <= adjustment && turnDegree != turn.degree; i++){
            if(turnDegree < turn.degree){
                turnDegree++;
            }
            else{
                turnDegree--;
            }
        }
        return turnDegree;
    }

    private void crash(Car[] cars, int lap, int severity){
        crashed = true;
        crashSeverity = (int)(Math.random() * 6) + (severity - 1) * 6 + 1;
        if(lap == 1){
            int index = 0;
            for(int i = 0; i < cars.length; i++){
                if(cars[i] == this){
                    index = i;
                }
            }
            int end = cars.length - 1;
            while(cars[end].crashed){
                end--;
            }
            cars[index] = cars[end];
            cars[end] = this;
            return;
        }
        for(int i = place - 1; i < cars.length - 1; i++){
            //Car temp = cars[i];
            cars[i] = cars[i + 1];
            cars[i].place = i + 1;
            //System.out.println(cars[i].driver.lName + " replaced " + temp.driver.lName);
        }
        //System.out.println(driver.lName + " replaced " + cars[cars.length - 1]);
        cars[cars.length - 1] = this;
    }

    public void setPosition(Car leadCar){
        if(leadCar == this){
            position = 0;
            return;
        }
        position = (int)((leadCar.raceCompletionTime - raceCompletionTime) * 100) / 100.0;
    }
    public void setPlace(int place){
        this.place = place + 1;
    }
    public int compareTo(Car other){
        if(crashed){
            return 20001;
        }
        if(other.crashed){
            return -20001;
        }
        return (int)((other.position - position) * 100);
    }

    public String getRaceCompletionTime(){
        int intTime = (int)raceCompletionTime;
        String timeString = intTime / 3600 + ":";
        intTime %= 3600;
        if(intTime / 60 < 10){
            timeString += "0";
        }
        timeString += intTime / 60 + ":";
        intTime %= 60;
        if(intTime < 10){
            timeString += "0";
        }
        timeString += intTime;
        return timeString;
    }

    public String getLapCompletionTime(){
        int intTime = (int)lapCompletionTime;
        String timeString = intTime / 60 + ":";
        intTime %= 60;
        if(intTime < 10){
            timeString += "0";
        }
        double seconds = intTime + (((int)((lapCompletionTime - (int)lapCompletionTime) * 100)) / 100.0);
        timeString += seconds;
        return timeString;
    }

    public String movement(int lap){
        String move = " ";
        if(place == lastPlace || lap == 1){
            move = "-";
        }
        else if(place < lastPlace){
            move = "^";
        }
        else{
            move = "v";
        }
        return " " + move + " ";
    }

    public void resetStats(){
        tread = 10;
        needToPit = false;
        currentSpeed = 0;
        raceCompletionTime = 0;
    }

    public String toString(){
        return driver + "(" + team + ")";
    }
}
