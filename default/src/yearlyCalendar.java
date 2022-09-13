import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;


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

        int year = 2024;
        ArrayList<Day> daysInYear = Days.getDays(year);

        daysInYear.forEach((n) -> System.out.println(n));

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("country", country);
        requestParams.put("zipcode", zipCode);
        requestParams.put("latitude", latitude);
        requestParams.put("longitude", longitude);
        requestParams.put("method", method);
        requestParams.put("juristic", juristic);

        daysInYear.forEach((n) -> {
            requestParams.put("date", n.getEpochInSec().toString());
            String encodedURL = requestParams.keySet().stream()
                    .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                    .collect(joining("&", endpoint, ""));

            System.out.println(encodedURL);
        });

    }

    private static String encodeValue(String value) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
