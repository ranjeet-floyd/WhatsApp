package in.istore.bitblue.app.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static String convertToStringDateAndTime(Date todayDate) {

        String dateFormat = "dd/MM/yyyy hh:mm a";

        DateFormat dFormat = new SimpleDateFormat(dateFormat);
        if (todayDate != null)
            return dFormat.format(todayDate);
        else return null;
    }

    public static String convertToStringDateOnly(Date todayDate) {

        String dateFormat = "yyyy-MM-dd";

        DateFormat dFormat = new SimpleDateFormat(dateFormat);
        if (todayDate != null)
            return dFormat.format(todayDate);
        else return null;
    }

    public static String convertFromDD_MM_YYYYtoYYYY_MM_DD(String date_in_dd_mm_yyyy) {
        String dd_mm_yyyy = "dd/MM/yyyy";
        String yyyy_mm_dd = "yyyy-MM-dd";

        Date initDate = null;
        SimpleDateFormat formatter = null;
        try {
            initDate = new SimpleDateFormat(dd_mm_yyyy).parse(date_in_dd_mm_yyyy);
            formatter = new SimpleDateFormat(yyyy_mm_dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (formatter != null) {
            return formatter.format(initDate);
        } else {
            return null;
        }
    }

    public static String convertFromYYYY_MM_DDtoDD_MM_YYYY(String date_in_yyyy_mm_dd) {
        String dd_mm_yyyy = "dd/MM/yyyy";
        String yyyy_mm_dd = "yyyy-MM-dd";

        Date initDate = null;
        SimpleDateFormat formatter = null;
        try {
            initDate = new SimpleDateFormat(yyyy_mm_dd).parse(date_in_yyyy_mm_dd);
            formatter = new SimpleDateFormat(dd_mm_yyyy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (formatter != null) {
            return formatter.format(initDate);
        } else {
            return null;
        }
    }

}
