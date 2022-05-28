public class FVal_QTD implements FVal {
    public FVal inner;

    public FVal_QTD(FVal inner) { this.inner = inner; }

    public String toString()
    { return String.format("[`]%s", this.inner); }
}
