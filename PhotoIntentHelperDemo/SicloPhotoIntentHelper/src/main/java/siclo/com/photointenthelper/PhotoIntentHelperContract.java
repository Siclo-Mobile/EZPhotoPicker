package siclo.com.photointenthelper;

import android.content.Intent;
import android.os.Bundle;

import siclo.com.photointenthelper.models.PhotoIntentException;

/**
 * Created by ericta on 11/13/16.
 */

interface PhotoIntentHelperContract {

    interface View{

        void openGallery();

        void showLoading();

        void finishPickPhotoWithSuccessResult();

        void showPickPhotoFromGalleryError();

        void finishWithNoResult();

        void requestCameraPermission();
    }

    interface Presenter{

        void onCreate(Bundle savedInstanceState) throws PhotoIntentException;

        void onPhotoPickedFromGallery(Intent data);

        void onRequestPermissionGranted();

        void onRequestPermissionDenied();
    }

}
