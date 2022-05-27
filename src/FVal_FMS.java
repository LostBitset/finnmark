public class FVal_FMS implements FVal {
    public String u;
    public FVal[] items;

    public FVal_FMS(String u) { this.u = u; }

    public String toString()
    { return String.format("[fms]%s", this.u); }
}
