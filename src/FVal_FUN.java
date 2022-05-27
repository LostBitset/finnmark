public class FVal_FUN implements FVal {
    public String[] args;
    public FVal body;

    public FVal_FUN(String[] args, FVal body) {
        this.args = args;
        this.body = body;
    }
}
