import java.util.ArrayList;


// https://www.islamicfinder.us/index.php/api/prayer_times?country=US&zipcode=94582&latitude=37.7767&longitude=-121.9692&method=2&juristic=1&date=1663027200
class yearlyCalendar {
    public static void main(String[] args) {
        String endpoint = "https://www.islamicfinder.us/index.php/api/prayer_times";
        String country = "US";
        String zipCode = "94582";
        String latitude= "37.7767";
        String longitutde = "-121.9692";
        String method = "2";
        String juristic = "1";

        int year = 2024;
        ArrayList<Day> daysInYear = Days.getDays(year);

        daysInYear.forEach((n) -> System.out.println(n) );
    }
}
