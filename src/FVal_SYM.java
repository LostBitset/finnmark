public class FVal_SYM implements FVal {
    public String uName;

    public FVal_SYM(String uName) { this.uName = uName; }

    public String toString()
    { return String.format("[sym]%s", this.uName); }
}
