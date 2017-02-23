package siclo.com.ezphotopicker.api;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
import siclo.com.ezphotopicker.models.PhotoIntentConstants;
import siclo.com.ezphotopicker.models.PhotoIntentException;
import siclo.com.ezphotopicker.ui.PhotoIntentHelperActivity;

/**
 * Created by ericta on 11/13/16.
 */

public class EZPhotoPick {

    public static final int PHOTO_PICK_CAMERA_REQUEST_CODE = 9067;
    public static final int PHOTO_PICK_GALERY_REQUEST_CODE = 9068;

    public static final String PICKED_PHOTO_NAME_KEY = "PICKED_PHOTO_NAME";
    public static final String PICKED_PHOTO_NAMES_KEY = "PICKED_PHOTO_NAMES_KEY";

    private static final String TAG = EZPhotoPick.class.getSimpleName();

    public static void startPhotoPickActivity(Activity activity, EZPhotoPickConfig config){
        Intent intent = new Intent(activity, PhotoIntentHelperActivity.class);
        int orientation = getScreenOrientation(activity);
        intent.putExtra(PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY, config);
        intent.putExtra(PhotoIntentConstants.SCREEN_ORIENTATION, orientation);
        int requestCode = config.photoSource == PhotoSource.GALERY ? PHOTO_PICK_GALERY_REQUEST_CODE : PHOTO_PICK_CAMERA_REQUEST_CODE;
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(0, 0);
    }

    public static void startPhotoPickActivity(Fragment fragment, EZPhotoPickConfig config) throws PhotoIntentException {
        Activity activity = fragment.getActivity();
        if(activity == null){
            throw new PhotoIntentException("Can not find the host activity of fragment");
        }
        Intent intent = new Intent(fragment.getContext(), PhotoIntentHelperActivity.class);
        int orientation = getScreenOrientation(activity);
        intent.putExtra(PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY, config);
        intent.putExtra(PhotoIntentConstants.SCREEN_ORIENTATION, orientation);
        int requestCode = config.photoSource == PhotoSource.GALERY ? PHOTO_PICK_GALERY_REQUEST_CODE : PHOTO_PICK_CAMERA_REQUEST_CODE;
        fragment.startActivityForResult(intent, requestCode);
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
