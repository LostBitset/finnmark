import java.util.ArrayList;

public class FinnmarkParser {
    public static FVal parseExpr(String raw) {
        String s = raw
            .trim()
            .replaceAll("\n", " ")
            .replaceAll("\t", " ")
            .replaceAll(" +", " ");
        switch (s.charAt(0)) {
            case '(':
                return parseList(s);
            case '~':
                return parseFormatted(s);
            case '\'':
                return parseString(s);
            case ':':
                if (s.charAt(s.length() - 1) == 'a') {
                    return asIdx(readBinInt(
                        s.substring(1, s.length() - 2)
                    ));
                } else {
                    throw new Error("Binary literals must be of the index type");
                }
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-':
                if (s.charAt(s.length() - 1) == 'a') {
                    return asIdx(readDecInt(
                        s.substring(0, s.length() - 1)
                    ));
                } else {
                    return asNum(readDecDouble(s));
                }
            default:
                return parseSym(s);
        }
    }

    private static FVal_SYM parseSym(String s) {
        return new FVal_SYM(s);
    }

    private static double readDecDouble(String s) {
        return (double) Double.valueOf(s);
    }

    private static int readDecInt(String s) {
        return (int) Integer.parseInt(s);
    }
    
    private static int readBinInt(String s) {
        return (int) Integer.parseInt(s, 2);
    }

    private static FVal_NUM asNum(double n) {
        return new FVal_NUM(n);
    }

    private static FVal_IDX asIdx(int n) {
        return new FVal_IDX(n);
    }

    private static FVal_STR parseString(String s) {
        return new FVal_STR(
            s
                .substring(1, s.length() - 1)
                .replaceAll("\\n", "\n")
                .replaceAll("\\t", "\t")
        );
    }

    private static FVal_FMS parseFormatted(String s) {
        return new FVal_FMS(
            s.substring(2, s.length() - 2)
        );
    }

    private static FVal_LST parseList(String s) {
        ArrayList<FVal> items = new ArrayList<>();
        StringBuilder currBuf = new StringBuilder();
        int nested = 0;
        boolean inStr = false;
        boolean inStrEsc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (nested == 0 && i != 0) throw new Error("List ended early");
            if (!inStr) {
                switch (c) {
                    case ' ':
                        if (nested != 1) break; // Break out of the `switch', not the `for'
                        items.add(
                            parseExpr(currBuf.toString())
                        );
                        currBuf = new StringBuilder();
                        continue;
                    case '(':
                        if (++nested == 1) continue;
                        else break;
                    case ')':
                        if (--nested == 1) continue;
                        else break;
                }
            }
            switch (c) {
                case '\'':
                    inStr = !inStr || inStrEsc;
                    break;
                case '\\':
                    inStrEsc = !inStrEsc;
                    break;
            }
            currBuf.append(c);
        }
        items.add(
            parseExpr(currBuf.toString())
        );
        return new FVal_LST(
            items.toArray(FVal[]::new)
        );
    }
}
