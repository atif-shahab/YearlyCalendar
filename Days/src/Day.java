import java.time.LocalDate;

public class Day {
    private LocalDate date;
    private Long epochInSec;

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
        return getDate() + ": " + getEpochInSec();
    }

}
