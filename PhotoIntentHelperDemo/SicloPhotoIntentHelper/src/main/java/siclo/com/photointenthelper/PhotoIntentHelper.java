package siclo.com.photointenthelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;

import static siclo.com.photointenthelper.models.PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY;
import static siclo.com.photointenthelper.models.PhotoIntentConstants.PHOTO_PICK_REQUEST_CODE;
import static siclo.com.photointenthelper.models.PhotoIntentConstants.SCREEN_ORIENTATION;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelper {

    private static final String TAG = PhotoIntentHelper.class.getSimpleName();

    public static void startPhotoIntentHelperActivity(Activity activity, PhotoIntentHelperConfig config){
        Intent intent = new Intent(activity, PhotoIntentHelperActivity.class);
        int orientation = getScreenOrientation(activity);
        intent.putExtra(PHOTO_PICK_CONFIG_KEY, config);
        intent.putExtra(SCREEN_ORIENTATION, orientation);
        activity.startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
        activity.overridePendingTransition(0, 0);
    }

    private static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }


}
