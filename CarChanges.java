import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import csci1140.KeyboardReader;

public class CarChanges {
    public static void main(String[] args) {
        InputStream keyboard = System.in;
        KeyboardReader r = new KeyboardReader();
        FileInputStream fis = null;
        Team teams[] = new Team[16];
        try {
            //Make all cars and drivers here
            fis = new FileInputStream("JRCL_Cars.txt");
            System.setIn(fis);
            r.resetReader();
            r.readLine();
            for(int i = 0; i < 16; i++){
                String teamName = r.readLine();
                int topSpeed = r.readInt();
                double acc = r.readDouble();
                double tires = r.readDouble();
                double pitCrew = r.readDouble();
                double brakes = r.readDouble();
                double aero = r.readDouble();
                teams[i] = new Team(teamName, topSpeed, acc, tires, pitCrew, brakes, aero);
                r.readLine();
                r.readLine();
                r.readDouble();
                r.readLine();
                r.readLine();
                r.readDouble();
                r.readLine();
                r.readLine();
                r.readDouble();
                r.readLine();
                r.readLine();
                r.readDouble();
            }

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
        for(int i = 0; i < teams.length; i++){
            System.out.println(teams[i].name);
            carChanges(teams[i]);
        }
    }

    public static void carChanges(Team car){
        KeyboardReader r = new KeyboardReader();
        int[] money = new int[6];
        int i = 0;
        do{
            i = r.readInt("Enter the number of the category: ");
            if(i == 0){
                break;
            } else {
                money[i - 1] = r.readInt("Enter how much money should be added: ");
            }
        }while(i != 0);
        //Speed (0)
        int diceRoll = (int)((Math.random() * 5) + money[0] / 2.0) - 2;
        if(diceRoll > 3){
            diceRoll = 3;
        }
        System.out.println("Speed: " + (car.topSpeed + diceRoll));
        //Acceleration (1)
        if((int)(Math.random() * 2) == 0 || money[1] >= 4){
            if(money[1] >= 4){
                money[1] -= 4;
            }
            System.out.println("Acceleration: " + ((100 / car.acceleration) - ((int)(Math.random() * 10) + 1 + money[1]) / 100.0));
            // System.out.println("Current acceleration: " + (100 / car.acceleration));
            // int diceRoll = (int)(Math.random() * 10) + 1;
            // System.out.println("Dice roll: " + diceRoll);
            // System.out.println("New acceleration: " + ((100 / car.acceleration) - (diceRoll + money[1]) / 100.0));
        }
        else{
            System.out.println("Acceleration: " + ((100 / car.acceleration) + ((int)(Math.random() * 10) + 1 - money[1]) / 100.0));
        }
        //Tires (2)
        if((int)(Math.random() * 2) == 0 || money[2] >= 4){
            if(money[2] >= 4){
                money[2] -= 4;
            }
            System.out.println("Tires: " + (car.tires + ((int)(Math.random() * 20) + 1 + money[2] * 2) / 10.0));
        }
        else{
            System.out.println("Tires: " + (car.tires - ((int)(Math.random() * 20) + 1 - money[2] * 2) / 10.0));
        }
        //Pit Crew (3)
        if((int)(Math.random() * 2) == 0 || money[3] >= 4){
            if(money[3] >= 4){
                money[3] -= 4;
            }
            System.out.println("Pit Crew: " + (car.pitCrew + ((int)(Math.random() * 20) + 1 + money[3] * 2) / 10.0));
        }
        else{
            System.out.println("Pit Crew: " + (car.pitCrew - ((int)(Math.random() * 20) + 1 - money[3] * 2) / 10.0));
        }
        //Braking (4)
        if((int)(Math.random() * 2) == 0 || money[4] >= 4){
            if(money[4] >= 4){
                money[4] -= 4;
            }
            System.out.println("Braking: " + ((100 / car.braking) - ((int)(Math.random() * 10) + 1 + money[4]) / 100.0));
        }
        else{
            System.out.println("Braking: " + ((100 / car.braking) + ((int)(Math.random() * 10) + 1 - money[4]) / 100.0));
        }
        //Aero (5)
        if((int)(Math.random() * 2) == 0 || money[5] >= 4){
            if(money[5] >= 4){
                money[5] -= 4;
            }
            System.out.println("Aero: " + (car.aero - ((int)(Math.random() * 10) + 1 + money[5]) / 100.0));
        }
        else{
            System.out.println("Aero: " + (car.aero + ((int)(Math.random() * 10) + 1 - money[5]) / 100.0));
        }
    }
}
