import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FVal_XCO implements FVal {
    public FVal car;
    public int idx;
    public FVal[] fill;

    public FVal_XCO(FVal car, int idx) {
        this.car = car;
        this.idx = idx;
    }

    public FVal_XCO(FVal car, FVal_IDX idxObj) {
        this(car, idxObj.u);
    }

    public FVal introduce(FVal[] fill, Evaluator evaluator) {
        this.fill = fill;
        return new FVal_JFN(
            (x, env) -> {
                FVal[] res = new FVal[this.fill.length + 2];
                res[0] = evaluator.eval_any(this.car, env);
                for (int i = 0, iF = 0; i < this.fill.length + 1; i++) {
                    if (i == this.idx) {
                        res[i + 1] = evaluator.eval_any(x[0], env);
                    } else {
                        res[i + 1] = evaluator.eval_any(this.fill[iF], env);
                        iF++;
                    }
                }
                return evaluator.eval_code(new FVal_LST(res), env);
            }
        );
    }
}
