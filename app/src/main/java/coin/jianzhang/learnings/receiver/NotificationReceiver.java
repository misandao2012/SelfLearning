package coin.jianzhang.learnings.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jianzhang on 10/25/15.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent intent) {
        if (getResultCode() != Activity.RESULT_OK)
            // a foreground activity cancelled the broadcast
            return;

        int requestCode = intent.getIntExtra("REQUEST_CODE", 0);
        Notification notification = intent.getParcelableExtra("NOTIFICATION");

        NotificationManager notificationManager = (NotificationManager)
                c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, notification);
    }
}
