import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FVal_LST implements FVal {
    public FVal[] u;

    public FVal_LST(FVal[] u) { this.u = u; }

    public String toString() {
        return String.format(
            "[lst](%s)",
            Stream.of(this.u).map(String::valueOf).collect(Collectors.joining(" "))
        );
    }
}
