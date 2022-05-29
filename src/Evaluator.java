import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Evaluator {
    public HashMap<String,FVal> defaultEnv;
    
    public Evaluator() {
        this.defaultEnv = new HashMap<>();
        this.defaultEnv.put("println", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal e = eval_any(x[0], env);
                System.out.println(((FVal_STR)(
                    e
                )).u);
                return (FVal)(e);
            }
        ));
        this.defaultEnv.put("show", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal e = eval_any(x[0], env);
                System.out.println(
                    e.toString()
                );
                return (FVal)(e);
            }
        ));
        this.defaultEnv.put("repr", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal e = eval_any(x[0], env);
                return new FVal_STR(
                    e.toString()
                );
            }
        ));
        this.defaultEnv.put("#t", (FVal) new FVal_BLN(true));
        this.defaultEnv.put("#f", (FVal) new FVal_BLN(false));
        this.defaultEnv.put("not", eval_any(
            FinnmarkParser.parseExpr("(fun x (if x #f #t))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("and", eval_any(
            FinnmarkParser.parseExpr("(fun a b (if a b #f))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("or", eval_any(
            FinnmarkParser.parseExpr("(fun a b (if a #t b))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("nand", eval_any(
            FinnmarkParser.parseExpr("(fun a b (not (and a b)))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("nor", eval_any(
            FinnmarkParser.parseExpr("(fun a b (not (or a b)))"),
            this.defaultEnv
        ));
        this.defaultEnv.put(":", (FVal) new FVal_JFN(
            2,
            (x, env) -> {
                FVal lstE = eval_any(x[1], env);
                FVal_LST lst = (FVal_LST)(((FVal_QTD)lstE).inner);
                FVal[] arr = lst.u;
                FVal[] arr_pr = new FVal[lst.u.length + 1];
                arr_pr[0] = eval_any(x[0], env);
                for (int i = 1; i < arr_pr.length; i++) arr_pr[i] = arr[i - 1];
                return new FVal_QTD(new FVal_LST(arr_pr));
            }
        ));
        this.defaultEnv.put("rev", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal lstE = eval_any(x[0], env);
                FVal_LST lst = (FVal_LST)(((FVal_QTD)lstE).inner);
                FVal[] arr = lst.u;
                FVal[] arr_pr = new FVal[lst.u.length];
                for (int i = 0; i < arr_pr.length; i++) arr_pr[i] = arr[arr.length - i - 1];
                return new FVal_QTD(new FVal_LST(arr_pr));
            }
        ));
        this.defaultEnv.put("map", (FVal) new FVal_JFN(
            2,
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
            3,
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
        this.defaultEnv.put("arity", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal fun = eval_any(x[0], env);
                if (fun instanceof FVal_JFN) {
                    Integer jfnArity = ((FVal_JFN)fun).arity;
                    return new FVal_IDX(
                        jfnArity
                    );
                }
                else if (fun instanceof FVal_FUN) return new FVal_IDX(
                    ((FVal_FUN)fun).args.length
                );
                else if (fun instanceof FVal_IDX) return new FVal_IDX(
                    1 // Actually takes one arg
                );
                else if (fun instanceof FVal_XCO) return new FVal_IDX(
                    2 // Variadic constructs should have arity 2
                );
                else throw new Error(
                    String.format("Arity not given for type `%s'", x[0].getClass().getName())
                );
            }
        ));
        FVal[] nilList = new FVal[0];
        this.defaultEnv.put("nil", (FVal) new FVal_QTD(new FVal_LST(nilList)));
        this.defaultEnv.put("id", eval_any(
            FinnmarkParser.parseExpr("(fun x x)"),
            this.defaultEnv
        ));
        this.defaultEnv.put("+", (FVal) new FVal_JFN(
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
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
                        throw new Error("Cannot calc NUM + IDX");
                    default:
                        throw new Error("unreachable");
                }
            }
        ));
        this.defaultEnv.put("-", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                int numTypeMask = 0b00;
                if (x[0] instanceof FVal_IDX) numTypeMask |= 0b10;
                if (x[1] instanceof FVal_IDX) numTypeMask |= 0b01;
                switch (numTypeMask) {
                    case 0b00:
                        return new FVal_NUM(
                            ((FVal_NUM)(x[0])).u - ((FVal_NUM)(x[1])).u
                        );
                    case 0b01:
                        throw new Error("Cannot calc NUM - IDX");
                    case 0b10:
                        throw new Error("Cannot calc IDX - NUM");
                    case 0b11:
                        return new FVal_IDX(
                            ((FVal_IDX)(x[0])).u - ((FVal_IDX)(x[1])).u
                        );
                    default:
                        throw new Error("unreachable");
                }
            }
        ));
        this.defaultEnv.put("*", (FVal) new FVal_JFN(
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                double sumNum = 0.0;
                int sumIdx = 0;
                int numTypeMask = 0b00;
                for (int i = 0; i < x.length; i++) {
                    if (x[i] instanceof FVal_IDX) {
                        sumIdx *= ((FVal_IDX)(x[i])).u;
                        numTypeMask |= 0b10;
                    } else {
                        sumNum *= ((FVal_NUM)(x[i])).u;
                        numTypeMask |= 0b01;
                    }
                }
                switch (numTypeMask) {
                    case 0b00:
                        throw new Error("No */0 (could not pick type)");
                    case 0b01:
                        return (FVal) new FVal_NUM(sumNum);
                    case 0b10:
                        return (FVal) new FVal_IDX(sumIdx);
                    case 0b11:
                        return (FVal) new FVal_NUM(sumNum * sumIdx);
                    default:
                        throw new Error("unreachable");
                }
            }
        ));
        this.defaultEnv.put("/", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                int numTypeMask = 0b00;
                if (x[0] instanceof FVal_IDX) numTypeMask |= 0b10;
                if (x[1] instanceof FVal_IDX) numTypeMask |= 0b01;
                switch (numTypeMask) {
                    case 0b00:
                        return new FVal_NUM(
                            ((FVal_NUM)(x[0])).u / ((FVal_NUM)(x[1])).u
                        );
                    case 0b01:
                        return new FVal_NUM(
                            ((FVal_NUM)(x[0])).u / ((FVal_IDX)(x[1])).u
                        );
                    case 0b10:
                        return new FVal_NUM(
                            ((FVal_IDX)(x[0])).u / ((FVal_NUM)(x[1])).u
                        );
                    case 0b11:
                        throw new Error("Cannot calc IDX / IDX (not closed)");
                    default:
                        throw new Error("unreachable");
                }
            }
        ));
        this.defaultEnv.put("//", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                return new FVal_IDX(
                    ((FVal_IDX)(x[0])).u / ((FVal_IDX)(x[1])).u
                );
            }
        ));
        this.defaultEnv.put("zero-p", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal_NUM num = (FVal_NUM) eval_any(x[0], env);
                return new FVal_BLN(num.u == 0.0);
            }
        ));
        this.defaultEnv.put("=", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if (x[0].getClass() != x[1].getClass()) throw new Error(
                    String.format(
                        "Can only compare identical types, not `%s' and `%s'",
                        x[0].getClass().getName(),
                        x[1].getClass().getName()
                    )
                );
                else if (x[0] instanceof FVal_BLN) return new FVal_BLN(((FVal_BLN)(x[0])).u == ((FVal_BLN)(x[1])).u);
                else if (x[0] instanceof FVal_IDX) return new FVal_BLN(((FVal_IDX)(x[0])).u == ((FVal_IDX)(x[1])).u);
                else if (x[0] instanceof FVal_STR) return new FVal_BLN(((FVal_STR)(x[0])).u.equals(((FVal_STR)(x[1])).u));
                else throw new Error(
                    String.format("Cannot compare type `%s'", x[0].getClass().getName())
                );
            }
        ));
        this.defaultEnv.put(">=", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if (x[0].getClass() != x[1].getClass()) throw new Error(
                    String.format(
                        "Can only compare identical types, not `%s' and `%s'",
                        x[0].getClass().getName(),
                        x[1].getClass().getName()
                    )
                );
                else if (x[0] instanceof FVal_NUM) return new FVal_BLN(((FVal_NUM)(x[0])).u >= ((FVal_NUM)(x[1])).u);
                else if (x[0] instanceof FVal_IDX) return new FVal_BLN(((FVal_IDX)(x[0])).u >= ((FVal_IDX)(x[1])).u);
                else throw new Error(
                    String.format("Cannot compare type `%s'", x[0].getClass().getName())
                );
            }
        ));
        this.defaultEnv.put("<=", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if (x[0].getClass() != x[1].getClass()) throw new Error(
                    String.format(
                        "Can only compare identical types, not `%s' and `%s'",
                        x[0].getClass().getName(),
                        x[1].getClass().getName()
                    )
                );
                else if (x[0] instanceof FVal_NUM) return new FVal_BLN(((FVal_NUM)(x[0])).u <= ((FVal_NUM)(x[1])).u);
                else if (x[0] instanceof FVal_IDX) return new FVal_BLN(((FVal_IDX)(x[0])).u <= ((FVal_IDX)(x[1])).u);
                else throw new Error(
                    String.format("Cannot compare type `%s'", x[0].getClass().getName())
                );
            }
        ));
        this.defaultEnv.put(">", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if (x[0].getClass() != x[1].getClass()) throw new Error(
                    String.format(
                        "Can only compare identical types, not `%s' and `%s'",
                        x[0].getClass().getName(),
                        x[1].getClass().getName()
                    )
                );
                else if (x[0] instanceof FVal_NUM) return new FVal_BLN(((FVal_NUM)(x[0])).u > ((FVal_NUM)(x[1])).u);
                else if (x[0] instanceof FVal_IDX) return new FVal_BLN(((FVal_IDX)(x[0])).u > ((FVal_IDX)(x[1])).u);
                else throw new Error(
                    String.format("Cannot compare type `%s'", x[0].getClass().getName())
                );
            }
        ));
        this.defaultEnv.put("<", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if (x[0].getClass() != x[1].getClass()) throw new Error(
                    String.format(
                        "Can only compare identical types, not `%s' and `%s'",
                        x[0].getClass().getName(),
                        x[1].getClass().getName()
                    )
                );
                else if (x[0] instanceof FVal_NUM) return new FVal_BLN(((FVal_NUM)(x[0])).u < ((FVal_NUM)(x[1])).u);
                else if (x[0] instanceof FVal_IDX) return new FVal_BLN(((FVal_IDX)(x[0])).u < ((FVal_IDX)(x[1])).u);
                else throw new Error(
                    String.format("Cannot compare type `%s'", x[0].getClass().getName())
                );
            }
        ));
        this.defaultEnv.put("++", eval_any(
            FinnmarkParser.parseExpr("(fun x (+ x 1a))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("--", eval_any(
            FinnmarkParser.parseExpr("(fun x (- x 1a))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("cl", (FVal) new FVal_JFN(
            1,
            (x, env) -> {
                FVal[] arrArity = new FVal[] { new FVal_SYM("arity"), x[0] };
                FVal[] arrDec = new FVal[] { new FVal_SYM("--"), new FVal_LST(arrArity) };
                FVal[] arr = new FVal[3];
                arr[0] = new SpecialForm('-');
                arr[1] = eval_code(new FVal_LST(arrDec), env);
                arr[2] = x[0];
                return eval_any(
                    new FVal_LST(arr),
                    env
                );
            }
        ));
        this.defaultEnv.put("flip", eval_any(
            FinnmarkParser.parseExpr("(chain (fun f a b (f b a)) (cl) (cl))"),
            this.defaultEnv
        ));
        this.defaultEnv.put("&", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if ((x[0] instanceof FVal_IDX) && (x[1] instanceof FVal_IDX)) {
                    return new FVal_IDX(
                        ((FVal_IDX)(x[0])).u & ((FVal_IDX)(x[1])).u
                    );
                } else {
                    throw new Error(
                        String.format(
                            "Cannot bitwise and types `%s' and `%s'",
                            x[0].getClass().getName(),
                            x[1].getClass().getName()
                        )
                    );
                }
            }
        ));
        this.defaultEnv.put("|", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if ((x[0] instanceof FVal_IDX) && (x[1] instanceof FVal_IDX)) {
                    return new FVal_IDX(
                        ((FVal_IDX)(x[0])).u | ((FVal_IDX)(x[1])).u
                    );
                } else {
                    throw new Error(
                        String.format(
                            "Cannot bitwise or types `%s' and `%s'",
                            x[0].getClass().getName(),
                            x[1].getClass().getName()
                        )
                    );
                }
            }
        ));
        this.defaultEnv.put("<<", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if ((x[0] instanceof FVal_IDX) && (x[1] instanceof FVal_IDX)) {
                    return new FVal_IDX(
                        ((FVal_IDX)(x[0])).u << ((FVal_IDX)(x[1])).u
                    );
                } else {
                    throw new Error(
                        String.format(
                            "Cannot lshift types `%s' and `%s'",
                            x[0].getClass().getName(),
                            x[1].getClass().getName()
                        )
                    );
                }
            }
        ));
        this.defaultEnv.put(">>", (FVal) new FVal_JFN(
            2,
            (xE, env) -> {
                FVal[] x = new FVal[xE.length];
                for (int i = 0; i < xE.length; i++) x[i] = eval_any(xE[i], env);
                if ((x[0] instanceof FVal_IDX) && (x[1] instanceof FVal_IDX)) {
                    return new FVal_IDX(
                        ((FVal_IDX)(x[0])).u | ((FVal_IDX)(x[1])).u
                    );
                } else {
                    throw new Error(
                        String.format(
                            "Cannot rshift types `%s' and `%s'",
                            x[0].getClass().getName(),
                            x[1].getClass().getName()
                        )
                    );
                }
            }
        ));
    }

    public FVal eval_code(FVal_LST expr, HashMap<String,FVal> env) {
        //System.out.printf("Evaluating expr with car `%s'...\n", expr.u[0]);
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
                return new FVal_XCO(cdr[1], (FVal_IDX)(eval_any(cdr[0], env)), env);
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
            return ((FVal_XCO)car).introduce(cdr, this, env);
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
