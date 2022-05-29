import java.util.HashMap;
import java.util.function.BiFunction;

public class FVal_JFN implements FVal {
    public BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda;
    public int arity;

    public FVal_JFN(int arity, BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda) {
        this.lambda = lambda;
        this.arity = arity;
    }

    public FVal_JFN(BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda) {
        this.lambda = lambda;
        this.arity = 2;
    }

    public String toString() {
        return String.format("[jfn](%s)/%d", this.lambda, this.arity);
    }
}
