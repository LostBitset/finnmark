import java.util.HashMap;
import java.util.function.BiFunction;

public class FVal_JFN implements FVal {
    public BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda;

    public FVal_JFN(BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda) { this.lambda = lambda; }
}
