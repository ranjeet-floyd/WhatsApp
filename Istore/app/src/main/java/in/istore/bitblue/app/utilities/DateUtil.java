package in.istore.bitblue.app.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
    public static String getStringDate(long todayDate) {

        String dateFormat = "dd/MM/yyyy hh:mm a";

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(todayDate);
        return formatter.format(calendar.getTime());
    }
}
