import java.util.HashMap;
import java.util.stream.Stream;

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
            case 'c':
                FVal result = eval_any(cdr[cdr.length - 1], env);
                for (int i = cdr.length - 2; i > 0; i--) {
                    FVal_LST v = (FVal_LST)(cdr[i]);
                    FVal[] alt = new FVal[v.u.length + 1];
                    for (int j = 0; j < v.u.length; j++) {
                        alt[j] = v.u[j];
                    }
                    alt[v.u.length] = result;
                    result = eval_code(new FVal_LST(alt), env);
                }
                return result;
            case 'w':
                HashMap<String,FVal> env_pr1 = new HashMap<>(env);
                env_pr1.put(((FVal_SYM)cdr[0]).uName, eval_any(cdr[1], env));
                return eval_any(cdr[2], env_pr1);
            case 'n':
                String[] args1 = new String[cdr.length - 2];
                for (int i = 0; i < args1.length; i++)
                { args1[i] = ((FVal_SYM)(cdr[i + 1])).uName; }
                HashMap<String,FVal> env_pr2 = new HashMap<>(env);
                env_pr2.put(
                    ((FVal_SYM)cdr[0]).uName,
                    new FVal_FUN(args1, cdr[cdr.length - 1])
                );
                return eval_any(cdr[2], env_pr2);
            case 'i':
                if (((FVal_BLN)eval_any(cdr[0], env)).u) return eval_any(cdr[1], env);
                else return eval_any(cdr[2], env);
            case 'f':
                String[] args2 = new String[cdr.length - 1];
                for (int i = 0; i < args2.length; i++)
                { args2[i] = ((FVal_SYM)(cdr[i + 1])).uName; }
                return new FVal_FUN(args2, cdr[cdr.length - 1]);
        }
        if (!(car instanceof FVal_FUN)) throw new Error(
            String.format("Cannot apply type `%s'", car.getClass().getName())
        );
        FVal_FUN fun = (FVal_FUN) car;
        HashMap<String,FVal> env_pr = new HashMap<>(env);
        for (int i = 0; i < fun.args.length; i++) {
            env_pr.put(fun.args[i], cdr[i]);
        }
        return eval_any(fun.body, env_pr);
    }

    public FVal eval_symb(FVal_SYM expr, HashMap<String,FVal> env) {
        if (expr.uName.equals("chain"))      return new SpecialForm('c');
        if (expr.uName.equals("with"))       return new SpecialForm('w');
        if (expr.uName.equals("with-fn"))    return new SpecialForm('n');
        if (expr.uName.equals("if"))         return new SpecialForm('i');
        if (expr.uName.equals("fun"))        return new SpecialForm('f');
        return env.get(expr.uName); 
    }

    public FVal_STR eval_fstr(FVal_FMS expr, HashMap<String,FVal> env) {
        return new FVal_STR(
            String.format(
                expr.u,
                Stream.of(expr.refs)
                    .map(env::get)
                    .toArray()
            )
        );
    }

    public FVal eval_any(FVal expr, HashMap<String,FVal> env) {
        if (expr instanceof FVal_LST) return eval_code((FVal_LST)expr, env);
        if (expr instanceof FVal_SYM) return eval_symb((FVal_SYM)expr, env);
        if (expr instanceof FVal_FMS) return eval_fstr((FVal_FMS)expr, env);
        else return expr;
    }
}
