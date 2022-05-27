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
                int n = readBinInt(s);
                if (s.charAt(s.length() - 1) == 'a') return asIdx(n);
                else return asNum(n);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-':
                if (s.charAt(s.length() - 1) == 'a') {
                    return asIdx(readDecInt(s));
                } else {
                    return asNum(readDecDouble(s));
                }
            default:
                return parseSym(s);
        }
    }

    private static FVal_SYM parseSym(String s) {
        return null;
    }

    private static double readDecDouble(String s) {
        return 0.0;
    }

    private static int readDecInt(String s) {
        return 0;
    }

    private static FVal_NUM asNum(double n) {
        return null;
    }

    private static FVal_IDX asIdx(int n) {
        return null;
    }

    private static int readBinInt(String s) {
        return 0;
    }

    private static FVal_STR parseString(String s) {
        return new FVal_STR(
            s
                .substring(1, s.length() - 2)
                .replaceAll("\\n", "\n")
                .replaceAll("\\t", "\t")
                .replaceAll("\\0", "\0")
        );
    }

    private static FVal_FMS parseFormatted(String s) {
        return null;
    }

    private static FVal_LST parseList(String s) {
        return null;
    }
}
