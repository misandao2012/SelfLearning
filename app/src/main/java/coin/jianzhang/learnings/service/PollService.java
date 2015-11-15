package coin.jianzhang.learnings.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import coin.jianzhang.learnings.R;
import coin.jianzhang.learnings.ui.MainActivity;

/**
 * Created by jianzhang on 10/24/15.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 1000 * 5;
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String ACTION_SHOW_NOTIFICATION = "coin.jianzhang.creditcards.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE = "coin.jianzhang.creditcards.PRIVATE";

    public PollService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("CreditCards")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smile!")
                .setContentText("You are on camera")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        showBackgroundNotification(0, notification);

    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = new Intent(context, PollService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context).
                edit().
                putBoolean(PollService.PREF_IS_ALARM_ON, isOn).
                commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = new Intent(context, PollService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    void showBackgroundNotification(int requestCode, Notification notification) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra("REQUEST_CODE", requestCode);
        intent.putExtra("NOTIFICATION", notification);

        sendOrderedBroadcast(intent, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }
}
