public class FVal_STR implements FVal {
    public String u;

    public FVal_STR(String u) { this.u = u; }

    public String toString()
    { return String.format("[str]%s", this.u); }
}
