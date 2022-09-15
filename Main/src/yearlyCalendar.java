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
        String lastFajrIqama = "6:30";
        String lastDhuhrIqama = "12:30";
        String lastAsrIqama = "15:20";
        String lastIshaIqama = "19:30";

        int year = 2023;
        List<Day> daysInYear = Days.getDays(year);

        //daysInYear.forEach((n) -> System.out.println(n));

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

        System.out.println("\"Gregorian Date\"\t\"Hijri Date\"\t" +
                "\"Fajr Adhan\"\tDuha\t" +
                "\"Dhuhr Adhan\"\t\"Dhuhr Iqama\"\t" +
                "\"Asr Adhan\"\t\"Asr Iqama\"\t" +
                "\"Maghrib Adhan\"\t" +
                "\"Isha Adhan\"");


        daysInYear.forEach((day) -> {
            requestParams.put("date", day.getEpochInSec().toString());
            try {
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            while(daysInYear.get(0).getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
                Day day = daysInYear.remove(0);
                day.getTiming().setDhuhrIqama(lastDhuhrIqama);
                day.getTiming().setAsrIqama(lastAsrIqama);
                System.out.println(day.tabularPrint());
            }

            WeekList weekList = new WeekList(daysInYear);
            Iterator<List<Day>> it = weekList.iterator();
            List<Day> prevWeek = null;
            while(it.hasNext()) {
                List<Day> thisWeek = it.next();
                if(isDayLightWeek(thisWeek, timezone)) {
                    Day saturday = thisWeek.remove(0);
                    saturday.getTiming().setDhuhrIqama(prevWeek.get(0).getTiming().dhuhrIqama);
                    saturday.getTiming().setAsrIqama(prevWeek.get(0).getTiming().asrIqama);
                    setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, timezone));
                    setAsrIqama(thisWeek, getAsrIqama(thisWeek));
                } else {
                    setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, timezone));
                    setAsrIqama(thisWeek, getAsrIqama(thisWeek));
                }
                prevWeek = thisWeek;
            }
            daysInYear.forEach(day -> System.out.println(day.tabularPrint()));
        }

        private static LocalTime getDhuhrIqama(List<Day> days, String timeZone) {
            TimeZone tz = TimeZone.getTimeZone(timeZone);

            List<LocalTime> iqamaTimeForEachDay = new ArrayList<>();
            for(Day day : days) {
                if (tz.inDaylightTime(Date
                                        .from(day.getDate()
                                                .atStartOfDay(tz.
                                                        toZoneId())
                                                .plusHours(12)
                                                .toInstant()))) {
                    iqamaTimeForEachDay.add(LocalTime.parse("13:30"));
                } else if (day.getDate().getDayOfWeek() == DayOfWeek.FRIDAY) {//do nothing;
                }
                else {
                    LocalTime start = LocalTime.parse(day.getTiming().dhuhr);
                    LocalTime iqama = LocalTime.parse("12:30");
                    int startInMin = start.getHour() * 60 + start.getMinute();
                    int iqamaInMin = iqama.getHour() * 60 + iqama.getMinute();
                    if (iqamaInMin - startInMin < 10)
                        iqamaTimeForEachDay.add(LocalTime.parse("12:35"));
                    else
                        iqamaTimeForEachDay.add(LocalTime.parse("12:30"));
                }
            }
            Collections.sort(iqamaTimeForEachDay, (a,b) -> a.compareTo(b));
            return iqamaTimeForEachDay.get(iqamaTimeForEachDay.size()-1);
        }

        private static LocalTime getAsrIqama(List<Day> days) {
            Iterator<Day> it = days.iterator();
            String retVal = null;

            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().asr);
                int iqamaInMin = startInMin + 10 + 3;
                iqamaInMin = Math.round((float)iqamaInMin/10)*10;
                iqamaTimeForEachDay.put(new Integer(iqamaInMin)
                        , toTime(iqamaInMin));
            }

            Map.Entry<Integer, LocalTime> validIqamaTime = null;
            for(Map.Entry<Integer, LocalTime> entry : iqamaTimeForEachDay.entrySet()) {
                boolean foundIt = true;
                for(Day day: days) {
                    int startInMin = toMinutes(day.getTiming().asr);
                    if(entry.getKey() - startInMin < 0) {
                        foundIt = false;
                        break;
                    }
                }
                if(foundIt) {
                    validIqamaTime = entry;
                    break;
                }
            }
            return validIqamaTime.getValue();
        }

        private static int toMinutes(String time) {
            LocalTime start = LocalTime.parse(time);
            return start.getHour() * 60 + start.getMinute();
        }

        private static LocalTime toTime(int minutes) {
            LocalTime retVal = null;
            if(minutes%60 != 0)
                retVal = LocalTime.parse(minutes/60 +":" + minutes%60);
            else
               retVal =  LocalTime.parse(minutes/60 +":" + minutes%60 + "0");
            return retVal;
        }

        private static List<Day> setDhuhrIqama(List<Day> days, LocalTime iqamaTime) {
            days.forEach(day -> {
                if(day.getDate().getDayOfWeek() == DayOfWeek.FRIDAY)
                    day.getTiming().setDhuhrIqama("13:30");
                else
                    day.getTiming().setDhuhrIqama(iqamaTime.toString());
            });
            return days;
        }

        private static List<Day> setAsrIqama(List<Day> days, LocalTime iqamaTime) {
            days.forEach(day -> {
                day.getTiming().setAsrIqama(iqamaTime.toString());
            });
            return days;
        }

        private static boolean isDayLightWeek(List<Day> days, String timeZone) {
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            if(days.size() < 6)
                throw new RuntimeException("Cannot have " + days.size() + " days in a week");
            boolean result = tz.inDaylightTime(Date.from(days
                    .get(0)
                    .getDate()
                    .atStartOfDay(tz.toZoneId())
                    .plusHours(12)
                    .toInstant()))
                    ^ tz.inDaylightTime(Date.from(days
                    .get(1)
                    .getDate()
                    .atStartOfDay(tz.toZoneId())
                    .plusHours(12)
                    .toInstant()));

            for(int i = 2; i < days.size()-1; i++)
                result ^= tz.inDaylightTime(Date.from(days
                                                    .get(i)
                                                    .getDate()
                                                    .atStartOfDay(tz.toZoneId())
                                                    .plusHours(12)
                                                    .toInstant()));
            return result;
        }
    }


