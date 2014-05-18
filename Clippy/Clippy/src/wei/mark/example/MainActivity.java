package wei.mark.example;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StandOutWindow.closeAll(this, ClippyWindow.class);
		StandOutWindow.show(this, ClippyWindow.class, 0);
		finish();
	}
}