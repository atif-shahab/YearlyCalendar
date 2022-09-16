import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day {
    private LocalDate date;
    private Long epochInSec;

    private Timings timing;
    private String hijriDate;
    public String getHijriDate() {
        return this.hijriDate;
    }

    public void setHijriDate(String hijriDate) {
        this.hijriDate = hijriDate;
    }

    public void setTiming(Timings timing) {
        this.timing = timing;
    }
    public Timings getTiming() {
        return timing;
    }
    public Day (LocalDate date, Long epochInSec) {
        setDate(date);
        setEpochInSec(epochInSec);
    }
    private void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    private void setEpochInSec(Long epochInSec) {
        this.epochInSec = epochInSec;
    }

    public Long getEpochInSec() {
        return epochInSec;
    }

    public String toString() {
        return getDate() + ": " + getEpochInSec() + " : " + getTiming();
    }

    public String tabularPrint() {
        return getDate() + "\t"
                + getHijriDate() + "\t"
                + getTiming().fajr + "\t"
                + getTiming().duha + "\t"
                + getTiming().dhuhr + "\t"
                + getTiming().dhuhrIqama + "\t"
                + getTiming().asr + "\t"
                + getTiming().asrIqama + "\t"
                + getTiming().maghrib + "\t"
                + getTiming().isha + "\t"
                + getTiming().ishaIqama;
    }

    public int getHijriMonth() {
        Pattern pattern = Pattern.compile("-(.*?)-");
        Matcher matcher = pattern.matcher(getHijriDate());
        if(matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else
            throw new RuntimeException("Invalid Month in hijri date " + getHijriDate());
    }
}
