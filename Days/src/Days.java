
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Days {
    public static List<Day> getDays(int year) {

        ArrayList<Day> days = new ArrayList<>();
        LocalDate date = LocalDate.of(year, 1, 1);

        while(year == date.getYear()) {
            Day day = new Day (date, date.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.MIN));
            days.add(day);
            date = date.plusDays(1);
        }
        while(date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            Day day = new Day (date, date.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.MIN));
            days.add(day);
            date = date.plusDays(1);
        }
        return days;
    }
}
