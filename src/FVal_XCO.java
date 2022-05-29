import java.util.HashMap;

public class FVal_XCO implements FVal {
    public FVal car;
    public int idx;
    public FVal[] fill;
    public HashMap<String,FVal> localEnv;

    public FVal_XCO(FVal car, int idx, HashMap<String,FVal> origEnv) {
        this.car = car;
        this.idx = idx;
        this.localEnv = new HashMap<>(origEnv);
    }

    public FVal_XCO(FVal car, FVal_IDX idxObj, HashMap<String,FVal> origEnv) {
        this(car, idxObj.u, origEnv);
    }

    public FVal introduce(FVal[] fill, Evaluator evaluator, HashMap<String,FVal> invocationEnv) {
        this.fill = fill;
        this.localEnv.putAll(invocationEnv);
        return new FVal_JFN(
            1,
            (x, env) -> {
                this.localEnv.putAll(env);
                FVal[] res = new FVal[this.fill.length + 2];
                System.out.println(this.car);
                res[0] = evaluator.eval_any(this.car, env);
                for (int i = 0, iF = 0; i < this.fill.length + 1; i++) {
                    if (i == this.idx) {
                        res[i + 1] = evaluator.eval_any(x[0], this.localEnv);
                    } else {
                        res[i + 1] = evaluator.eval_any(this.fill[iF], this.localEnv);
                        iF++;
                    }
                }
                System.out.println(new FVal_LST(res));
                return evaluator.eval_code(new FVal_LST(res), this.localEnv);
            }
        );
    }
}
