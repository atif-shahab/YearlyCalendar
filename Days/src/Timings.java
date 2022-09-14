import com.fasterxml.jackson.annotation.JsonAlias;

public class Timings {
    @JsonAlias("Fajr")
    public String fajr;
    @JsonAlias("Duha")
    public String duha;
    @JsonAlias("Dhuhr")
    public String dhuhr;
    @JsonAlias("Asr")
    public String asr;
    @JsonAlias("Maghrib")
    public String maghrib;
    @JsonAlias("Isha")
    public String isha;
}
