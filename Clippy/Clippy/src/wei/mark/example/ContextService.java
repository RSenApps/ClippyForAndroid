package wei.mark.example;

import java.util.List;

import wei.mark.standout.StandOutWindow;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.widget.Toast;

public class ContextService extends Service {
	ClipboardManager clipboard;
	int lastMissedCallCount;
	MissedCallsContentObserver mcco;
	IncomingTextMessages incomingTexts;
	String lastPkg = "";
	final Handler handler = new Handler();
	Runnable runnable;
	public class MissedCallsContentObserver extends ContentObserver
	{
	    public MissedCallsContentObserver()
	    {
	        super(null);
	    	lastMissedCallCount = getNumberMissedCalls();
	    }

	    @Override
	    public void onChange(boolean selfChange)
	    {
	        if (lastMissedCallCount < getNumberMissedCalls())
	        {
	        	Cursor cursor = getContentResolver().query(
			            Calls.CONTENT_URI, 
			            null, 
			            Calls.TYPE +  " = ? AND " + Calls.NEW + " = ?", 
			            new String[] { Integer.toString(Calls.MISSED_TYPE), "1" }, 
			            Calls.DATE + " DESC ");
	        	  int numberColumnId = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
	              int contactNameId = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
	              cursor.moveToFirst();
	              String contactNumber = cursor.getString(numberColumnId);
	             String contactName = cursor.getString(contactNameId);
	                     
	             
	        	Bundle bundle = new Bundle();
		    	bundle.putString("number", contactNumber);
		    	bundle.putString("name", contactName);
				StandOutWindow.show(ContextService.this, ClippyWindow.class, 0);
		    	
				StandOutWindow.sendData(ContextService.this, ClippyWindow.class, 0, 3, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
		    	lastMissedCallCount = getNumberMissedCalls();
	        }
	    }
	    public int getNumberMissedCalls()
	    {
	    	Cursor cursor = getContentResolver().query(
		            Calls.CONTENT_URI, 
		            null, 
		            Calls.TYPE +  " = ? AND " + Calls.NEW + " = ?", 
		            new String[] { Integer.toString(Calls.MISSED_TYPE), "1" }, 
		            Calls.DATE + " DESC ");

		        //this is the number of missed calls
		        //for your case you may need to track this number
		        //so that you can figure out when it changes
		        int i = cursor.getCount(); 

		        cursor.close();
		        return i;
	    }
	}
	OnPrimaryClipChangedListener clipChange = new OnPrimaryClipChangedListener() {
		
		@Override
		public void onPrimaryClipChanged() {
			Bundle bundle = new Bundle();
	    	bundle.putString("clip", clipboard.getPrimaryClip().getItemAt(0).coerceToText(ContextService.this).toString());
			StandOutWindow.show(ContextService.this, ClippyWindow.class, 0);

	    	StandOutWindow.sendData(ContextService.this, ClippyWindow.class, 0, 2, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
			//Toast.makeText(ContextService.this, clipboard.getPrimaryClip().toString(), Toast.LENGTH_SHORT).show();
		}
	};
	public ContextService() {
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		incomingTexts = new IncomingTextMessages(this);
		mcco = new MissedCallsContentObserver();
		getApplicationContext().getContentResolver().registerContentObserver(Calls.CONTENT_URI, true, mcco);
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.addPrimaryClipChangedListener(clipChange);
		final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		 runnable = new Runnable() {
			
			@Override
			public void run() {
				
				List<ActivityManager.RunningTaskInfo> taskInfo = am
						.getRunningTasks(1);
				ComponentName componentInfo = taskInfo.get(0).topActivity;
				if (!componentInfo.getPackageName().equals(lastPkg))
				{
					lastPkg = componentInfo.getPackageName();
					Bundle bundle = new Bundle();
			    	bundle.putString("pkg", componentInfo.getPackageName());
			    	final PackageManager pm = getApplicationContext().getPackageManager();
			    	ApplicationInfo ai;
			    	try {
			    	    ai = pm.getApplicationInfo( lastPkg, 0);
			    	} catch (final NameNotFoundException e) {
			    	    ai = null;
			    	}
			    	final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
			    	bundle.putString("name", applicationName);
			    	if (componentInfo.getPackageName().equals("com.google.android.gm") || componentInfo.getPackageName().equals("com.twitter.android"))
			    	{
			    		StandOutWindow.show(ContextService.this, ClippyWindow.class, 0);
			    	}
			    	StandOutWindow.sendData(ContextService.this, ClippyWindow.class, 0, 1, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
			   
					//Toast.makeText(ContextService.this, componentInfo.getPackageName(), Toast.LENGTH_SHORT).show();
				}
			    handler.postDelayed(this, 2000);
			}
		};
		handler.postDelayed(runnable, 2000);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		incomingTexts.stop();
		getApplicationContext().getContentResolver().unregisterContentObserver(mcco);
		clipboard.removePrimaryClipChangedListener(clipChange);
		super.onDestroy();
	}
}
