package wei.mark.example;


import wei.mark.standout.StandOutWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class IncomingTextMessages extends BroadcastReceiver {

	private Context context;

	public IncomingTextMessages(Context context) {
		this.context = context;
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		context.registerReceiver(this, filter);
	}

	public void stop() {
		context.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras(); // ---get the SMS message passed
												// in---
			SmsMessage[] msgs = null;
			String msg_from;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						String msgBody = msgs[i].getMessageBody();
						 bundle = new Bundle();
				    	bundle.putString("number", msg_from);
				    	bundle.putString("msg", msgBody);
				    	bundle.putString("name", ContactSearcher.numberToID(msg_from, context));
			    		StandOutWindow.show(context, ClippyWindow.class, 0);
				    	StandOutWindow.sendData(context, ClippyWindow.class, 0, 4, bundle, ClippyWindow.class, StandOutWindow.DISREGARD_ID);
				    	//Toast.makeText(context, msg_from + msgBody, Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					// Log.d("Exception caught",e.getMessage());
				}
			}
		}
	}
}