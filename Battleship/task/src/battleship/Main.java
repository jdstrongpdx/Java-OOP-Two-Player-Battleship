package battleship;
import java.util.*;

/** Name: Joel Strong
 *  Date: 7/13/2023
 *  GitHub: jdstrongpdx
 *  Source: Hyperskill Introduction to Java Battleship Project
 *  NOTE TO READER: This is the fourth program I have completed in Java after 10 days of learning (transitioning from
 *  Python).  This is my first program utilizing OOP in Java.  Total time to complete: 8 hours.  There are likely a
 *  number of errors in terms of formatting or best practices, which will improve with experience.
 *  ----------------------------------------------------------------------------------
 *  Represents the board game Battleship.  Two instances of the game are created, and each player places their own
 *  ship locations on the board, and then the opposite player "shoots" at the other players board until all ships
 *  have been sunk.     */
class Battleship {
    final static Scanner scanner = new Scanner(System.in); // Do not change this line
    public char[][] board;
    public Ships[] ships;
    public int hitCount;

    /** Constructor to initialize hitCount, board matrix and ships. */
    public Battleship() {
        this.hitCount = 17;

        /* Generates the board into a char matrix */
        this.board = new char[11][11];
        char letter = '@';
        for (int y = 0; y < 11; y++){
            for (int x = 0; x < 11; x++){
                if (x == 0) {
                    board[x][y] = letter;
                    letter++;
                } else if (y == 0) {
                    board[x][y] = (char) ((char) x + 48);
                } else {
                    board[x][y] = '~';
                }
            }
        }

        /* Generates the ships for the game and stores them in ships list */
        Ships carrier = new Ships("Aircraft Carrier", 5);
        Ships battleship = new Ships("Battleship", 4);
        Ships submarine = new Ships("Submarine", 3);
        Ships cruiser = new Ships("Cruiser", 3);
        Ships destroyer = new Ships("Destroyer", 2);
        this.ships = new Ships[]{carrier, battleship, submarine, cruiser, destroyer};
    }

    /** Represents a Ship in the game Battleship with a name, spaces (length), non-hit spaces (remain), and a list of
     *  coordinates the ship is placed on */
    static class Ships {
        String name;
        int spaces;
        int remain;
        List<String> tiles;

        public Ships(String name, int length) {
            this.name = name;
            this.spaces = length;
            this.remain = length;
            this.tiles = new ArrayList<>();
        }
    }

    /** Main function for running the game */
    public static void main(String[] args) {

        // Init player one game and ship locations on the board
        Battleship playerOne = new Battleship();
        System.out.println("Player 1, place your ships on the game field.");
        playerOne.printBoard(false, playerOne);
        for (Ships ship : playerOne.ships) {
            playerOne.setShips(ship, playerOne);
        }

        changeTurns();

        // Init player two game and ship locations on the board
        Battleship playerTwo = new Battleship();
        System.out.println("Player 2, place your ships on the game field.\n");
        playerTwo.printBoard(false, playerTwo);
        for (Ships ship : playerTwo.ships) {
            playerTwo.setShips(ship, playerTwo);
        }

        String spacer = "---------------------";
        changeTurns();

        // Loop for playing the game until one player has lost all their ships
        do {// playerOne turn
            playerTwo.printBoard(true, playerTwo);
            System.out.println(spacer);
            playerOne.printBoard(false, playerOne);
            System.out.println("\nPlayer 1, it's your turn:\n");
            playerOne.takeShot(playerTwo);

            changeTurns();

            // playerTwo turn
            playerOne.printBoard(true, playerOne);
            System.out.println(spacer);
            playerTwo.printBoard(false, playerTwo);
            System.out.println("\nPlayer 2, it's your turn:\n");
            playerTwo.takeShot(playerOne);

            changeTurns();

        } while (playerOne.hitCount != 0 || playerTwo.hitCount != 0);
    }

    /** Pauses game to allow players to change spaces and removes newlines from prior scanner inputs */
    static void changeTurns() {
        System.out.println("Press Enter and pass the move to another player\n");
        scanner.nextLine();
        scanner.nextLine();
    }

    /** If a ship is hit, iterates through each ship, finds the matching coordinate, and decrements the ships
       remain count, returning 1 if the ship is sunk.    */
     int hitShip(String coord) {
        for (Ships ship : ships) {
            if (ship.tiles.contains(coord)) {
                ship.remain--;
                ship.tiles.remove(coord);
                if (ship.remain == 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /** Gets a coordinate from the user, parses the input and returns an array with [x,y] coordinates */
    static int[] getCoordinate() {
        try {
            String coordinate = scanner.next();
            int y = coordinate.charAt(0) - 64;
            int x = coordinate.charAt(1) - 48;
            try {
                int z = coordinate.charAt(2) - 48;
                x = x * 10 + z;
                } catch (IndexOutOfBoundsException e) {}
            if (x < 0 || x > 10 || y < 0 || y > 10) {
                System.out.println("Error! You entered the wrong coordinates! Try again: ");
                return new int[0];
            } else {
                int[] returnCoord = new int[2];
                returnCoord[0] = y;
                returnCoord[1] = x;
                return returnCoord;
            }
        } catch (ArrayIndexOutOfBoundsException | NegativeArraySizeException e) {return new int[0];}
    }

    /** Gets and validates a coordinate from the player, requesting new coordinate if not valid.  Determines if
    *  the shot was a hit or a miss, if a ship was sunk, or if all ships are sunk and the game is over.  */
     void takeShot(Battleship player) {
        int[] coordinate;
        int returnVal = 0;
        do {
            coordinate = getCoordinate();
            if (coordinate.length != 2) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
            }
        } while (coordinate.length != 2);
        char tile = player.board[coordinate[1]][coordinate[0]];
        if (tile == 'O') {
            player.board[coordinate[1]][coordinate[0]] = 'X';
            String coord = coordinate[1] + ":" + coordinate[0];
            returnVal = player.hitShip(coord);
            player.hitCount--;
        } else if (tile == '~') {
            player.board[coordinate[1]][coordinate[0]] = 'M';
        }
        if (player.hitCount == 0) {
            System.out.println("You sank the last ship. You won. Congratulations!");
            System.exit(0);
        } else if (returnVal == 1) {
            System.out.println("You sank a ship!\n");
        } else if (tile == 'O') {
            System.out.println("You hit a ship!\n");
        } else if (tile == '~') {
            System.out.println("You missed!\n");
        }
    }

    /** Takes in a ship name and number of spaces.  Until a valid entry, the function gets two coordinates for
     *  placement on the board, checks that the coordinates are valid, and displays error messages if invalid.
     *  If coordinates are valid, the ship is placed on the board. */
     void setShips(Ships ship, Battleship player) {
        System.out.println("\nEnter the coordinates of the " + ship.name + " (" + ship.spaces + " cells):");
        int check = -1;
        do {
            int[] val1 = getCoordinate();
            int[] val2 = getCoordinate();
            if (val1.length != 2 || val2.length != 2) {
                check = 4;
            } else {
                check = setPieces(val1, val2, ship, player);
            }
            if (check == 0) {
                player.printBoard(false, player);
                return;
            } else if (check == 1) {
                System.out.println("\nError! You placed the ship too close to another ship.  Try again:");
            } else if (check == 2) {
                System.out.println("\nError! Wrong length of the " + ship.name + ".  Try again:");
            } else if (check == 3) {
                System.out.println("\nError! Wrong ship location!  Try again:");
            } else if (check == 4) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:");
            } else if (check == -1) {
                System.out.println("\nError! Wrong ship location!  Try again:");
            }
        } while (check != 0);
    }

    /** Helper function of setShips - determines if the coordinates are valid for the type of ship,
     *  the placement direction of the ship, and calls setPiecesHelper to place the ship on the board */
     int setPieces(int[] front, int[] back, Ships ship, Battleship player) {
        int xDiff = front[1] - back[1];
        int yDiff = front[0] - back[0];
        int check = -1;
        // check placement tiles are empty and place ship if true
        if (Math.abs(xDiff) == ship.spaces - 1 || Math.abs(yDiff) == ship.spaces - 1) {
            if (xDiff > 0 && yDiff == 0) {
                // BOW FACING LEFT
                check = setPiecesHelper(back, 0, ship, player);
            } else if (xDiff == 0 && yDiff < 0) {
                // BOW FACING UP
                check = setPiecesHelper(front, 1, ship, player);
            } else if (xDiff < 0 && yDiff == 0) {
                // BOW FACING RIGHT
                check = setPiecesHelper(front, 0, ship, player);
            } else if (xDiff == 0 && yDiff > 0) {
                // BOW FACING DOWN
                check = setPiecesHelper(back, 1, ship, player);
                }
            } else {
            return 2;
        }
        return check;
    }

    /** Helper function of setPieces.  For each coordinate where the ship is supposed to be place, helper
     *  coordCheck is used to ensure placement does not touch another ship.  If placements checks are clear,
     *  it places the ships onto board and updates the coordinates in the ships coordinates list.   */
     int setPiecesHelper(int[] start, int direction, Ships ship, Battleship player) {
        int end = 0;
        int axis = 0;
        int check = -1;
        if (direction == 0) {
            end = start[1] + ship.spaces;
            axis = start[0];
            for (int i = start[1]; i < end; i++) {
                check = coordCheck(axis, i, player);
                if (check != 0) {
                    return check;
                }
            }
            for (int i = start[1]; i < end; i++) {
                String coord = i + ":" + axis;
                ship.tiles.add(coord);
                player.board[i][axis] = 'O';
            }
        } else {
            end = start[0] + ship.spaces;
            axis = start[1];
            for (int i = start[0]; i < end; i++) {
                check = coordCheck(i, axis, player);
                if (check != 0) {
                    return check;
                }
            }
            for (int i = start[0]; i < end; i++) {
                String coord = axis + ":" + i;
                ship.tiles.add(coord);
                player.board[axis][i] = 'O';
            }
        }
        return 0;
    }

    /** Checks all the coordinates around a given point to ensure the placement does not border another ship. */
     int coordCheck(int y, int x, Battleship player) {
        int[][] checkArray = {{1, -1}, {1, 0}, {1, 1}, {-1, 0}, {-1, -1}, {-1, 0}, {-1, 1}, {1, 0}};
        int tryX;
        int tryY;
        char tile;
        for (int i = 0; i < 8; i++) {
            int[] value = checkArray[i];
            tryX = x + value[1];
            tryY = y + value[0];
            if (tryX >= 1 && tryX <= 10 && tryY >= 1 && tryY <= 10) {
                tile = player.board[tryX][tryY];
                if (tile == 'O') {
                    return 1;
                } else if (tile != '~') {
                    return 3;
                }
            }
        }
        return 0;
    }

    /** Prints the current state of the game board.  If fog is passed as true, replaces board data with waves (~) */
     void printBoard(boolean fog, Battleship player) {
        for (int y = 0; y < 11; y++){
            for (int x = 0; x < 11; x++){
                if (x == 0 && y == 0) {
                    System.out.print("  ");
                } else if (player.board[x][y] < 25) {
                    System.out.print(player.board[x][y] + " ");
                } else {
                    char b = player.board[x][y];
                    if (b == ':') {
                        System.out.print(10 + " ");
                    } else {
                        if (fog && (b == 'O' || b == 'M' || b == 'X')) {
                            System.out.print('~' + " ");
                        } else {
                            System.out.print(b + " ");
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}
