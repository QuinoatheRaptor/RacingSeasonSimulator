# Racing Season Simulator
This program simulates a two-division season of a racing league, keeping track of each racer's stats as well as their team's stats and presenting a winner at the end.

## Features
This program runs a season of a racing league with two divisions of 32 drivers each. There are also 16 teams, with 2 drivers in each division. Each division has 10 races in the season that run alternatingly (i.e. a race from one division is simulated then a race from the other division). After every race each racer and team receives the points they earned based on their finishing position in the race. The current league standings are printed to the terminal after every race. At the end of the season, the final individual and team standings are appended to a file called "FinalStandings.txt".
### Race Features
A race is run on a track which is a collection of straightways of a length measured in km and turns measured in degrees. The code measures how long it takes each car to complete each straightaway based on its speed (which is affected by acceleration, the last turn, etc). The order of drivers is then set based on who completed the lap the fastest. After every lap the current race standings are printed to the terminal.

After every straightaway is a turn. As cars approach a turn they slow down to a turn's "idealSpeed". Better drives have a higher idealSpeed for turns. If a car doesn't slow down enough for a turn they run the risk of crashing.

If a car goes fast enough to pass another car the method pass() is called which gives the car being passed an opportunity to block the car trying to pass.

One stat a car has is tire tread which wears down during the race. Cars with lower tire tread go slower partially due to a need to go around turns slower. When a car's tire tread is low enough they take a pit stop. Teams have a pit stop stat that determines how long a pit stop takes (pit stop times are based off F1 pit stop times). A pit stop adds 19 seconds to a car's time for time spent pulling in plus the length of the pit stop itself. Occasionally cars won't take a pit stop when they should in order to defend a lead or because there isn't enough race left for a pit stop to be worth it.

As mentioned earlier, cars can crash if they round a turn poorly. Crashes can also occur when one car tries to pass another if one car cuts off the other or both cars collide. If a car crashes they're out of the race and the driver's skill takes a hit for the next race. With some crashes the car has the possibility to recover in time to get back in the race. Crashes come in different severities that affect how much of a hit a driver's skill takes for the next race. When the league standings are printed, drivers get an 'X' printed by their name for every crash they've had that season.

## Usage
When you run the code it will immediately jump into the first race. For each race you will be given the option to press 0 to fully simulate the race or 1 to quickly simulate the race. If you quickly simulate the race it will fly through all the laps and the only information you will see about the race is if people crashed and the final standings of the race. If you fully simulate the race you will need to press enter between every lap. Every lap the current race standings will be printed to terminal. Every racer will also have two numbers next to them. These are the gaps (in seconds) between that car and the car in front of it as well as the distance between that car and first place. Racers also have an arrow or "-" next to them showing if their position changed.

If you fully simulate the race, another thing you'll see printed is the list of "Events" of the lap. Lap events are crashes, near crashes, first place lead changes, and pit stops. Pit stops print how long the actual pit stop took.

If you don't what to see every car's position every lap there are two variables called topCars and topSeconds that can be adjusted. The code will only print the top topCars cars in the race or every car who's less than topSeconds behind first place. These can be changed in lines 131-133 of RaceSimulator.java.

## Input

## Future additions
