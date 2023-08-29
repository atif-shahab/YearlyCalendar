import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@CommandLine.Command(name = "YearlyCalendar", mixinStandardHelpOptions = true,
            description = "Prints the yearly salah calendar")
class yearlyCalendar implements Runnable {
    @CommandLine.Option(names={"-y", "--year"}, required = true
            , description = "Gregorian year for which to calculate the salah calendar")
    int year;
    @CommandLine.Option(names={"-f", "--file"}, required = true,
                description = "File with properties")
    String filename;

    public static void main (String[] args) {
        System.exit(new CommandLine(new yearlyCalendar()).execute(args));
    }
    public  void run() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(filename)) {
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // get the property value
       // String calEndPoint = prop.getProperty("calendar.EndPoint");
       // String hijriEndPoint = prop.getProperty("hijri.EndPoint");
       // String country = prop.getProperty("country");
        //String zipCode = prop.getProperty("zipCode");
        //String latitude = prop.getProperty("latitude");
        //String longitude = prop.getProperty("longitude");
        //String method = prop.getProperty("method");
        //String juristic = prop.getProperty("juristic");
        //String timeFormat = prop.getProperty("timeFormat");
        //String timezone = prop.getProperty("timezone");
        //String initFajrIqama = prop.getProperty("initial.FajrIqama");
        //String initDhuhrIqama = prop.getProperty("initial.DhuhrIqama");
        //String initAsrIqama = prop.getProperty("initial.AsrIqama");
        //String initIshaIqama = prop.getProperty("initial.IshaIqama");
        //int fajrIqamaOffsetOutsideRamadhan =
                //Integer.parseInt(prop.getProperty("fajrIqamaOffset.outsideRamadhan"));
        //int fajrIqamaOffsetInRamdhan =
                //Integer.parseInt(prop.getProperty("fajrIqamaOffset.inRamadhan"));



        // get the property value and print it out
        Settings settings = new Settings.Builder()
                .calEndPoint(prop.getProperty("calendar.EndPoint"))
                .hijriEndPoint(prop.getProperty("hijri.EndPoint"))
                .country(prop.getProperty("country"))
                .zipCode(prop.getProperty("zipCode"))
                .latitude(prop.getProperty("latitude"))
                .longitude(prop.getProperty("longitude"))
                .method(prop.getProperty("method"))
                .juristic(prop.getProperty("juristic"))
                .timeFormat(prop.getProperty("timeFormat"))
                .timezone(prop.getProperty("timezone"))
                .initFajrIqama(prop.getProperty("initial.FajrIqama"))
                .initDhuhrIqama(prop.getProperty("initial.DhuhrIqama"))
                .initAsrIqama(prop.getProperty("initial.AsrIqama"))
                .initIshaIqama(prop.getProperty("initial.IshaIqama"))
                .fajrIqamaOffsetOutsideRamadhan(
                        Integer.parseInt(prop.getProperty("fajrIqamaOffset.outsideRamadhan")))
                .fajrIqamaOffsetInRamadhan(
                        Integer.parseInt(prop.getProperty("fajrIqamaOffset.inRamadhan")))
                .build();

        System.out.println("\"Gregorian Date\"\t\"Hijri Date\"\t" +
                "\"Fajr Adhan\"\t\"Fajr Iqama\"\t" +
                "\"Duha\"\t" +
                "\"Dhuhr Adhan\"\t\"Dhuhr Iqama\"\t" +
                "\"Asr Adhan\"\t\"Asr Iqama\"\t" +
                "\"Maghrib Adhan\"\t" +
                "\"Isha Adhan\"\t\"Isha Iqama\"");


        List<Day> daysInYear = buildCalendarFromRemote(settings, year);

        List<Day> prevWeek = new ArrayList<> ();
        while(daysInYear.get(0).getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
                Day day = daysInYear.remove(0);
                day.getTiming().setFajrIqama(settings.initFajrIqama);
                day.getTiming().setDhuhrIqama(settings.initDhuhrIqama);
                day.getTiming().setAsrIqama(settings.initAsrIqama);
                day.getTiming().setIshaIqama(settings.initIshaIqama);
                prevWeek.add(day);
                System.out.println(day.tabularPrint());
        }

        WeekList weekList = new WeekList(daysInYear);
        Iterator<List<Day>> it = weekList.iterator();

        while(it.hasNext()) {
            List<Day> thisWeek = it.next();
            if(isDayLightWeek(thisWeek, settings.timezone)) {
                iqamaForDayLightStartWeek(settings.timezone, prevWeek, thisWeek, settings.fajrIqamaOffsetInRamdhan);
            } else {
                setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, settings.timezone));
                setAsrIqama(thisWeek, getAsrIqama(thisWeek));
                if(startOfRamadhan(thisWeek)) {
                    adjustForStartOfRmadhan(thisWeek, settings.fajrIqamaOffsetInRamdhan);
                } else if(endOfRamadhan(thisWeek)) {
                    LocalTime ishaIqamaBeforeRamadhan = getIshaIqama(thisWeek, 10, false);
                    LocalTime ishaIqamaInRamadhan = getIshaIqama(thisWeek, 20, true);
                    LocalTime fajrIqamaInRamadhan = getFajrIqama(thisWeek, settings.fajrIqamaOffsetInRamdhan, true);
                    //TODO:  This can result in undesirable case where time changes for few days before
                    // changing again
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
                        day.getTiming().setFajrIqama(fajrIqamaInRamadhan.toString());
                    }
                }
                else if(isRamadhanWeek(thisWeek)) {
                    setIshaIqama(thisWeek, getIshaIqama(thisWeek, 20, true));
                    setFajrIqama(thisWeek, getFajrIqama(thisWeek, settings.fajrIqamaOffsetInRamdhan, true));
                }
                else {
                    setIshaIqama(thisWeek, getIshaIqama(thisWeek, 10, false));
                    if(isShawwalButLessThanEleventh(thisWeek))
                        setFajrIqama(thisWeek, getFajrIqama(thisWeek, settings.fajrIqamaOffsetInRamdhan, true));
                    else
                        setFajrIqama(thisWeek, getFajrIqama(thisWeek, settings.fajrIqamaOffsetOutsideRamadhan, false));
                }
            }
            prevWeek = thisWeek;
        }
        daysInYear.forEach(day -> System.out.println(day.tabularPrint()));
    }

    private static void adjustForStartOfRmadhan(List<Day> thisWeek, int fajrIqamaOffsetInRamdhan) {
        LocalTime ishaIqamaBeforeRamadhan = getIshaIqama(thisWeek, 10, false);
        LocalTime ishaIqamaaInRamadhan = getIshaIqama(thisWeek, 20, true);
        LocalTime fajrIqamaBeforeRamadhan = getFajrIqama(thisWeek, 30, false);
        LocalTime fajrIqamaInRamadhan = getFajrIqama(thisWeek, fajrIqamaOffsetInRamdhan, true);

        for(Day day : thisWeek) {
            switch (day.getHijriMonth()) {
                case 8 -> {
                    day.getTiming().setIshaIqama(ishaIqamaBeforeRamadhan.toString());
                    day.getTiming().setFajrIqama(fajrIqamaBeforeRamadhan.toString());
                }
                case 9 -> {
                    day.getTiming().setIshaIqama(ishaIqamaaInRamadhan.toString());
                    day.getTiming().setFajrIqama(fajrIqamaInRamadhan.toString());
                }
                default ->
                    throw new IllegalStateException("Invalid hijri month (expecting 8 or 9): "
                            + day.getHijriDate());
            }
        }
    }

    private static void iqamaForDayLightStartWeek(String timezone, List<Day> prevWeek, List<Day> thisWeek,
                                                  int fajrIqamaOffsetInRamdhan) {
        //TODO:  Edge case where the start of Ramadhan coincides with the week of time change
        Day saturday = thisWeek.remove(0);
        saturday.getTiming().setFajrIqama(prevWeek.get(0).getTiming().fajrIqama);
        saturday.getTiming().setDhuhrIqama(prevWeek.get(0).getTiming().dhuhrIqama);
        saturday.getTiming().setAsrIqama(prevWeek.get(0).getTiming().asrIqama);
        saturday.getTiming().setIshaIqama((prevWeek.get(0).getTiming().ishaIqama));
        setDhuhrIqama(thisWeek, getDhuhrIqama(thisWeek, timezone));
        setAsrIqama(thisWeek, getAsrIqama(thisWeek));
        if(startOfRamadhan(thisWeek))
            adjustForStartOfRmadhan(thisWeek, fajrIqamaOffsetInRamdhan);
        else if(endOfRamadhan(thisWeek))
            throw new UnsupportedOperationException("end of ramadhan " + thisWeek.get(0));
        else if(isRamadhanWeek(thisWeek))
            throw new UnsupportedOperationException("ramadhan week " + thisWeek.get(0));
        else {
            setIshaIqama(thisWeek, getIshaIqama(thisWeek, 10, false));
            setFajrIqama(thisWeek, getFajrIqama(thisWeek, 30, false));
        }
    }

    private static List<Day> buildCalendarFromRemote(Settings settings,int  year) {
        List<Day> daysInYear = Days.getDays(year);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestParams = getRequestParams(settings);

        daysInYear.forEach((day) -> {
            requestParams.put("date", day.getEpochInSec().toString());
            try {
                day.setTiming(((RemoteTimeResult) mapper
                        .readerFor(RemoteTimeResult.class)
                        .readValue(HTTPGet
                                .sendGET(CalendarURL
                                        .getTimingURL(settings.calEndPoint, requestParams))))
                        .getTiming());
                day.setHijriDate(((RemoteHijriResult) mapper
                        .readerFor(RemoteHijriResult.class)
                        .readValue(HTTPGet
                                .sendGET(HijriURL
                                        .getHijriURL(settings.hijriEndPoint, day))))
                        .getHijriDate());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return daysInYear;
    }

    private static Map<String, String> getRequestParams(Settings settings) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("country", settings.country);
        requestParams.put("zipcode", settings.zipCode);
        requestParams.put("latitude", settings.latitude);
        requestParams.put("longitude", settings.longitude);
        requestParams.put("method", settings.method);
        requestParams.put("juristic", settings.juristic);
        requestParams.put("time_format", settings.timeFormat);
        requestParams.put("timezone", settings.timezone);
        return requestParams;
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
            iqamaTimeForEachDay.sort(Comparator.naturalOrder());
            return iqamaTimeForEachDay.get(iqamaTimeForEachDay.size()-1);
        }

        private static LocalTime getAsrIqama(List<Day> days) {
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            //TODO:  Extract the following as a method since it is common to both isha and asr methods
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().asr);
                int iqamaInMin = startInMin + 3;
                iqamaInMin = (int) (Math.ceil((float)iqamaInMin/10)*10);
                iqamaTimeForEachDay.put(iqamaInMin
                        , toLocalTime(iqamaInMin));
            }

            return getValidIqamaTime(days, iqamaTimeForEachDay, "asr", false);
        }

        private static LocalTime getIshaIqama(List<Day> days, int timeAfter, boolean isRamadhan) {
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().isha);
                int iqamaInMin = startInMin + timeAfter;
                if(isRamadhan)
                    iqamaInMin = (int) Math.ceil((float) iqamaInMin / 10) * 10;
                else iqamaInMin = Math.round((float) iqamaInMin / timeAfter) * timeAfter;
                iqamaTimeForEachDay.put(iqamaInMin
                        , toLocalTime(iqamaInMin));
            }

            LocalTime validIqamaTime = getValidIqamaTime(days, iqamaTimeForEachDay, "isha", isRamadhan);
            LocalTime sevenThirty = LocalTime.parse("19:30");
            LocalTime tenTen = LocalTime.parse("22:10");
            if(validIqamaTime != null) {
                if(!isRamadhan) {
                    if (validIqamaTime.isBefore(sevenThirty))
                        return sevenThirty;
                    else if (validIqamaTime.isAfter(tenTen))
                        return tenTen;
                    else
                        return validIqamaTime;
                } else
                    return validIqamaTime;
            } else
                throw new IllegalStateException("could not calculate valid Isha Iqama time for the week of "
                                                            + days.get(0));
        }

        private static LocalTime getFajrIqama(List<Day> days, int timeAfter, boolean isRamadhan) {
            SortedMap<Integer, LocalTime> iqamaTimeForEachDay = new TreeMap<>();
            for(Day day : days) {
                int startInMin = toMinutes(day.getTiming().fajr);
                int iqamaInMin = startInMin + timeAfter;

                iqamaInMin = Math.round((float) iqamaInMin / 10) * 10;
                iqamaTimeForEachDay.put(iqamaInMin
                        , toLocalTime(iqamaInMin));
            }

            LocalTime validIqamaTime = getValidIqamaTime(days, iqamaTimeForEachDay, "fajr", isRamadhan);
            LocalTime five = LocalTime.parse("05:00");
            LocalTime sixThirty = LocalTime.parse("06:30");
            if(validIqamaTime != null) {
                if(!isRamadhan) {
                    if (validIqamaTime.isBefore(five))
                        return five;
                    else if (validIqamaTime.isAfter(sixThirty))
                        return sixThirty;
                    else
                        return validIqamaTime;
                } else
                    return validIqamaTime;
            } else
                throw new IllegalStateException("could not calculate valid Fajr Iqama time for the week of "
                        + days.get(0));
        }

        private static LocalTime getValidIqamaTime(List<Day> forDays
                                    , Map<Integer, LocalTime> iqamaTimes, String forSalah, boolean isRamadhan) {
            Map.Entry<Integer, LocalTime> validIqamaTime = null;
            if(forSalah.compareTo("fajr") == 0)  {
                return findOptimalFajrIqama(forDays, iqamaTimes, isRamadhan);
            } else {
                for (Map.Entry<Integer, LocalTime> entry : iqamaTimes.entrySet()) {
                    boolean foundIt = true;
                    for (Day day : forDays) {
                        int startInMin;
                        switch (forSalah) {
                            case "asr" -> startInMin = toMinutes(day.getTiming().asr);
                            case "isha" -> startInMin = toMinutes(day.getTiming().isha);
                            default -> throw new IllegalStateException("unknow salah type " + forSalah);
                        }
                        if (entry.getKey() - startInMin < 3) {
                            foundIt = false;
                            break;
                        }
                    }
                    if (foundIt) {
                        validIqamaTime = entry;
                        break;
                    }
                }
                if (validIqamaTime != null)
                    return validIqamaTime.getValue();
                else
                    throw new IllegalStateException("unable to compute a valid iqama");
            }
        }

    private static LocalTime findOptimalFajrIqama(List<Day> forDays, Map<Integer, LocalTime> iqamaTimes, boolean isRamadhan) {
        SortedMap<Integer, LocalTime> errors = new TreeMap<>();
        for(Map.Entry<Integer, LocalTime> entry : iqamaTimes.entrySet()) {
            int error = 0;
            for(Day day: forDays) {
                if(isRamadhan)
                    error += Math.pow(toMinutes(day.getTiming().fajr) + 20 - entry.getKey(), 2);
                else
                    error += Math.pow(toMinutes(day.getTiming().fajr) + 30 - entry.getKey(), 2);
            }
            errors.put(error, entry.getValue());
        }
        return errors.get(errors.firstKey());
    }

    private static int toMinutes(String timeString) {
            LocalTime time = LocalTime.parse(timeString);
            return time.getHour() * 60 + time.getMinute();
        }

        private static LocalTime toLocalTime(int minutes) {
            LocalTime retVal;
            if(minutes%60 != 0) {
                if (minutes / 60 < 10)
                    retVal = LocalTime.parse("0" + minutes / 60 + ":" + minutes % 60);
                else
                    retVal = LocalTime.parse(minutes / 60 + ":" + minutes % 60);
            }
            else {
                if (minutes / 60 < 10)
                    retVal = LocalTime.parse("0" + minutes / 60 + ":00");
                else
                    retVal = LocalTime.parse(minutes / 60 + ":00");
            }
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

    private static List<Day> setFajrIqama(List<Day> days, LocalTime iqamaTime) {
        days.forEach(day -> {
            day.getTiming().setFajrIqama(iqamaTime.toString());
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
            return HijriMonth.fromInt(forDays.get(0).getHijriMonth()) == HijriMonth.Shaaban
                    && HijriMonth.fromInt(forDays.get(forDays.size() - 1).getHijriMonth()) == HijriMonth.Ramadhan;
        }

        private static boolean endOfRamadhan(List<Day> forDays) {
            return HijriMonth.fromInt(forDays.get(0).getHijriMonth()) == HijriMonth.Ramadhan
                    && HijriMonth.fromInt(forDays.get(forDays.size() - 1).getHijriMonth()) == HijriMonth.Shawwal;
        }

        private static boolean isRamadhanWeek(List<Day> forDays) {
            return HijriMonth.fromInt(forDays.get(0).getHijriMonth()) == HijriMonth.Ramadhan
                    && HijriMonth.fromInt(forDays.get(forDays.size() - 1).getHijriMonth()) == HijriMonth.Ramadhan;
        }

        private static boolean isShawwalButLessThanEleventh(List<Day> forDays) {
            return HijriMonth.fromInt(forDays.get(0).getHijriMonth()) == HijriMonth.Shawwal
                    && forDays.get(0).getHijriDay() <= 11;
        }
    }


