# Clue

Download the zip file, extract it, and do the following within that directory

The main program to run the game is in the file Clue.java. You can compile the source code by typing at the command prompt:

`javac *.java`


There are four different ways you can run the Clue program:

`java Clue`

This will run the game with a default delay time of 1 second between players.

`java Clue delay`

This will run the game with a delay time in milliseconds as specified in delay. Note that if you set a long delay time (such as four seconds, i.e., 4000) then you can easily pause the game between plays by clicking on the "Stop" button to examine a move, and then the "Start" button to resume play. This may help you to understand how a strategy is working and confirm that only legal moves are executed.

`java Clue 0`

This will run the game with no delay time, allowing each agent to take a turn by clicking the agent's player button on the GUI. The agent will make a move according to the roll of the die and the strategy for that agent (the die roll for each turn is executed in Clue.java and passed into the player module's strategy method). When the program is run with this option, the agents are responsible for ensuring that they take their turn at the appropriate time by clicking on their button when it is their turn, i.e., the GUI does not enforce clockwise turns around the board -- agents may take a turn whenver they want to. This may be a good way to test Scarlet's strategy without the other agents involved.

`java Clue -ntimes`

This will run the game without the GUI a specified number of times, as given in -ntimes, and print to the screen the number of times that each agent won. In other words, if you run the program as java Clue -100, the game will be played 100 times and the number of times each agent won will be printed to the screen.
