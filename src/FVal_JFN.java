import java.util.HashMap;
import java.util.function.BiFunction;

public class FVal_JFN implements FVal {
    public BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda;
    public Integer arity;

    public FVal_JFN(int arity, BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda) {
        this.lambda = lambda;
        this.arity = arity;
    }

    public FVal_JFN(BiFunction<FVal[],HashMap<String,FVal>,FVal> lambda) {
        this.lambda = lambda;
        this.arity = null;
    }

    public String toString() {
        if (this.arity == null) {
            return String.format("[jfn](%s)/<noarity>", this.lambda);
        } else {
            return String.format("[jfn](%s)/%d", this.lambda, (int)this.arity);
        }
    }
}
