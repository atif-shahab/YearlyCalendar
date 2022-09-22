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
        String initFajrIqama = "6:30";
        String initDhuhrIqama = "12:30";
        String initAsrIqama = "15:30";
        String initIshaIqama = "19:30";

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
                "\"Fajr Adhan\"\t\"Fajr Iqama\"\t" +
                "\"Duha\"\t" +
                "\"Dhuhr Adhan\"\t\"Dhuhr Iqama\"\t" +
                "\"Asr Adhan\"\t\"Asr Iqama\"\t" +
                "\"Maghrib Adhan\"\t" +
                "\"Isha Adhan\"\t\"Isha Iqama\"");


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
                day.getTiming().setDhuhrIqama(initDhuhrIqama);
                day.getTiming().setAsrIqama(initAsrIqama);
                day.getTiming().setIshaIqama(initIshaIqama);

                System.out.println(day.tabularPrint());
            }

            WeekList weekList = new WeekList(daysInYear);
            Iterator<List<Day>> it = weekList.iterator();
            List<Day> prevWeek = null;
            while(it.hasNext()) {
                List<Day> thisWeek = it.next();
                if(isDayLightWeek(thisWeek, timezone)) {
                    //TODO:  Edge case where the start of Ramadhan coincides with the week of time change
                    Day saturday = thisWeek.remove(0);
                    saturday.getTiming().setDhuhrIqama(prevWeek.get(0).getTiming().dhuhrIqama);
                    saturday.getTiming().setAsrIqama(prevWeek.get(0).getTiming().asrIqama);
                    saturday.getTiming().setIshaIqama((prevWeek.get(0).getTiming().ishaIqama));
                    setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, timezone));
                    setAsrIqama(thisWeek, getAsrIqama(thisWeek));
                    if(startOfRamadhan(thisWeek))
                        throw new UnsupportedOperationException("start of ramdhan " + thisWeek.get(0));
                    else if(endOfRamadhan(thisWeek))
                        throw new UnsupportedOperationException("end of ramadhan " + thisWeek.get(0));
                    else if(isRamadhanWeek(thisWeek))
                        throw new UnsupportedOperationException("ramadhan week " + thisWeek.get(0));
                    else
                        setIshaIqama(thisWeek, getIshaIqama(thisWeek, 10, false));
                } else {
                    setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, timezone));
                    setAsrIqama(thisWeek, getAsrIqama(thisWeek));
                    if(startOfRamadhan(thisWeek)) {
                        LocalTime ishaIqamaBeforeRamadhan = getIshaIqama(thisWeek, 10, false);
                        LocalTime ishaIqamaaInRamadhan = getIshaIqama(thisWeek, 15, true);

                        for(Day day : thisWeek) {
                            switch (day.getHijriMonth()) {
                                case 8 ->
                                    day.getTiming().setIshaIqama(ishaIqamaBeforeRamadhan.toString());
                                case 9 ->
                                    day.getTiming().setIshaIqama(ishaIqamaaInRamadhan.toString());
                                default ->
                                    throw new IllegalStateException("In valid hijri month (expecting 8 or 9): "
                                            + day.getHijriDate());
                            }
                        }
                    }
                    else if(endOfRamadhan(thisWeek)) {
                        LocalTime ishaIqamaBeforeRamadhan = getIshaIqama(thisWeek, 10, false);
                        LocalTime ishaIqamaInRamadhan = getIshaIqama(thisWeek, 15, true);

                        for (Day day : thisWeek) {
                            switch (day.getHijriMonth()) {
                                case 9 ->
                                    day.getTiming().setIshaIqama(ishaIqamaInRamadhan.toString());
                                case 10 ->
                                    day.getTiming().setIshaIqama(ishaIqamaBeforeRamadhan.toString());
                                default ->
                                    throw new IllegalStateException("In valid hijri month, expecting 9 or 10: "
                                            + day.getHijriDate());
                            }
                        }
                    }
                    else if(isRamadhanWeek(thisWeek))
                        setIshaIqama(thisWeek, getIshaIqama(thisWeek, 15, true));
                    else
                        setIshaIqama(thisWeek, getIshaIqama(thisWeek, 10, false));
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
                } else if (day.getDate().getDayOfWeek() == DayOfWeek.FRIDAY) {
                    //do nothing;
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
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            //TODO:  Extract the following as a method since it is common to both isha and asr mthods
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().asr);
                int iqamaInMin = startInMin + 10 + 3;
                iqamaInMin = Math.round((float)iqamaInMin/10)*10;
                iqamaTimeForEachDay.put(Integer.valueOf(iqamaInMin)
                        , toTime(iqamaInMin));
            }

            LocalTime validIqamaTime = getValidIqamaTime(days, iqamaTimeForEachDay, "asr");
            return validIqamaTime;
        }

        private static LocalTime getIshaIqama(List<Day> days, int timeAfter, boolean isRamadhan) {
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().isha);
                int iqamaInMin = startInMin + timeAfter + 3;
                if(isRamadhan)
                    iqamaInMin = (int) Math.ceil((float) iqamaInMin / timeAfter) * timeAfter;
                else iqamaInMin = Math.round((float) iqamaInMin / timeAfter) * timeAfter;
                iqamaTimeForEachDay.put(Integer.valueOf(iqamaInMin)
                        , toTime(iqamaInMin));
            }

            LocalTime validIqamaTime = getValidIqamaTime(days, iqamaTimeForEachDay, "isha");
            LocalTime sevenThirty = LocalTime.parse("19:30");
            LocalTime tenTen = LocalTime.parse("22:10");
            //TODO:  Might need to adjust for Ramadhan
            if(validIqamaTime != null) {
                if (validIqamaTime.compareTo(sevenThirty) < 0)
                    return sevenThirty;
                else if (validIqamaTime.compareTo(tenTen) > 0)
                    return tenTen;
                else
                    return validIqamaTime;
            } else
                throw new IllegalStateException("could not calculate valid Isha Iqama time for the week of "
                                                            + days.get(0));
        }

        private static LocalTime getFajrIqama(List<Day> days, int timeAfter) {
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().isha);
                int iqamaInMin = startInMin + timeAfter + 3;

                iqamaInMin = Math.round((float) iqamaInMin / timeAfter) * timeAfter;
                iqamaTimeForEachDay.put(Integer.valueOf(iqamaInMin)
                        , toTime(iqamaInMin));
            }

            LocalTime validIqamaTime = getValidIqamaTime(days, iqamaTimeForEachDay, "fajr");
            LocalTime five = LocalTime.parse("05:00");
            LocalTime sixThirty = LocalTime.parse("06:30");
            //TODO:  Might need to adjust for Ramadhan
            if(validIqamaTime != null) {
                if (validIqamaTime.compareTo(five) < 0)
                    return five;
                else if (validIqamaTime.compareTo(sixThirty) > 0)
                    return sixThirty;
                else
                    return validIqamaTime;
            } else
                throw new IllegalStateException("could not calculate valid Isha Iqama time for the week of "
                        + days.get(0));
        }

        private static LocalTime getValidIqamaTime(List<Day> forDays
                                    , Map<Integer, LocalTime> iqamaTimes, String forSalah) {
            Map.Entry<Integer, LocalTime> validIqamaTime = null;
            for(Map.Entry<Integer, LocalTime> entry : iqamaTimes.entrySet()) {
                boolean foundIt = true;
                for(Day day: forDays) {
                    int startInMin = 0;
                    switch (forSalah) {
                        case "asr" -> startInMin = toMinutes(day.getTiming().asr);
                        case "isha" -> startInMin = toMinutes(day.getTiming().isha);
                        case "fajr" -> {
                            throw new UnsupportedOperationException("Fajr salah calculations not implemented");
                        }
                        default -> throw new IllegalStateException("unknow salah type " + forSalah);
                    }
                    if(entry.getKey() - startInMin < 3) {
                        foundIt = false;
                        break;
                    }
                }
                if(foundIt) {
                    validIqamaTime = entry;
                    break;
                }
            }
            if(validIqamaTime != null)
                return validIqamaTime.getValue();
            else
                return null;
        }
        private static int toMinutes(String timeString) {
            LocalTime time = LocalTime.parse(timeString);
            return time.getHour() * 60 + time.getMinute();
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

        private static List<Day> setIshaIqama(List<Day> days, LocalTime iqamaTime) {
            days.forEach(day -> {
                day.getTiming().setIshaIqama(iqamaTime.toString());
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

        private static boolean startOfRamadhan(List<Day> forDays) {
            if(forDays.get(0).getHijriMonth() == 8
                    && forDays.get(forDays.size()-1).getHijriMonth() == 9) return true;
            else return false;
        }

        private static boolean endOfRamadhan(List<Day> forDays) {
            if(forDays.get(0).getHijriMonth() == 9
                    && forDays.get(forDays.size()-1).getHijriMonth() == 10) return true;
            else return false;
        }

        private static boolean isRamadhanWeek(List<Day> forDays) {
            if(forDays.get(0).getHijriMonth() == 9
                    && forDays.get(forDays.size()-1).getHijriMonth() == 9) return true;
            else return false;
        }
    }


