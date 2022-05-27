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
                return parseSymb(s);
        }
    }
}
