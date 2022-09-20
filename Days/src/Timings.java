import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Timings {
    @JsonAlias("Fajr")
    public String fajr;

    public void setFajrIqama(String fajrIqama) {
        this.fajrIqama = fajrIqama;
    }

    @JsonIgnore
    public String fajrIqama;
    @JsonAlias("Duha")
    public String duha;
    @JsonAlias("Dhuhr")
    public String dhuhr;

    public void setDhuhrIqama(String dhuhrIqama) {
        this.dhuhrIqama = dhuhrIqama;
    }

    @JsonIgnore
    public String dhuhrIqama;
    @JsonAlias("Asr")
    public String asr;
    @JsonIgnore
    public String asrIqama;

    public void setAsrIqama(String asrIqama) {
        this.asrIqama = asrIqama;
    }

    @JsonAlias("Maghrib")
    public String maghrib;
    @JsonAlias("Isha")
    public String isha;

    public void setIshaIqama(String ishaIqama) {
        this.ishaIqama = ishaIqama;
    }

    @JsonIgnore
    public String ishaIqama;
}
