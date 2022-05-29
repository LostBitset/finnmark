import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FinnmarkMain {
    public static void main(String[] args) {
        System.out.printf(
            "[RUN] Looking for `main.fnn' in `%s'...\n",
            System.getProperty("user.dir"));
        String source;
        try {
            source = Files.readString(Paths.get("main.fnn"));
        } catch (IOException err) {
            System.out.println("[RUN] Cannot find `main.fnn'");
            return;
        }
        FVal code = FinnmarkParser.parseExpr(source);
        System.out.println(code);
        Evaluator evaluator = new Evaluator();
        System.out.println("[RUN] Starting...");
        FVal res = evaluator.eval_any(code, evaluator.defaultEnv);
        System.out.println("[RUN] Completed.");
        System.out.printf("Got: %s\n", res.toString());
    }
}
