import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class newURL {
    public static URL getURL(String endpoint, Map<String, String> requestParams) throws Exception{
        String encodedURL = requestParams.keySet().stream()
                .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                .collect(joining("&", endpoint, ""));
        return new URL(encodedURL);
    }

    private static String encodeValue(String value)  {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
