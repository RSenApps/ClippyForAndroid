package wei.mark.example;

import wei.mark.standout.StandOutWindow;
import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class MyAccessibilityService extends AccessibilityService {

	private static final String TAG = "ClippyAccessibility";
	private static final String[] hotphrases = new String[] {"attach"};
	private String getEventType(AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			return "TYPE_NOTIFICATION_STATE_CHANGED";
		case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
			return "TYPE_VIEW_TEXT_CHANGED";
		}
		return "default";
	}

	private String getEventText(AccessibilityEvent event) {
		StringBuilder sb = new StringBuilder();
		for (CharSequence s : event.getText()) {
			sb.append(s);
		}
		return sb.toString();
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		String pkg = event.getPackageName().toString();
		if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)
		{
			Log.v("hotphrase", getEventText(event));
			for (String hotphrase : hotphrases)
			{
				if (getEventText(event).toLowerCase().contains(hotphrase))
				{
					Bundle bundle = new Bundle();
					bundle.putString("hotphrase", hotphrase);
		    		StandOutWindow.show(this, ClippyWindow.class, 0);
					StandOutWindow.sendData(this, ClippyWindow.class, 0, 5, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);

				}
			}
			
		}
		else if (!pkg.equals(getPackageName()))
		{
			Notification notification = (Notification) event.getParcelableData();
			if (notification == null)
			{
				return;
			}
			String content = event.getText().toString();
			// UI Stuff
			try {
				if (content.startsWith("[") && content.endsWith("]")) {
					content = content.substring(1, content.length() - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Bundle bundle = new Bundle();
	    	bundle.putString("title", getTitleFromPackage(event.getPackageName().toString()));
	    	bundle.putString("desc", content);
	    	bundle.putParcelable("intent", notification.contentIntent);
	    	bundle.putString("pkg", pkg);
	    	bundle.putString("name", getTitleFromPackage(pkg));
    		StandOutWindow.show(this, ClippyWindow.class, 0);
	    	StandOutWindow.sendData(this, ClippyWindow.class, 0, 0, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
		
			//Toast.makeText(this, "Notification: " +  + content, Toast.LENGTH_SHORT).show();
		}
	}
	private String getTitleFromPackage(String pkg) {
		final PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(pkg, 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
		return applicationName;
	}
	@Override
	public void onInterrupt() {
		Log.v(TAG, "onInterrupt");
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Log.v(TAG, "onServiceConnected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT;
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
		// Set the type of feedback your service will provide.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
				} else {
					info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
				}

				// Default services are invoked only if no package-specific ones are
				// present
				// for the type of AccessibilityEvent generated. This service *is*
				// application-specific, so the flag isn't necessary. If this was a
				// general-purpose service, it would be worth considering setting the
				// DEFAULT flag.

				// info.flags = AccessibilityServiceInfo.DEFAULT;

				info.notificationTimeout = 100;
		setServiceInfo(info);
	}
	/**
	 * Check if Accessibility Service is enabled.
	 * 
	 * @param mContext
	 * @return <code>true</code> if Accessibility Service is ON, otherwise
	 *         <code>false</code>
	 */
	public static boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		final String service = "com.mytest.accessibility/com.mytest.accessibility.MyAccessibilityService";

		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (SettingNotFoundException e) {
			Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

		if (accessibilityEnabled == 1) {
			Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
					if (accessabilityService.equalsIgnoreCase(service)) {
						Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
		}

		return accessibilityFound;
	}

}