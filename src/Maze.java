import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Maze {
    public String[] mazeStrings;

    public Maze(String[] mazeStrings) {
        this.mazeStrings = mazeStrings;
    }
    
    public void search(int startR, int startC, double startA) {
        String source;
        try {
            source = Files.readString(Paths.get("maze.fnn"));
        } catch (IOException err) {
            System.out.println("[RUN] Cannot find `maze.fnn'");
            return;
        }
        FVal code = FinnmarkParser.parseExpr(source);
        //System.out.println(code);
        Evaluator evaluator = new Evaluator();
        FVal[] args = new FVal[3];
        args[0] = new FVal_IDX(startR);
        args[1] = new FVal_IDX(startC);
        args[2] = new FVal_NUM(startA);
        FVal maze = fnnMaze();
        HashMap<String,FVal> env = new HashMap<>(evaluator.defaultEnv);
        env.put("maze", maze);
        env.put("start", new FVal_QTD(new FVal_LST(args)));
        evaluator.eval_any(code, env);
    }

    public FVal fnnMaze() {
        // The Java `int' type (and hence the Finnmark `IDX' type) is always 32-bits
        // Because twos-complement is used to store negative numbers, we can super efficiently represent the maze with
        // one bit per cell, wasting only two bits per row
        // This only works because each maze row is 30 cells with two states
        // Assuming we're forced to use bytes and have to keep rows seperate, this is as efficient as it could possibly be
        // Because I control the language, I can add as many confusing bitstring ops as I want (lol)
        int[] arr = new int[this.mazeStrings.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 30; j++) {
                if (this.mazeStrings[i].charAt(j) == 'X') {
                    arr[i] |= (0b1 << j);
                }
            }
        }
        FVal_IDX[] arrIdx = new FVal_IDX[this.mazeStrings.length];
        for (int i = 0; i < arr.length; i++) arrIdx[i] = new FVal_IDX(arr[i]);
        return new FVal_QTD(new FVal_LST(arrIdx));
    }
}
