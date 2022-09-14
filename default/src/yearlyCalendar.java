import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



// https://www.islamicfinder.us/index.php/api/prayer_times?country=US&zipcode=94582&latitude=37.7767&longitude=-121.9692&method=2&juristic=1&date=1663027200
class yearlyCalendar {
    public static void main(String[] args) {
        String endpoint = "https://www.islamicfinder.us/index.php/api/prayer_times?";
        String country = "US";
        String zipCode = "94582";
        String latitude = "37.7767";
        String longitude = "-121.9692";
        String method = "2";
        String juristic = "1";
        String timeFormat = "0";

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
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Date\tFajr\tDuha\tDhuhr\tAsr\tMaghrib\tIsha");
        daysInYear.forEach((n) -> {
            requestParams.put("date", n.getEpochInSec().toString());
            URL encodedURL;
            try {
                encodedURL = newURL.getURL(endpoint, requestParams);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //System.out.println(encodedURL);
            try {
                String resp = HTTPGet.sendGET(encodedURL);
                n.setTiming(((RemoteTimeResult) mapper
                        .readerFor(RemoteTimeResult.class)
                        .readValue(resp))
                        .getTiming());
                System.out.println(n.tabularPrint());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        }
    }


