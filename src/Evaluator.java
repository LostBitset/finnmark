import java.util.HashMap;

public class Evaluator {
    public RRegistry reg;

    public Evaluator(RRegistry reg) {
        this.reg = reg;
    }

    public FVal eval_code(FVal_LST expr, HashMap<String,FVal> env) {
        FVal car = null;
        FVal[] cdr = new FVal[expr.u.length - 1];
        for (int i = 0; i < expr.u.length; i++) {
            if (i == 0) car = eval_any(expr.u[i], env);
            else cdr[i - 1] = eval_any(expr.u[i], env);
        }
        if (car == null) throw new Error("Cannot evaluate an empty list");
        if (car instanceof SpecialForm) switch (((SpecialForm)car).ch) {
            // TODO
        }
        // TODO
    }

    public FVal eval_symb(FVal_SYM expr, HashMap<String,FVal> env) {
        if (expr.uName.equals("chain")) return new SpecialForm('c');
        return env.get(expr.uName); 
    }

    public FVal eval_any(FVal expr, HashMap<String,FVal> env) {
        if (expr instanceof FVal_LST) return eval_code((FVal_LST)expr, env);
        if (expr instanceof FVal_SYM) return eval_symb((FVal_SYM)expr, env);
        else return expr;
    }
}
