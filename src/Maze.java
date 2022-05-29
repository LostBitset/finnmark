import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Maze {
    public String[] mazeStrings;

    public Maze(String[] mazeStrings) {
        this.mazeStrings = mazeStrings;
    }
    
    public void search(int startR, int startC, double startAngleDouble) {
        int startA = (int) startAngleDouble;
        String source;
        try {
            source = Files.readString(Paths.get("maze.fnn"));
        } catch (IOException err) {
            System.out.println("[RUN] Cannot find `maze.fnn'");
            return;
        }
        FVal code = FinnmarkParser.parseExpr(source);
        System.out.println(code);
        Evaluator evaluator = new Evaluator();
        evaluator.defaultEnv.put("maze", fnnMaze(startR, startC, startA));
        FVal res = evaluator.eval_any(code, evaluator.defaultEnv);
    }

    public static FVal fnnMaze(Integer... args) {
        // TODO
    }
}
