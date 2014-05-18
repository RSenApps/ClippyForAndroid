package wei.mark.example;

import wei.mark.standout.StandOutWindow;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NLService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
    	/*
    	Bundle bundle = new Bundle();
    	bundle.putString("ticker", sbn.getNotification().tickerText.toString());
    	bundle.putString("title", sbn.getNotification().extras.getString(Notification.EXTRA_TITLE));
    	bundle.putString("title")
    	bundle.putParcelable("intent", sbn.getNotification().contentIntent);
    	bundle.putString("pkg", sbn.getPackageName());
    	final PackageManager pm = getApplicationContext().getPackageManager();
    	ApplicationInfo ai;
    	try {
    	    ai = pm.getApplicationInfo( sbn.getPackageName(), 0);
    	} catch (final NameNotFoundException e) {
    	    ai = null;
    	}
    	final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    	bundle.putString("name", applicationName);
    	StandOutWindow.sendData(this, ClippyWindow.class, 0, 0, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
	*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
       
    }
}