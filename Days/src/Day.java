import java.time.LocalDate;

public class Day {
    private LocalDate date;
    private Long epochInSec;

    private Timings timing;
    private String hijriDate;
    public String getHijriDate() {
        return hijriDate;
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
        return date;
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
                + getTiming().asr + "\t"
                + getTiming().maghrib + "\t"
                + getTiming().isha;
    }

}
