package wei.mark.example;

import java.net.URLEncoder;
import java.util.Timer;

import wei.mark.example.ClippyAnimation.CLIPPY_ANIMATIONS;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class ClippyWindow extends StandOutWindow {
	AnimationDrawable animation;
	public static final int NOTIFICATION_REQUEST_CODE = 0; // ticker, pkg
	public static final int FOREGROUND_ACTIVITY_REQUEST_CODE = 1; // pkg
	public static final int CLIPBOARD_CHANGE_REQUEST_CODE = 2; // number, name
	public static final int MISSED_CALL_REQUEST_CODE = 3; // number, name
	public static final int TEXT_MESSAGE_REQUEST_CODE = 4; // number, msg
	public static final int TEXT_CHANGE_REQUEST_CODE = 5; // hotphrase (ie
															// attach)

	private TextView messageText, messageTitle;
	private boolean close = false;
	private Animation expand, collapse;
	private boolean animatingBubble = false, animatingClippy = false;
	private ImageView clippy;
	private View messageView, buttonView, rootView;
	private int mInterval = 10000;
	private Handler mHandler;
	private EditText textField;
	private Button button1, button2;
	private int lastPriority = 0;
	Runnable mStatusChecker = new Runnable() {
		@Override
		public void run() {
			animate(Math.random() < 0.8 ? ClippyAnimation.CLIPPY_ANIMATIONS.LOOK_ALIVE : ClippyAnimation.CLIPPY_ANIMATIONS.SCRATCH_HEAD, true, false);
			mHandler.postDelayed(mStatusChecker, mInterval);
		}
	};

	void animate(CLIPPY_ANIMATIONS anim, boolean oneShot, boolean override, int dur) {
		if (!animatingClippy || override) {
			AnimationDrawable animation = ClippyAnimation.createDrawableAnimation(this, anim, dur);
			animation.setOneShot(oneShot);
			clippy.setImageDrawable(animation);
			animation.start();
			animatingClippy = true;
			int duration = animation.getDuration(0) * animation.getNumberOfFrames();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					animatingClippy = false;
				}

			}, duration);
		}
	}

	void animate(CLIPPY_ANIMATIONS anim, boolean oneShot, boolean override) {
		animate(anim, oneShot, override, 100);
	}

	void startRepeatingTask() {
		new Handler().postDelayed(mStatusChecker, mInterval);
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

	@Override
	public String getAppName() {
		return "ClippyWindow";
	}

	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_close_clear_cancel;
	}
	CountDownTimer timer = new CountDownTimer(30000, 30000) {
		
		@Override
		public void onTick(long millisUntilFinished) {
			
		}
		
		@Override
		public void onFinish() {
			close(0);
		}
	};
	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		mHandler = new Handler();

		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		rootView = inflater.inflate(R.layout.clippy, frame, true);
		messageView = rootView.findViewById(R.id.rl);
		messageTitle = (TextView) rootView.findViewById(R.id.messageTitle);
		messageText = (TextView) rootView.findViewById(R.id.messageText);
		textField = (EditText) rootView.findViewById(R.id.inputField);
		buttonView = rootView.findViewById(R.id.buttonView);
		messageView.setVisibility(View.GONE);
		button1 = (Button) rootView.findViewById(R.id.button1);
		button2 = (Button) rootView.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				close(0);
			}
		});
		showButtons(false);
		textField.setImeActionLabel("Go", EditorInfo.IME_ACTION_GO);

		clippy = ((ImageView) frame.findViewById(R.id.clippy));
		animate(ClippyAnimation.CLIPPY_ANIMATIONS.BICYCLE_IN, true, true);

		showHelp(2500);

		startRepeatingTask();

		// nextAnimation =
		// ClippyAnimation.createRandomAnimationDrawable(SimpleWindow.this, 70);
		// imgView.set
		/*
		 * imgView.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent arg1) {
		 * //arg0.setBackgroundDrawable(nextAnimation);
		 * 
		 * nextAnimation.start();
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { nextAnimation =
		 * ClippyAnimation.createRandomAnimationDrawable(SimpleWindow.this, 70);
		 * } }).start();
		 * 
		 * return false; } });
		 */
	}

	void showButtons(boolean show) {
		if (show) {
			buttonView.setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.view1).setVisibility(View.VISIBLE);
		} else {
			rootView.findViewById(R.id.view1).setVisibility(View.GONE);
			buttonView.setVisibility(View.GONE);
		}
	}

	void setButtonsText(String _button1, String _button2) {
		button1.setText(_button1);
		button2.setText(_button2);
	}

	public void showHelp(int delay) {
		showButtons(false);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (lastPriority < 2)
				{
					messageTitle.setText("Hey!");
					messageText.setText("How can I help you?");
				}
				lastPriority = 0;
				textField.setVisibility(View.VISIBLE);
				showBubble(false, true);
			}

		}, delay);
	}

	@Override
	public boolean onShow(final int id, final Window w) {
		lastPriority = 0;
		startService(new Intent(this, ContextService.class));
		timer.start();
		w.setTranslationX(pxFromDp(10));
		close = false;
		animatingBubble = false;

		expand = AnimationUtils.loadAnimation(this, R.anim.expand);
		collapse = AnimationUtils.loadAnimation(this, R.anim.collapse);
		collapse.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				animatingBubble = true;
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				messageView.setVisibility(View.GONE);
				textField.setVisibility(View.GONE);
				showButtons(false);
				animatingBubble = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

		});

		clippy.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				timer.cancel();
				timer.start();
				onTouchHandleMove(id, w, v, event);
				return true;
			}
		});

		return false;
	}

	@Override
	public Animation getShowAnimation(int id) {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.expand);
		anim.setDuration(500);
		return anim;
	}

	private int pxFromDp(float dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}

	// the window will be centered
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, StandOutLayoutParams.WRAP_CONTENT, StandOutLayoutParams.WRAP_CONTENT, StandOutLayoutParams.RIGHT, pxFromDp(50));
	}

	// move the window by dragging the view
	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
	}

	@Override
	public void onMove(int id, Window window, View v, MotionEvent e) {
		timer.cancel();
		timer.start();
		if (!animatingBubble)
			showBubble(true, false);
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Click to close the SimpleWindow";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getCloseIntent(this, ClippyWindow.class, id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceiveData(int id, int requestCode, Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
		textField.setVisibility(View.GONE);
		switch (requestCode) {
		case NOTIFICATION_REQUEST_CODE:
			if (lastPriority > 0)
			{
				break;
			}
			lastPriority = 0;
			String title = data.getString("title");
			String desc = data.getString("desc");
			messageTitle.setText(title);
			messageText.setText(desc);
			showButtons(false);
			showBubble(true, true);
			
			break;
		case TEXT_MESSAGE_REQUEST_CODE:
			if (lastPriority > 4)
			{
				break;
			}
			lastPriority = 4;
			showButtons(true);
			setButtonsText("Reply", "Close");
			final String number = data.getString("number");
			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showBubble(true, false);
					Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
					sendIntent.setData(Uri.parse("sms:" + number));
					sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(sendIntent);
					
				}
			});
			String name = data.getString("name");
			String msg = data.getString("msg");
			
			messageTitle.setText(name);
			messageText.setText(msg);
			showBubble(true, true);
			break;
		case MISSED_CALL_REQUEST_CODE:
			lastPriority = 5;
			showButtons(true);
			setButtonsText("Call", "Close");
			final String number3 = data.getString("number");
			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showBubble(true, false);
					String uri = "tel:" + number3.trim() ;
					 Intent intent = new Intent(Intent.ACTION_DIAL);
					 intent.setData(Uri.parse(uri));
					 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 startActivity(intent);
				}
			});
			String name2 = data.getString("name");
			messageTitle.setText("Missed Call");
			messageText.setText(name2);
			showBubble(true, true);
			break;
		case CLIPBOARD_CHANGE_REQUEST_CODE:
			if (lastPriority > 3)
			{
				break;
			}
			lastPriority = 3;
			showButtons(true);
			setButtonsText("Yes", "No");
			final String clip = data.getString("clip");

			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					showBubble(true, false);
					Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, clip);
					sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(sharingIntent);
				}
			});
			messageTitle.setText("Clipboard");
			messageText.setText("I see you have copied some text, would you like me to help you with that?");
			showBubble(true, true);
			break;
		case TEXT_CHANGE_REQUEST_CODE:
			if (lastPriority > 2)
			{
				break;
			}
			lastPriority = 2;
			showButtons(false);
			String hotphrase = data.getString("hotphrase");
			if (hotphrase.equals("attach")) {
			
				messageTitle.setText("Attach File");
				messageText.setText("Can I help you attach the file?");
				showBubble(true, true);
			}
			break;
		case FOREGROUND_ACTIVITY_REQUEST_CODE:
			if (lastPriority > 1)
			{
				break;
			}
			lastPriority = 1;
			String pkg = data.getString("pkg");
			if (pkg.equals("com.google.android.gm")) {
				showButtons(true);
				setButtonsText("Open", "Ignore");
				button1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showBubble(true, false);

						animate(ClippyAnimation.CLIPPY_ANIMATIONS.SEARCH, true, true);

						try {
							String url = "http://www.hotmail.com";
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
							browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(browserIntent);
						}
						catch (Exception e)
						{}
					}
				});
				messageTitle.setText("Scroogled");
				messageText.setText("Did you know that Google reads your gmail? Would you like me to open up the more 'modern' hotmail?");
				showBubble(true, true);
				animate(CLIPPY_ANIMATIONS.WRITING, true, true);
			}
			else if (pkg.equals("com.twitter.android"))
			{
				showButtons(true);
				setButtonsText("Open", "Ignore");
				button1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showBubble(true, false);

						animate(ClippyAnimation.CLIPPY_ANIMATIONS.SEARCH, true, true);

						try {
							String url = "http://www.twitter.com/search?q=angelhack";
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
							browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(browserIntent);
							
						}
						catch (Exception e)
						{}
					}
				});
				messageTitle.setText("#AngelHack is trending!");
				messageText.setText("Check it out on Twitter...");
				showBubble(true, true);
				animate(CLIPPY_ANIMATIONS.ORBIT, true, true);

			}
			break;
		}
		// Toast.makeText(this, messageTitle.getText().toString() +
		// messageText.getText().toString(), Toast.LENGTH_SHORT).show();

	}

	private void showBubble(boolean animate, boolean show) {
		timer.cancel();
		timer.start();
		/**
		 * Search action
		 */
		textField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				performSearch(v.getContext(), ((EditText) messageView.findViewById(R.id.inputField)).getText().toString());
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return true;
			}
		});
		
		if (show && messageView.getVisibility() == View.GONE && !animatingBubble) {
			messageView.startAnimation(expand);
			textField.setText("");

			messageView.setVisibility(View.VISIBLE);

			// Knock
			if (animate) {
				animate(ClippyAnimation.CLIPPY_ANIMATIONS.NOTIFY, true, true);
			}

		} else if (messageView.getVisibility() == View.VISIBLE && !show && !animatingBubble) {
			lastPriority = 0;
			messageView.startAnimation(collapse);
		}
	}

	protected void performSearch(Context ctx, String string) {
		showBubble(true, false);

		animate(ClippyAnimation.CLIPPY_ANIMATIONS.SEARCH, true, true);

		try {
			final String query = URLEncoder.encode(string, "utf-8");
			String url = "http://bing.com/search?q=" + query;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(browserIntent);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					messageTitle.setText("Just kidding!");
					messageText.setText("Meant to Google it");
					showBubble(false, true);
					animate(ClippyAnimation.CLIPPY_ANIMATIONS.SCRATCH_HEAD, true, true, 160);
					Uri uri = Uri.parse("http://www.google.com/#q=" + query);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(intent);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							showBubble(true, false);
						}
					}, 3000);
				}
			}, 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Window close
	 */
	public boolean onClose(final int id, Window w) {
		if (!close) {
			showBubble(true, false);
			AnimationDrawable exit = ClippyAnimation.createDrawableAnimation(this, ClippyAnimation.CLIPPY_ANIMATIONS.BICYCLE_OUT, 100);
			exit.setOneShot(true);
			clippy.setImageDrawable(exit);
			animatingBubble = true;
			int duration = exit.getDuration(0) * exit.getNumberOfFrames();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					close = true;
					close(id);
				}

			}, duration);
			exit.start();
			return true;
		}
		stopRepeatingTask();
		return false;
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	@SuppressWarnings("deprecation")
	@Override
	public Notification getPersistentNotification(int id) {
		int icon = getAppIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String contentText = getPersistentNotificationMessage(id);

		Intent notificationIntent = getPersistentNotificationIntent(id);

		PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		final int apiLevel = Build.VERSION.SDK_INT;

		// 4.1+ Low priority notification
		if (apiLevel >= 16) {
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification);
			Notification.Builder mBuilder = new Notification.Builder(this).setContent(views).setSmallIcon(getAppIcon()).setContentTitle(contentTitle).setContentText(contentText).setPriority(Notification.PRIORITY_MIN).setContentIntent(contentIntent);
			return mBuilder.build();
		}

		String tickerText = String.format("%s: %s", contentTitle, contentText);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);

		return notification;
	}

	@Override
	public boolean onTouchHandleMove(int id, Window window, View view, MotionEvent event) {
		StandOutLayoutParams params = window.getLayoutParams();

		// how much you have to move in either direction in order for the
		// gesture to be a move and not tap

		int totalDeltaX = window.touchInfo.lastX - window.touchInfo.firstX;
		int totalDeltaY = window.touchInfo.lastY - window.touchInfo.firstY;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			window.touchInfo.lastX = (int) event.getRawX();
			window.touchInfo.lastY = (int) event.getRawY();

			window.touchInfo.firstX = window.touchInfo.lastX;
			window.touchInfo.firstY = window.touchInfo.lastY;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = (int) event.getRawY() - window.touchInfo.lastY;

			window.touchInfo.lastX = (int) event.getRawX();
			window.touchInfo.lastY = (int) event.getRawY();

			if (window.touchInfo.moving || Math.abs(totalDeltaX) >= params.threshold || Math.abs(totalDeltaY) >= params.threshold) {
				window.touchInfo.moving = true;

				// update the position of the window
				if (event.getPointerCount() == 1) {
					// params.x += deltaX;
					params.y += deltaY;
				}

				window.edit().setPosition(params.x, params.y).commit();
			}
			break;
		case MotionEvent.ACTION_UP:
			int deltaX = window.touchInfo.lastX - window.touchInfo.firstX;
			deltaY = window.touchInfo.lastY - window.touchInfo.firstY;
			if (deltaX > pxFromDp(30) && Math.abs(deltaY) < pxFromDp(40) && !animatingBubble) {
				close(id);
			}
			if (Math.abs(deltaX) < pxFromDp(5) && Math.abs(deltaY) < pxFromDp(5)) {
				Log.d("Tap", "Tapped");
				showHelp(0);
			}
			window.touchInfo.moving = false;
			break;
		}

		onMove(id, window, view, event);

		return true;
	}
}
