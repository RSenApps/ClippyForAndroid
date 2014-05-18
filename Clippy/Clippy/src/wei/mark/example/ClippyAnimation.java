package wei.mark.example;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class ClippyAnimation {
	public static enum CLIPPY_ANIMATIONS {
		BICYCLE_IN_OUT, BICYCLE_IN, BICYCLE_OUT, LOOK_ALIVE, SCRATCH_HEAD, CHECK, ORBIT, TIRED_IN, TIRED_OUT, TIRED_IN_OUT, PRINT_IN, PRINT_OUT, KNOCK, NEUTRAL, NOTIFY, SEARCH, WRITING
	}

	// 536-555 and back is notify
	public static final int[] BICYCLE_IN_OUT = new int[] { 824, 888 };
	public static final int[] BICYCLE_IN = new int[] { 824, 857 };
	public static final int[] BICYCLE_OUT = new int[] { 857, 888 };
	public static final int[] LOOK_ALIVE = new int[] { 234, 249 };
	public static final int[] SCRATCH_HEAD = new int[] { 719, 737 };
	public static final int[] CHECK = new int[] { 1, 11 };
	public static final int[] ORBIT = new int[] { 24, 65 };
	public static final int[] TIRED_IN = new int[] { 66, 127 };
	public static final int[] TIRED_OUT = new int[] { 128, 137 };
	public static final int[] TIRED_IN_OUT = new int[] { 66, 137 };
	public static final int[] PRINT_IN = new int[] { 138, 186 };
	public static final int[] PRINT_OUT = new int[] { 187, 200 };
	public static final int[] PRINT_IN_OUT = new int[] { 138, 200 };
	public static final int[] KNOCK = new int[] { 202, 217 };
	public static final int[] NEUTRAL = new int[] { 234, 234 };
	public static final int[] NOTIFY = new int[] { 536, 555 };
	public static final int[] SEARCH = new int[] { 737, 792 };
	public static final int[] WRITING = new int[] { 556, 616 };

	public static final int[][] animationFrames = new int[][] { BICYCLE_IN_OUT, BICYCLE_IN, BICYCLE_OUT, LOOK_ALIVE, SCRATCH_HEAD, CHECK, ORBIT, TIRED_IN, TIRED_OUT, TIRED_IN_OUT, PRINT_IN, PRINT_OUT, KNOCK, NEUTRAL, NOTIFY, SEARCH, WRITING };

	public static Drawable[] getFrameSet(Context context, CLIPPY_ANIMATIONS animationType) {
		int[] animation = animationFrames[animationType.ordinal()];
		Resources res = context.getResources();
		Drawable[] frames = new Drawable[animation[1] - animation[0] + 1];
		for (int i = 0; i <= animation[1] - animation[0]; i++) {
			int frame = animation[0] + i;
			frames[i] = getResourceByName(res, "image" + (frame));
		}
		return frames;
	}

	public static Bitmap[] getFrameSetBitmap(Context context, int[] animation) {
		Resources res = context.getResources();
		Bitmap[] frames = new Bitmap[animation[1] - animation[0] + 1];
		for (int i = 0; i <= animation[1] - animation[0]; i++) {
			int frame = animation[0] + i;
			String packageName = res.getResourcePackageName(R.id.imageView1);
			int resId = res.getIdentifier("image" + frame, "drawable", packageName);
			frames[i] = BitmapFactory.decodeResource(res, resId);
		}
		return frames;
	}

	private static Drawable getResourceByName(Resources res, String id) {
		String packageName = res.getResourcePackageName(R.id.imageView1);
		int resId = res.getIdentifier(id, "drawable", packageName);
		return res.getDrawable(resId);
	}

	public static AnimationDrawable createDrawableAnimation(Context context, CLIPPY_ANIMATIONS animation, int duration) {
		AnimationDrawable animationDrawable = new AnimationDrawable();
		animationDrawable.setOneShot(false);
		for (Drawable drawable : ClippyAnimation.getFrameSet(context, animation)) {
			animationDrawable.addFrame(drawable, duration);
		}
		return animationDrawable;
	}

	public static AnimationDrawable createRandomAnimationDrawable(Context context, int duration) {
		Random rand = new Random();
		CLIPPY_ANIMATIONS animationType = CLIPPY_ANIMATIONS.values()[rand.nextInt(CLIPPY_ANIMATIONS.values().length)];
		return createDrawableAnimation(context, animationType, duration);
	}

	public static AnimationDrawable mirrorAnimation(AnimationDrawable animation) {
		for (int frame = animation.getNumberOfFrames() - 1; frame >= 0; frame--) {
			animation.addFrame(animation.getFrame(frame), animation.getDuration(0));
		}
		return animation;
	}

	public static AnimationDrawable createAllAnimationDrawable(Context context, int duration) {
		AnimationDrawable animationDrawable = new AnimationDrawable();
		animationDrawable.setOneShot(false);
		for (CLIPPY_ANIMATIONS anim : CLIPPY_ANIMATIONS.values()) {
			for (Drawable drawable : ClippyAnimation.getFrameSet(context, anim)) {
				animationDrawable.addFrame(drawable, duration);
			}
		}
		return animationDrawable;
	}
}
