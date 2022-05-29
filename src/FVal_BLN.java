public class FVal_BLN implements FVal {
    public boolean u;

    public FVal_BLN(boolean u) { this.u = u; }

    public String toString()
    { return this.u ? "#T" : "#F"; }
}
