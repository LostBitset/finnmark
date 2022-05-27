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
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                if (s.charAt(s.length() - 1) == 'a') return parseIdx(s);
                else return parseNum(s);
            default:
                return parseSymb(s);
        }
    }
}
