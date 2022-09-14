import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RemoteHijriResult {
    //TODO:  will this field come in handy for dubugging?
    //public String from;
    public String to;
    public boolean success;

    public String getHijriDate() {
        if(success) {
            return this.to;
        } else {
            throw new RuntimeException("Call to convert to Hijri date failed");
        }
    }
}
