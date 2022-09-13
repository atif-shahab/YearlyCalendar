import java.util.ArrayList;

class yearlyCalendar {
    public static void main(String[] args) {
        int year = 2024;
        ArrayList<Day> daysInYear = Days.getDays(year);

        daysInYear.forEach((n) -> System.out.println(n) );
    }
}
