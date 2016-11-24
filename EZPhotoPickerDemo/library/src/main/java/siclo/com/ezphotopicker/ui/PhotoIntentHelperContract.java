package siclo.com.ezphotopicker.ui;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import siclo.com.ezphotopicker.models.PhotoIntentException;

/**
 * Created by ericta on 11/13/16.
 */

interface PhotoIntentHelperContract {

    interface View{

        void openGallery();

        void showLoading();

        void finishPickPhotoWithSuccessResult();

        void showPickPhotoFromGalleryError(int unexpectedErrorStringResource);

        void finishWithNoResult();

        void requestCameraPermission();

        void openCamera();

        void requestReadExternalStoragePermission();

        void showToastMessagePermissionDenied(int permisionDeniedErrorStringResource);
    }

    interface Presenter{

        void onCreate(Bundle savedInstanceState) throws PhotoIntentException;

        void onPhotoPickedFromGallery(Intent data);

        void onRequestCameraPermissionGranted();

        void onRequestPermissionDenied();

        void onDestroy();

        void onPhotoPickedFromCamera(File filesDir);

        void onRequestReadExternalPermissionGranted();
    }

}
