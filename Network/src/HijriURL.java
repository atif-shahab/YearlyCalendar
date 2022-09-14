import java.net.URL;

public class HijriURL {
    public static URL getHijriURL(String endpoint, Day day) throws Exception{
        String encodedURL = endpoint
                + "day=" + day.getDate().getDayOfMonth()
                + "&" + "month=" + day.getDate().getMonth().getValue()
                + "&" + "year=" + day.getDate().getYear()
                + "&" + "convert_to=0";
        return new URL(encodedURL);
    }
}
