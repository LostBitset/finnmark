import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
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
        FVal_LST code = (FVal_LST) FinnmarkParser.parseExpr(source); // Upcast
        System.out.println(code);
        RRegistry reg = new RRegistry();
        /*PassiveCompiler compiler = new PassiveCompiler(reg);*/
        Evaluator evaluator = new Evaluator(reg);
        System.out.println("[RUN] Starting...");
        String res = evaluator.eval_code(code, new HashMap<>()).toString();
        System.out.println("[RUN] Completed.");
        System.out.printf("Got: %s\n", res);
    }
}
