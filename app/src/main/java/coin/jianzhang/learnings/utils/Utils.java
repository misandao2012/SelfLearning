package coin.jianzhang.learnings.utils;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jianzhang on 10/17/15.
 */
public class Utils {

    private static final String TAG = "CreditCards";

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //parse the date time to long for comparison
    public static long parseDateTime(String original, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        Date date;
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = dateFormat.parse(original);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
        return date.getTime();
    }

    public static String formatExpireDate(String original) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(original);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        dateFormat = new SimpleDateFormat("MM / yy", Locale.US);
        return dateFormat.format(date);
    }

    //create more space for the card number
    public static String formatCardNumber(String original) {
        return original.replaceAll(" ", "   ");
    }
}
