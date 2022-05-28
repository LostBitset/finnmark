import java.util.function.Function;

public class FVal_JFN implements FVal {
    public Function<FVal[],FVal> lambda;

    public FVal_JFN(Function<FVal[],FVal> lambda) { this.lambda = lambda; }
}
