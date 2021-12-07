/*
 * CluePlayer.java
 */

import java.util.*;

/** 
 * This class contains player strategies for the game of Clue. 
 *
 * @author     Ari Wisenburn
 *
 */

public class CluePlayer {

    /**
     *  Find a random square on the board for a player to move to.
     *
     *  @return                  The square that the player ends up on  
     */
    public Square findSquareRand() {
        // I'm not used anymore :(
        int row = 0, col = 0;
        boolean valid = false;
        
        while (!valid) {
            col = (int)(Math.random()*(Clue.board.WIDTH)) + 1;
            row = (int)(Math.random()*(Clue.board.HEIGHT)) + 1;
            if (col >= 0 && col < Clue.board.WIDTH && 
                row >= 2 && row < Clue.board.HEIGHT)
                valid = true;
        }  
        return new Square(row, col);
    }

    /**
     *  Find a square on the board for a player to move to by rolling the 
     *  die and chosing a random direction. The square that is chosen must
     *  be legally accessible to the player (i.e., the player must adhere to 
     *  the rules of the game to be there).
     *
     *  @param    c_row          The current row of this player
     *  @param    c_col          The current column of this player
     *
     *  @return                  The square that this player ends up on
     */
    public Square findSquareRandDir(int c_row, int c_col) {

        int roll = roll();

        int dir = (int)(Math.random() * 4);
        int col = c_col;
        int row = c_row;
        int prevCol, prevRow;
        while (roll > 0) {
            // store previous spot
            prevCol = col;
            prevRow = row;
            // move 1 space
            switch(dir){
                case 0: // go up
                    row -= 1;
                    break;
                case 1: // go left
                    col -= 1;
                    break;
                case 2: // go down
                    row += 1;
                    break;
                case 3: // go right
                    col += 1;
                    break;
            }
            // check if the space is valid.
            if (isValid(row, col) && (Clue.board.isDoor(row, col) || Clue.board.getRoom(row, col).equals(" "))) {
                if(Clue.board.isDoor(row, col))
                    return new Square(row, col);
            }
            // if the space is not valid, go back 1 step and choose a different direction
            else{
                dir += 1;
                dir %= 4;
                col = prevCol;
                row = prevRow;
                roll++;
            }
            roll--;
        }
        return new Square(row, col);
    }

    /**
     *  Find a square on the board for a player to move to by rolling the
     *  die and chosing a random direction. The square that is chosen must
     *  be legally accessible to the player (i.e., the player must adhere to
     *  the rules of the game to be there).
     *
     *  @param    c_row          The current row of this player
     *  @param    c_col          The current column of this player
     *  @param    roll           The number of spaces for the player to move
     *
     *  @return                  The square that this player ends up on
     */
    public Square findSquareRandDir(int c_row, int c_col, int roll){
        int dir = (int)(Math.random() * 4);
        int col = c_col;
        int row = c_row;
        int prevCol, prevRow;
        while (roll > 0) {
            // store previous spot
            prevCol = col;
            prevRow = row;
            // move 1 space
            switch(dir){
                case 0: // go up
                    row -= 1;
                    break;
                case 1: // go left
                    col -= 1;
                    break;
                case 2: // go down
                    row += 1;
                    break;
                case 3: // go right
                    col += 1;
                    break;
            }
            // check if the space is valid.
            if (isValid(row, col) && (Clue.board.isDoor(row, col) || Clue.board.getRoom(row, col).equals(" "))) {
                if(Clue.board.isDoor(row, col))
                    return new Square(row, col);
            }
            // if the space is not valid, go back 1 step and choose a different direction
            else{
                dir += 1;
                dir %= 4;
                col = prevCol;
                row = prevRow;
                roll++;
            }
            roll--;
        }
        return new Square(row, col);
    }

    /**
     *  Find a square on the board for a player to move to by rolling the 
     *  die and chosing a good direction. The square that is chosen must
     *  be legally accessible to the player (i.e., the player must adhere to 
     *  the rules of the game to be there).
     *
     *  @param    c_row          The current row of this player
     *  @param    c_col          The current column of this player
     *  @param    notes          The Detective Notes of this player 
     *
     *  @return                  The square that this player ends up on
     */
    public Square findSquareSmart(int c_row, int c_col, DetectiveNotes notes) {
        int roll = roll();
        return findDoor(c_row, c_col, notes, roll);
    }

    /**
     *  Find the nearest door to a valid room (a room that hasn't already been eliminated as a possibility.
     *
     * @param      c_row          The current row of this player
     * @param      c_col          The current column of this player
     * @param      notes          The Detective Notes of this player
     * @param      roll           The roll for this player's turn
     *
     * @return
     */
    public Square findDoor(int c_row, int c_col, DetectiveNotes notes, int roll){
        Square original = new Square(c_row, c_col);
        Square[][] arr = new Square[20][18];
        boolean doorFound = false;
        boolean lastDoor = (notes.getMyRooms().size() == 8) ? true : false;

        Square current = original;
        arr[c_row][c_col] = current; // set home square so the null val doesn't bite us later

        // make a queue for visiting
        ArrayList<Square> queue = new ArrayList<>();
        queue.add(current);

        ArrayList<Integer> visiting = new ArrayList<>(); // Since we can't visiting.contains(Square),
                                                         // each row, col pair is represented by (row * 100) + col
                                                         // so (3, 14) would be 314, (12, 1) would be 1201

        while (!doorFound) {
            // get the new node
            current = queue.remove(0);
            int row = current.getRow();
            int col = current.getColumn();

            visiting.add((row * 100) + col);

            // check if the current node is a door
            if (Clue.board.isDoor(row, col) // if the current node is a door
                    && (!Clue.board.getRoom(row, col).equals(Clue.board.getRoom(c_row, c_col)) || // you can't go into the same room you were in
                    Clue.board.getRoom(c_row, c_col).equals(" ")) && // if you were in the hallway you're fine
                    !notes.getMyRooms().contains(Clue.board.getRoom(row, col))) {
                doorFound = true;
                break;
            }

            // check if this is the last room we haven't eliminated
            if(lastDoor){
                int rollTemp = roll;
                if(roll % 2 == 0) { // if roll is even, you could move back and forth between two spaces right by the door,
                    // so it's "equivalent" to rolling a 2
                    return findSquareRandDir(c_row, c_col, 2);
                }
                return findSquareRandDir(c_row, c_col, 1); // same thing with a 1
            }

            // otherwise, check the surrounding squares
            // check up, if valid add it to queue
            if (isValid(row - 1, col) && !visiting.contains((row - 1) * 100 + col)) {
                Square up = new Square(row - 1, col);
                if (Clue.board.isDoor(row - 1, col) || Clue.board.getRoom(row - 1, col).equals(" ")) {
                    visiting.add((row - 1) * 100 + col);
                    queue.add(up);
                    arr[row - 1][col] = current;
                }
            }
            // check down, if valid add it to queue
            if (isValid(row + 1, col) && !visiting.contains((row + 1) * 100 + col)) {
                Square down = new Square(row + 1, col);
                if (Clue.board.isDoor(row + 1, col) || Clue.board.getRoom(row + 1, col).equals(" ")) {
                    visiting.add((row + 1) * 100 + col);
                    queue.add(down);
                    arr[row + 1][col] = current;
                }
            }
            // check left, if valid add it to queue
            if (isValid(row, col - 1) && !visiting.contains((row * 100) + (col - 1))) {
                Square left = new Square(row, col - 1);
                if (Clue.board.isDoor(row, col - 1) || Clue.board.getRoom(row, col - 1).equals(" ")) {
                    visiting.add(row * 100 + (col - 1));
                    queue.add(left);
                    arr[row][col - 1] = current;
                }
            }
            // check right, if valid add it to queue
            if (isValid(row, col + 1) && !visiting.contains((row * 100) + (col + 1))) {
                Square right = new Square(row, col + 1);
                if (Clue.board.isDoor(row, col + 1) || Clue.board.getRoom(row, col + 1).equals(" ")) {
                    visiting.add(row * 100 + (col + 1));
                    queue.add(right);
                    arr[row][col + 1] = current;
                }
            }
        }

        // now we know where the door is
        Square doorSquare = current;
        ArrayList<Square> path = new ArrayList<>();
        while (!(equal(current, original))) {
            path.add(0, arr[current.getRow()][current.getColumn()]);
            current = arr[current.getRow()][current.getColumn()];
        }

        if (path.size() > roll)
            return path.get(roll);
        return doorSquare;
    }

    /**
     *  Checking whether square is within the board. Looks nicer in larger functions
     * @param r     row
     * @param c     col
     * @return      whether or not the square is within the board
     */
    public boolean isValid(int r, int c){
        return (c >= 0 && c < Clue.board.WIDTH &&
                r >= 2 && r < Clue.board.HEIGHT);
    }

    /**
     *  Method to check whether squares are equal
     *
     * @param   s1          Square 1
     * @param   s2          Square 2
     * @return  boolean     Whether the squares are at the same place or not
     */
    public boolean equal(Square s1, Square s2){
        return (s1.getRow() == s2.getRow()) && (s1.getColumn() == s2.getColumn());
    }

    /**
     *  Move to a legal square on the board. If the move lands on a door,
     *  make a suggestion by guessing a random suspect and random weapon.
     *
     *  @param    curr_row        The row of the player before move
     *  @param    curr_column     The column of the player before move
     *  @param    row             Selected row 
     *  @param    column          Selected column 
     *  @param    color           Player color
     *  @param    name            Player name
     *  @param    notes           Player Detective Notes 
     *
     *  @return                   A suggestion -> [name,room,suspect,weapon]
     */
    public String[] moveNaive(int curr_row, int curr_column, 
                         int row, int column, String color, String name, 
                         DetectiveNotes notes) {

	String [] retVal = new String[4];
        String suspect = notes.getRandomSuspect();
	String weapon = notes.getRandomWeapon();
        String room = Clue.board.getRoom(row,column);

        if (Clue.board.isDoor(curr_row,curr_column))
            Clue.board.setColor(curr_row,curr_column,"Gray");
        else 
            Clue.board.setColor(curr_row, curr_column, "None");

        if (Clue.board.isDoor(row,column)) { 
            retVal[0] = name;
            retVal[1] = room;
            retVal[2] = suspect;
            retVal[3] = weapon;

            if (Clue.gui) {
                System.out.print(name+" suggests that the crime was committed");
                System.out.println(" in the " + room + " by " + suspect + " with the " + weapon);
            }
            // I just added the prove function
            prove(retVal, notes, find(name), (find(name)+1)%6);
        }
        else retVal = null;

        Clue.board.setColor(row,column,color);

	return retVal;
    }

    /**
     *  Get the corresponding number representation of a player
     *
     * @param   name    String name of player
     * @return  int     Number that represents that player
     */
    public int find(String name){
        switch(name){
            case("Scarlet"):
                return 0;
            case("Mustard"):
                return 1;
            case("White"):
                return 2;
            case("Peacock"):
                return 3;
            case("Green"):
                return 4;
            case("Plum"):
                return 5;
        }
        return -1;
    }

    /**
     *  Move to a legal square on the board. If the move lands on a door,
     *  make a good suggestion for the suspect and the weapon. A good
     *  suggestion here is one which does not include any suspects or
     *  weapons that are already in the Detective Notes of this player.
     *
     *  @param    curr_row        The row of the player before move
     *  @param    curr_column     The column of the player before move
     *  @param    row             Selected row 
     *  @param    column          Selected column 
     *  @param    color           Player color
     *  @param    name            Player name
     *  @param    notes           Player Detective Notes 
     *
     *  @return                   A suggestion -> [name,room,suspect,weapon]
     */
    public String[] moveSmart(int curr_row, int curr_column, 
                         int row, int column, String color, String name, 
                         DetectiveNotes notes) {

        String [] retVal = new String[4];
        boolean goodGuess = false;

        // suggest someone we haven't already cleared
        String suspect = notes.getRandomSuspect();
        while(!goodGuess){
            if(notes.getMySuspects().contains(suspect))
                suspect = notes.getRandomSuspect();
            else
                goodGuess = true;
        }

        // suggest a weapon we haven't already eliminated
        String weapon = notes.getRandomWeapon();
        goodGuess = false;
        while(!goodGuess){
            if(notes.getMyWeapons().contains(weapon))
                weapon = notes.getRandomWeapon();
            else
                goodGuess = true;
        }

        // suggest the current room
        String room = Clue.board.getRoom(row,column);

        if (Clue.board.isDoor(curr_row,curr_column))
            Clue.board.setColor(curr_row,curr_column,"Gray");
        else
            Clue.board.setColor(curr_row, curr_column, "None");

        if (Clue.board.isDoor(row,column)) {
            retVal[0] = name;
            retVal[1] = room;
            retVal[2] = suspect;
            retVal[3] = weapon;

            if (Clue.gui) {
                System.out.print(name+" suggests that the crime was committed");
                System.out.println(" in the " + room + " by " + suspect +
                        " with the " + weapon);
            }
            prove(retVal, notes, Clue.SCARLET, Clue.MUSTARD);
        }
        else retVal = null;

        Clue.board.setColor(row,column,color);

        return retVal;
    }
    
    /**
     *  Try to prove a suggestion is false by asking the players, in a
     *  round-robin fashion, to show the suggester one of the suggestions if
     *  the player has that suggested card in their hand. The other players 
     *  know that ONE of the suggestions cannot be in the case file, but they 
     *  do not know which one.
     *
     *  @param  suggestion      A suggestion -> [name,room,suspect,weapon]
     *  @param  notes           The Detective Notes of the current player
     *  @param  player          The current player
     *  @param  next            The next player clockwise around the board
     *  
     *  @return                 An accusation, to check if it is a winner
     *
     */
    public ArrayList<String> prove(String[] suggestion, DetectiveNotes notes,
                         int player, int next) {
        String card;
        boolean found = false;
        ArrayList<String> accusation = new ArrayList<String>();

        // Ask the other 5 players to show one of the suggested cards
        int character = next;
        while(!found && character != player) {
            for(int i = 0; i < 3; i++) {
                card = (String) Arrays.asList((Clue.allCards.get(character)).keySet().toArray()).get(i);
                if (card.equals(suggestion[1])) { // room
                    setNotes(notes, suggestion[1], "room");
                    found = true;
                    break;
                } else if (card.equals(suggestion[2])) { // suspect
                    setNotes(notes, suggestion[2], "suspect");
                    found = true;
                    break;
                } else if (card.equals(suggestion[3])) { // weapon
                    setNotes(notes, suggestion[3], "weapon");
                    found = true;
                    break;
                }
            }
            character = (character + 1) % 6; // increment the character, but make sure the number is always 0-5
        }

        // Make an accusation
        if (!found) {
            // Check this player's cards to see if this player has them
            for (int j=0; j<3; j++) {
                card = (String)Arrays.asList(
                      (Clue.allCards.get(Clue.turn)).keySet().toArray()).get(j);

                for (int k=1; k<=3; k++) 
                    if (!found && card.equals(suggestion[k])) {
                        found = true;
                    }
            }
            // If still not found, I do believe I have won the game!
            for (int j=1; j<4; j++)
                if (!found)
                    accusation.add(suggestion[j]);
                else 
                    accusation.add("None");
        }
        return accusation;
    }

    /**
     *  Update this player's detective notes upon learning some information.
     *
     *  @param    notes    The detective notes of this player
     *  @param    card     The card that caused the change
     *  @param    type     The type of the card - suspect, weapon, or room
     *
     */
    public void setNotes(DetectiveNotes notes, String card, String type) {

        if (type.equals("suspect"))
            notes.addSuspect(card);
        else if (type.equals("weapon"))
            notes.addWeapon(card);
        else if (type.equals("room"))
            notes.addRoom(card);
    }

    /**
     * Roll a die and return a random integer 1-6
     *
     * @param     none
     * @return    roll     A random number 1-6
     *
     */
    public int roll(){
        return (int)(Math.random() * 6 + 1);
    }
}
