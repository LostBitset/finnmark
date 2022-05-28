import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Evaluator {
    public static HashMap<String,FVal> defaultEnv = new HashMap<>(Map.ofEntries(
        new AbstractMap.SimpleEntry<>("println", (FVal) new FVal_JFN(
            x -> { System.out.println(((FVal_STR)(x[0])).u); return (FVal)(x[0]); }
        ))
    ));

    public FVal eval_code(FVal_LST expr, HashMap<String,FVal> env) {
        System.out.printf("Evaluating expr with car `%s'...\n", expr.u[0]);
        FVal car = eval_any(expr.u[0], env);
        FVal[] cdr = new FVal[expr.u.length - 1];
        for (int i = 1; i < expr.u.length; i++) {
            cdr[i - 1] = expr.u[i];
        }
        if (car instanceof SpecialForm) switch (((SpecialForm)car).ch) {
            case 'c':
                FVal curr = cdr[0];
                for (int i = 1; i < cdr.length; i++) {
                    FVal_LST ls = (FVal_LST) cdr[i];
                    FVal[] nextAsArray = new FVal[ls.u.length + 1];
                    for (int j = 0; j < ls.u.length; j++) nextAsArray[j] = ls.u[j];
                    nextAsArray[ls.u.length] = curr;
                    curr = new FVal_LST(nextAsArray);
                }
                return eval_any(curr, env);
            case 'w':
                HashMap<String,FVal> env_pr1 = new HashMap<>(env);
                env_pr1.put(((FVal_SYM)cdr[0]).uName, eval_any(cdr[1], env));
                return eval_any(cdr[2], env_pr1);
            case 'n':
                String[] args1 = new String[cdr.length - 3];
                for (int i = 0; i < args1.length; i++) {
                    args1[i] = ((FVal_SYM)(cdr[i + 1])).uName;
                }
                HashMap<String,FVal> env_pr2 = new HashMap<>(env);
                env_pr2.put(
                    ((FVal_SYM)cdr[0]).uName,
                    new FVal_FUN(args1, cdr[cdr.length - 2])
                );
                return eval_any(cdr[cdr.length - 1], env_pr2);
            case 'i':
                if (((FVal_BLN)eval_any(cdr[0], env)).u) return eval_any(cdr[1], env);
                else return eval_any(cdr[2], env);
            case 'f':
                String[] args2 = new String[cdr.length - 1];
                for (int i = 0; i < args2.length; i++)
                { args2[i] = ((FVal_SYM)(cdr[i + 1])).uName; }
                return new FVal_FUN(args2, cdr[cdr.length - 1]);
        }
        for (int i = 0; i < cdr.length; i++) {
            cdr[i] = eval_any(cdr[i], env);
        }
        if (car instanceof FVal_JFN) {
            return ((FVal_JFN)car).lambda.apply(cdr);
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
        if (!env.containsKey(expr.uName)) throw new Error(
            String.format("Cannot find value for `%s'\n", expr.uName)
        );
        return env.get(expr.uName);
    }

    public FVal_STR eval_fstr(FVal_FMS expr, HashMap<String,FVal> env) {
        return new FVal_STR(
            String.format(
                expr.u,
                Stream.of(expr.refs)
                    .map(env::get)
                    .map(x -> {
                        if (x instanceof FVal_STR) return ((FVal_STR)x).u;
                        else return x.toString();
                    })
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
