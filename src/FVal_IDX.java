public class FVal_IDX implements FVal {
    public int u;

    public FVal_IDX(int u) { this.u = u; }

    public String toString()
    { return String.format("[idx]%d", this.u); }
}
