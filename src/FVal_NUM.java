public class FVal_NUM implements FVal {
    public double u;

    public FVal_NUM(double u) { this.u = u; }

    public String toString()
    { return String.format("[num]%.3f", this.u); }
}
