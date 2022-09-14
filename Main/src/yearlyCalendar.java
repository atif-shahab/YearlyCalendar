import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;


// https://www.islamicfinder.us/index.php/api/prayer_times?country=US&zipcode=94582&latitude=37.7767&longitude=-121.9692&method=2&juristic=1&date=1663027200
// https://www.islamicfinder.us/index.php/api/calendar?day=1&month=1&year=2023&convert_to=0
class yearlyCalendar {
    public static void main(String[] args) {
        String calEndPoint = "https://www.islamicfinder.us/index.php/api/prayer_times?";
        String hijriEndPoint = "https://www.islamicfinder.us/index.php/api/calendar?";
        String country = "US";
        String zipCode = "94582";
        String latitude = "37.7767";
        String longitude = "-121.9692";
        String method = "2";
        String juristic = "1";
        String timeFormat = "0";
        String timezone = "America/Los_Angeles";

        int year = 2023;
        ArrayList<Day> daysInYear = Days.getDays(year);

        daysInYear.forEach((n) -> System.out.println(n));

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("country", country);
        requestParams.put("zipcode", zipCode);
        requestParams.put("latitude", latitude);
        requestParams.put("longitude", longitude);
        requestParams.put("method", method);
        requestParams.put("juristic", juristic);
        requestParams.put("time_format", timeFormat);
        requestParams.put("timezone", timezone);
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Gregorian Date\tHijri Date\tFajr Adhan\tDuha\tDhuhr " +
                "Adhan\tDhuhr Iqama\tAsr Adhan\tMaghrib Adhan\tIsha Adhan");
        daysInYear.forEach((day) -> {
            requestParams.put("date", day.getEpochInSec().toString());

            //System.out.println(encodedURL);
            try {
                //String resp = HTTPGet.sendGET(encodedURL);
                day.setTiming(((RemoteTimeResult) mapper
                        .readerFor(RemoteTimeResult.class)
                        .readValue(HTTPGet
                                .sendGET(CalendarURL
                                        .getTimingURL(calEndPoint, requestParams))))
                        .getTiming());
                day.setHijriDate(((RemoteHijriResult) mapper
                        .readerFor(RemoteHijriResult.class)
                        .readValue(HTTPGet
                                .sendGET(HijriURL
                                        .getHijriURL(hijriEndPoint, day))))
                        .getHijriDate());
                day.getTiming().setDhuhrIqama(getDhuhrIqama(day, timezone));
                System.out.println(day.tabularPrint());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        }

        private static String getDhuhrIqama(Day day, String timeZone) {
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            if(tz.inDaylightTime(Date.from(day.getDate().atStartOfDay(tz.toZoneId()).plusHours(12).toInstant()))) {
                return "13:30";
            } else if (day.getDate().getDayOfWeek() == DayOfWeek.FRIDAY) return "13:30";
            else {
                LocalTime start = LocalTime.parse(day.getTiming().dhuhr);
                LocalTime iqama = LocalTime.parse("12:30");
                int startInMin = start.getHour()*60 + start.getMinute();
                int iqamaInMin = iqama.getHour()*60 + iqama.getMinute();
                if(iqamaInMin-startInMin < 10)
                    return "12:40";
                else
                    return "12:30";
            }
        }
    }


