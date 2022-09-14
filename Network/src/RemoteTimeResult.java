import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RemoteTimeResult {
    @JsonAlias("results")
    public Timings timing;

    public Timings getTiming() {
        return timing;
    }
}
