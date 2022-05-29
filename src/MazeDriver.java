public class MazeDriver {
    public static void main(String[] args) {
        String[] mazeStrings = new String[]
        {"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
         "XXXXXXXXXXXX                XX",
         "XXXX         XXXXXXXXXXXX XXXX",
         "XXXX XXXXX XXXXXXX         XXX",
         "XXXX XXXXX XXXXXXXXXXXXXXX XXX",
         "XXXX       XXXXXXXXXXXXXXXXXXX",
         "XXXX XXXXX               XXXXX",
         "     XXXXX XXXXXXXXXXXXX XXXXX",
         "X XXXXXXXX XXXXXXXXXXXXX XXXXX",
         "X XXXXXXXXXXXXX      XXX   XXX",
         "X   XXXXXXXXXXXXX    XXXXX XXX",
         "XXX XXXXXXXXXXXXX    XXXXX XXX",
         "XXX          XXXXX      XXXXXXX",
         "XXX XXXXXXXX XXXXXXXXX XXXXXXX",
         "XXX XXXXXXXX XXXXXXXXX XXXXXXX",
         "XXX     XXXX XXXXXXXXX        ",
         "XXX XXX XXXX XXXXXXXXX XXXXXXX",
         "XXX     XXXX XXXXXXXXX XXXXXXX",
         "XXXXXXXXXXXX           XXXXXXX",
         "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"};

        Maze maze = new Maze(mazeStrings);
        maze.search(7, 0, 0.0);
    } // end main
} // end MazeDriver class
