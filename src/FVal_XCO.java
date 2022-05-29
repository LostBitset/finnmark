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

    public FVal introduce(FVal[] next) {
        boolean completed = false;
        if (false);
        else if (this.fill == null) this.fill = next;
        else completed = true;
        if (completed) {
            return new FVal_JFN(
                (x, env) -> {
                    FVal[] args = new FVal[this.fill.length + 1];
                    for (int i = 0, iF = 0; i < this.fill.length + 1; i++) {
                        //
                    }
                    return eval_any()
                }
            );
        } else {
            return this;
        }
    }
}
