package siclo.com.photointenthelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

import siclo.com.photointenthelper.models.PhotoIntentException;
import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.models.PhotoSource;
import siclo.com.photointenthelper.storage.PhotoGenerator;
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

    private static final int STORE_SUCCESS_MSG = 0;
    private static final int STORE_FAIL_MSG = 1;

    private PhotoIntentHelperContract.View view;
    private PhotoIntentHelperStorage photoIntentHelperStorage;
    private PhotoGenerator photoGenerator;
    private PhotoIntentHelperConfig photoIntentHelperConfig;
    private String randomPhotoName;
    Bitmap pickingPhoto;

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

        if (photoIntentHelperConfig.photoSource == PhotoSource.CAMERA) {
            onPickPhotoWithCamera();
            return;
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
        isOpenedPhotoPick =false;
        isStoringPhoto = true;
        view.showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pickingPhoto = photoGenerator.generatePhotoWithValue(data.getData(), photoIntentHelperConfig.scaleSize);
                } catch (IOException e) {
                    e.printStackTrace();
                    photoPickHandler.sendEmptyMessage(STORE_FAIL_MSG);
                }
                randomPhotoName = UUID.randomUUID().toString();
                photoIntentHelperStorage.storeLastetStoredPhotoName(randomPhotoName);
                photoIntentHelperStorage.storePhotoBitmap(pickingPhoto, photoIntentHelperConfig.internalStorageDir, randomPhotoName);
                photoPickHandler.sendEmptyMessage(STORE_SUCCESS_MSG);
            }
        }).start();
    }

    @Override
    public void onRequestCameraPermissionGranted() {
        onPickPhotoWithCamera();
    }

    @Override
    public void onRequestCameraPermissionDenied() {
        view.finishWithNoResult();
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
