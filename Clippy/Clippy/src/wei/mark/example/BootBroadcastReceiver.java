package wei.mark.example;

import wei.mark.standout.StandOutWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, ContextService.class);
		context.startService(startServiceIntent);
		StandOutWindow.show(context, ClippyWindow.class, 0);
	}
}