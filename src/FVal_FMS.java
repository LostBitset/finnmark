import java.util.ArrayList;

public class FVal_FMS implements FVal {
    public String u;
    public String[] refs;

    public FVal_FMS(String finnmark_form) {
        ArrayList<String> refs_ = new ArrayList<>();
        StringBuilder uBuilder = new StringBuilder();
        StringBuilder currRef = new StringBuilder();
        boolean refMode = false;
        for (int i = 0; i < finnmark_form.length(); i++) {
            char c = finnmark_form.charAt(i);
            if (refMode) {
                if (c == '%') {
                    refs_.add(currRef.toString());
                    uBuilder.append("%s");
                    currRef = new StringBuilder();
                    refMode = false;
                    continue;
                }
                currRef.append(c);
                continue;
            }
            if (c == '%') refMode = true;
            else uBuilder.append(c);
        }
        this.u = uBuilder.toString();
        this.refs = refs_.toArray(String[]::new);
    }

    public String toString()
    { return String.format("[fms]%s", this.u); }
}
