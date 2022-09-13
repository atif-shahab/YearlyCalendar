
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class Days {
    public static ArrayList<Day> getDays(int year) {

        ArrayList<Day> days = new ArrayList<>();
        LocalDate date = LocalDate.of(year, 1, 1);
        while(year == date.getYear()) {
            Day day = new Day (date, date.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.MIN));
            days.add(day);
            date = date.plusDays(1);
        }
        return days;
    }
}