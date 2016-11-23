package siclo.com.photointenthelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import siclo.com.photointenthelper.models.PhotoIntentException;
import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.models.PhotoSource;
import siclo.com.photointenthelper.storage.PhotoGenerator;
import siclo.com.photointenthelper.storage.PhotoIntentContentProvider;
import siclo.com.photointenthelper.storage.PhotoIntentHelperStorage;

/**
 * Created by ericta on 11/13/16.
 */

class PhotoIntentHelperPresenter implements PhotoIntentHelperContract.Presenter {

    /*
    Only 1 camera/gallery screen at 1 time
    it 's ok to keep static value here(for simple)
     */
    private static boolean isStoringPhoto = false;
    private static boolean isOpenedPhotoPick = false;
    private static Uri photoUri;

    private static final int STORE_SUCCESS_MSG = 0;
    private static final int STORE_FAIL_MSG = 1;

    private PhotoIntentHelperContract.View view;
    private PhotoIntentHelperStorage photoIntentHelperStorage;
    private PhotoGenerator photoGenerator;
    private PhotoIntentHelperConfig photoIntentHelperConfig;
    private String randomPhotoName;

    PhotoIntentHelperPresenter(PhotoIntentHelperContract.View view, PhotoGenerator photoGenerator, PhotoIntentHelperStorage photoIntentHelperStorage, PhotoIntentHelperConfig photoIntentHelperConfig) {
        this.view = view;
        this.photoGenerator = photoGenerator;
        this.photoIntentHelperStorage = photoIntentHelperStorage;
        this.photoIntentHelperConfig = photoIntentHelperConfig;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) throws PhotoIntentException {
        if (photoIntentHelperConfig == null) {
            throw PhotoIntentException.getNullPhotoPickConfigException();
        }

        if(isStoringPhoto){
            view.showLoading();
            return;
        }

        if(isOpenedPhotoPick){
            return;
        }

        if(photoIntentHelperConfig.photoSource == PhotoSource.CAMERA){
            view.requestCameraPermission();
        }else{
            onPickPhotoWithGalery();
        }
    }

    @Override
    public void onPhotoPickedFromGallery(final Intent data) {
        photoUri = data.getData();
        boolean isPhotoUriPointToExternalStorage = isCurrentPhotoUriPointToExternalStorage();
        if(isPhotoUriPointToExternalStorage){
            view.requestReadExternalStoragePermission();
            return;
        }
        onPhotoPicked();
    }

    @Override
    public void onRequestReadExternalPermissionGranted() {
        onPhotoPicked();
    }

    private void onPhotoPicked() {
        isOpenedPhotoPick =false;
        isStoringPhoto = true;
        view.showLoading();
        processPickedUriInBackground(photoUri);
    }

    private boolean isCurrentPhotoUriPointToExternalStorage() {
        return photoUri.toString().contains(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void processPickedUriInBackground(final Uri photoUri) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap pickingPhoto = photoGenerator.generatePhotoWithValue(photoUri, photoIntentHelperConfig.scaleSize);
                        randomPhotoName = UUID.randomUUID().toString();
                        photoIntentHelperStorage.storeLastetStoredPhotoName(randomPhotoName);
                        photoIntentHelperStorage.storeLastetStoredPhotoDir(photoIntentHelperConfig.internalStorageDir);
                        Bitmap storedBitmap = photoIntentHelperStorage.storePhotoBitmap(photoUri, pickingPhoto, photoIntentHelperConfig.internalStorageDir, randomPhotoName);
                        if(photoIntentHelperConfig.extraAction !=null){
                            photoIntentHelperConfig.extraAction.doExtraAction(storedBitmap);
                        }
                        photoPickHandler.sendEmptyMessage(STORE_SUCCESS_MSG);
                    } catch (IOException e) {
                        e.printStackTrace();
                        photoPickHandler.sendEmptyMessage(STORE_FAIL_MSG);
                    }

                }
        }).start();
    }

    @Override
    public void onPhotoPickedFromCamera(File internalDir) {
//        view.notifyGalleryDataChanged(exportedPhotoUri);
        File photoFile = new File(internalDir, PhotoIntentContentProvider.TEMP_PHOTO_NAME);
        photoUri = Uri.fromFile(photoFile);
        onPhotoPicked();
    }

    @Override
    public void onRequestCameraPermissionGranted() {
        onPickPhotoWithCamera();
    }

    @Override
    public void onRequestPermissionDenied() {
        view.showToastMessagePermissionDenied();
        view.finishWithNoResult();
    }

    @Override
    public void onDestroy() {
        isStoringPhoto = false;
        isOpenedPhotoPick = false;
    }



    Handler photoPickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            switch (what){
                case STORE_SUCCESS_MSG:
                    view.finishPickPhotoWithSuccessResult();
                    break;
                case STORE_FAIL_MSG:
                    view.showPickPhotoFromGalleryError();
                    view.finishWithNoResult();
                    break;
            }
            isStoringPhoto = false;
            return false;
        }
    });

    private void onPickPhotoWithCamera() {
        isOpenedPhotoPick = true;
        view.openCamera();
    }

    private void onPickPhotoWithGalery() {
        isOpenedPhotoPick = true;
        view.openGallery();
    }
}
