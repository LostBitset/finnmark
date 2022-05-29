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

    public FVal introduce(FVal[] next, Evaluator evaluator) {
        boolean completed = false;
        if (this.fill == null) this.fill = next;
        else completed = true;
        if (completed) {
            return new FVal_JFN(
                (x, env) -> {
                    FVal[] res = new FVal[this.fill.length + 2];
                    res[0] = evaluator.eval_any(this.car, env);
                    for (int i = 1, iF = 1; i < this.fill.length + 1; i++) {
                        if (i == this.idx) {
                            res[i] = evaluator.eval_any(x[0], env);
                        } else {
                            res[i] = evaluator.eval_any(this.fill[iF], env);
                        }
                    }
                    return evaluator.eval_code(new FVal_LST(res), env);
                }
            );
        } else {
            return this;
        }
    }
}
