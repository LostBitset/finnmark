import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Evaluator {
    public HashMap<String,FVal> defaultEnv;
    
    public Evaluator() {
        this.defaultEnv = new HashMap<>();
        this.defaultEnv.put("println", (FVal) new FVal_JFN(
            (x, env) -> { System.out.println(((FVal_STR)(x[0])).u); return (FVal)(x[0]); }
        ));
        this.defaultEnv.put("map", (FVal) new FVal_JFN(
            (x, env) -> {
                FVal_LST innerList = (FVal_LST)(((FVal_QTD)(
                    eval_any(x[1], env)
                )).inner);
                FVal[] res = new FVal[innerList.u.length];
                for (int i = 0; i < innerList.u.length; i++) {
                    FVal[] appExpr = new FVal[2];
                    appExpr[0] = x[0];
                    appExpr[1] = innerList.u[i];
                    res[i] = eval_code(new FVal_LST(appExpr), env);
                }
                return new FVal_QTD(new FVal_LST(res));
            }
        ));
        this.defaultEnv.put("fold", (FVal) new FVal_JFN(
            (x, env) -> {
                // (fold (fun x a ...) init list)
                FVal_LST innerList = (FVal_LST)(((FVal_QTD)(x[2])).inner);
                FVal res = x[1];
                for (int i = 0; i < innerList.u.length; i++) {
                    FVal[] appExpr = new FVal[3];
                    appExpr[0] = x[0];
                    appExpr[1] = innerList.u[i];
                    appExpr[2] = res;
                    res = eval_code(new FVal_LST(appExpr), env);
                }
                return res;
            }
        ));
        this.defaultEnv.put("unravel", (FVal) new FVal_JFN(
            (x, env) -> {
                // (unravel idx-list ...)
                FVal[] exprNew = IntStream.range(0, x.length - 1)
                    .mapToObj(Integer::valueOf)
                    .flatMap((Integer a) -> {
                        boolean toUnravel = false;
                        for (FVal e : ((FVal_LST)(((FVal_QTD)(x[0])).inner)).u) {
                            if (a == ((FVal_IDX)e).u) toUnravel = true;
                        }
                        if (toUnravel) {
                            return Stream.of(
                                ((FVal_LST)(((FVal_QTD)(
                                    eval_any(x[a + 1], env)
                                )).inner)).u
                            );
                        }
                        else return Stream.of(x[a + 1]);
                    })
                    .toArray(FVal[]::new);
                return eval_code(new FVal_LST(exprNew), env);
            }
        ));
        FVal[] nilList = new FVal[0];
        this.defaultEnv.put("nil", (FVal) new FVal_QTD(new FVal_LST(nilList)));
        this.defaultEnv.put("id", eval_any(
            FinnmarkParser.parseExpr("(fun x x)"),
            this.defaultEnv
        ));
        this.defaultEnv.put("+", (FVal) new FVal_JFN(
            (x, env) -> {
                double sumNum = 0.0;
                int sumIdx = 0;
                int numTypeMask = 0b00;
                for (int i = 0; i < x.length; i++) {
                    if (x[i] instanceof FVal_IDX) {
                        sumIdx += ((FVal_IDX)(x[i])).u;
                        numTypeMask |= 0b10;
                    } else {
                        sumNum += ((FVal_NUM)(x[i])).u;
                        numTypeMask |= 0b01;
                    }
                }
                switch (numTypeMask) {
                    case 0b00:
                        throw new Error("No +/0 (could not pick type)");
                    case 0b01:
                        return (FVal) new FVal_NUM(sumNum);
                    case 0b10:
                        return (FVal) new FVal_IDX(sumIdx);
                    case 0b11:
                        throw new Error("Cannot add types NUM and IDX");
                    default:
                        throw new Error("unreachable");
                }
            }
        ));
    }

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
                { args2[i] = ((FVal_SYM)(cdr[i])).uName; }
                return new FVal_FUN(args2, cdr[cdr.length - 1]);
            case '-':
                return new FVal_XCO(cdr[1], (FVal_IDX)(cdr[0]));
        }
        for (int i = 0; i < cdr.length; i++) {
            cdr[i] = eval_any(cdr[i], env);
        }
        if (car instanceof FVal_IDX) {
            return ((FVal_LST)(((FVal_QTD)(cdr[0])).inner)).u[((FVal_IDX)car).u];
        }
        if (car instanceof FVal_JFN) {
            return ((FVal_JFN)car).lambda.apply(cdr, env);
        }
        if (car instanceof FVal_XCO) {
            return ((FVal_XCO)car).introduce(cdr, this);
        }
        if (!(car instanceof FVal_FUN)) throw new Error(
            String.format("Cannot apply type `%s'", car.getClass().getName())
        );
        FVal_FUN fun = (FVal_FUN) car;
        HashMap<String,FVal> env_pr = new HashMap<>(env);
        for (int i = 0; i < fun.args.length; i++) {
            env_pr.put(fun.args[i], cdr[i]);
        }
        System.out.println("body : " + fun.body);
        System.out.println(env_pr);
        return eval_any(fun.body, env_pr);
    }

    public FVal eval_symb(FVal_SYM expr, HashMap<String,FVal> env) {
        System.out.println("env : " + env);
        if (expr.uName.equals("chain"))     return new SpecialForm('c');
        if (expr.uName.equals("with"))      return new SpecialForm('w');
        if (expr.uName.equals("with-fn"))   return new SpecialForm('n');
        if (expr.uName.equals("if"))        return new SpecialForm('i');
        if (expr.uName.equals("fun"))       return new SpecialForm('f');
        if (expr.uName.equals("co"))        return new SpecialForm('-');
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

    public FVal eval_unqt(FVal_UNQ expr, HashMap<String,FVal> env) {
        if (expr.inner instanceof FVal_QTD) {
            return ((FVal_QTD)(expr.inner)).inner;
        } else {
            return ((FVal_QTD)(eval_any(expr, env))).inner;
        }
    }

    public FVal eval_any(FVal expr, HashMap<String,FVal> env) {
        if (expr instanceof FVal_LST) return eval_code((FVal_LST)expr, env);
        if (expr instanceof FVal_SYM) return eval_symb((FVal_SYM)expr, env);
        if (expr instanceof FVal_FMS) return eval_fstr((FVal_FMS)expr, env);
        if (expr instanceof FVal_UNQ) return eval_unqt((FVal_UNQ)expr, env);
        else return expr;
    }
}
