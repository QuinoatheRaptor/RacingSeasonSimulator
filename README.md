# Racing Season Simulator
This program simulates a two-division season of a racing league, keeping track of each racer's stats as well as their team's stats and presenting a winner at the end.

## Features
This program runs a season of a racing league with two divisions of 32 drivers each. There are also 16 teams, with 2 drivers in each division. Each division has 10 races in the season that run alternatingly (i.e. a race from one division is simulated then a race from the other division). After every race each racer and team receives the points they earned based on their finishing position in the race. The current league standings are printed to the terminal after every race. At the end of the season, the final individual and team standings are appended to a file called "FinalStandings.txt".
### Race Features
A race is run on a track which is a collection of straightways of a length measured in km and turns measured in degrees. The code measures how long it takes each car to complete each straightaway based on its speed (which is affected by acceleration, the last turn, etc). The order of drivers is then set based on who completed the lap the fastest. After every lap the current race standings are printed to the terminal.

After every straightaway is a turn. As cars approach a turn they slow down to a turn's "idealSpeed". Better drives have a higher idealSpeed for turns. If a car doesn't slow down enough for a turn they run the risk of crashing.

If a car goes fast enough to pass another car the method pass() is called which gives the car being passed an opportunity to block the car trying to pass.
One stat a car has is tire tread which wears down during the race. Cars with lower tire tread go slower partially due to a need to go around turns slower. When a car's tire tread is low enough they take a pit stop. Teams have a pit stop stat that determines how long a pit stop takes (pit stop times are based off F1 pit stop times). Occasionally cars won't take a pit stop when they should in order to defend a lead or because there isn't enough race left for a pit stop to be worth it.

As mentioned earlier, cars can crash if they round a turn poorly. Crashes can also occur when one car tries to pass another if one car cuts off the other or both cars collide. If a car crashes they're out of the race and the driver's skill takes a hit for the next race. With some crashes the car has the possibility to recover in time to get back in the race.

## Usage

## Input

## Future additions
