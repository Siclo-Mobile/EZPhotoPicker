package siclo.com.ezphotopicker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
import siclo.com.ezphotopicker.models.PhotoIntentConstants;
import siclo.com.ezphotopicker.models.PhotoIntentException;
import siclo.com.ezphotopicker.storage.PhotoGenerator;
import siclo.com.ezphotopicker.storage.PhotoIntentContentProvider;
import siclo.com.ezphotopicker.storage.PhotoIntentHelperStorage;
import siclo.com.ezphotopicker.storage.PhotoUriHelper;

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
    private static List<Uri> photoUriList = new ArrayList<>();
    private static List<String> storedPhotoNames = new ArrayList<>();

    private static final int STORE_SUCCESS_MSG = 0;
    private static final int STORE_FAIL_MSG = 1;

    private PhotoIntentHelperContract.View view;
    private PhotoIntentHelperStorage photoIntentHelperStorage;
    private PhotoUriHelper photoUriHelper;
    private PhotoGenerator photoGenerator;
    private EZPhotoPickConfig eZPhotoPickConfig;
    private String storingPhotoName;
    private boolean isAllowMultipleSelect;

    PhotoIntentHelperPresenter(PhotoIntentHelperContract.View view, PhotoUriHelper photoUriHelper, PhotoGenerator photoGenerator, PhotoIntentHelperStorage photoIntentHelperStorage, EZPhotoPickConfig eZPhotoPickConfig) {
        this.view = view;
        this.photoUriHelper = photoUriHelper;
        this.photoGenerator = photoGenerator;
        this.photoIntentHelperStorage = photoIntentHelperStorage;
        this.eZPhotoPickConfig = eZPhotoPickConfig;
        isAllowMultipleSelect = eZPhotoPickConfig.isAllowMultipleSelect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) throws PhotoIntentException {
        if (eZPhotoPickConfig == null) {
            throw PhotoIntentException.getNullPhotoPickConfigException();
        }

        if (isStoringPhoto) {
            view.showLoading();
            return;
        }

        if (isOpenedPhotoPick) {
            return;
        }

        if (eZPhotoPickConfig.photoSource == PhotoSource.CAMERA) {
            view.requestCameraAndExternalStoragePermission(eZPhotoPickConfig.needToAddToGallery);
        } else {
            onPickPhotoWithGalery();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPhotoPickedFromGallery(final Intent data) {
        photoUriList.clear();
        if (isAllowMultipleSelect && data.getClipData()!= null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                photoUriList.add(data.getClipData().getItemAt(i).getUri());
            }
        } else {
            photoUriList.add(data.getData());
        }
        for (Uri photoUri : photoUriList) {
            boolean isPhotoUriPointToExternalStorage = isUriPointToExternalStorage(photoUri);
            if (!isPhotoUriPointToExternalStorage) {
                view.requestReadExternalStoragePermission();
                return;
            }
        }

        onPhotoPicked();
    }

    @Override
    public void onRequestReadExternalPermissionGranted() {
        onPhotoPicked();
    }

    private void onPhotoPicked() {
        isOpenedPhotoPick = false;
        isStoringPhoto = true;
        view.showLoading();
        processPickedUrisInBackground();
    }

    private boolean isUriPointToExternalStorage(Uri photoUri) {
        return photoUri.toString().contains(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void processPickedUrisInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Uri photoUri : photoUriList) {
                    processPickedUri(photoUri);
                }
                photoPickHandler.sendEmptyMessage(STORE_SUCCESS_MSG);
            }
        }).start();
    }

    private void processPickedUri(Uri photoUri) {
        generateStoringPhotoName();
        try {
            Bitmap pickingPhoto = photoGenerator.generatePhotoWithValue(photoUri, eZPhotoPickConfig);

            Bitmap.CompressFormat bitmapConfig = photoUriHelper.getUriPhotoBitmapFormat(photoUri);

            photoIntentHelperStorage.storePhotoBitmap(pickingPhoto, bitmapConfig, eZPhotoPickConfig.storageDir, storingPhotoName);

            Bitmap thumbnail;
            if (eZPhotoPickConfig.needToExportThumbnail) {
                thumbnail = photoGenerator.scalePhotoByMaxSize(eZPhotoPickConfig.exportingThumbSize, pickingPhoto);
                photoIntentHelperStorage.storePhotoBitmapThumbnail(thumbnail, bitmapConfig, eZPhotoPickConfig.storageDir, storingPhotoName);
            }

            if (needToAddToGalery()) {
                addLastestCapturedPhotoToGallery(pickingPhoto);
            }
            storedPhotoNames.add(storingPhotoName);
            photoIntentHelperStorage.storeLatestStoredPhotoName(storingPhotoName);
            photoIntentHelperStorage.storeLatestStoredPhotoDir(eZPhotoPickConfig.storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            photoPickHandler.sendEmptyMessage(STORE_FAIL_MSG);
        }
    }

    private boolean needToAddToGalery() {
        return eZPhotoPickConfig.needToAddToGallery && eZPhotoPickConfig.photoSource == PhotoSource.CAMERA;
    }

    private void generateStoringPhotoName() {

        if (eZPhotoPickConfig.isGenerateUniqueName || isPickingMultiplePhotoFromGalery()) {
            storingPhotoName = UUID.randomUUID().toString();
            return;
        }

        if (!TextUtils.isEmpty(eZPhotoPickConfig.exportedPhotoName)) {
            storingPhotoName = eZPhotoPickConfig.exportedPhotoName;
            return;
        }

        storingPhotoName = PhotoIntentConstants.TEMP_STORING_PHOTO_NAME;

    }

    private boolean isPickingMultiplePhotoFromGalery() {
        return isAllowMultipleSelect && eZPhotoPickConfig.photoSource == PhotoSource.GALERY;
    }

    @Override
    public void onPhotoPickedFromCamera(File internalDir) {
//        view.notifyGalleryDataChanged(exportedPhotoUri);
        File photoFile = new File(internalDir, PhotoIntentContentProvider.TEMP_PHOTO_NAME);
        photoUriList.clear();
        photoUriList.add(Uri.fromFile(photoFile));
        onPhotoPicked();
    }

    private void addLastestCapturedPhotoToGallery(Bitmap thumb) {
        try {
            Calendar calendar = Calendar.getInstance();
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = calendar.getTime().getTime() + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            FileOutputStream out = new FileOutputStream(file);
            thumb.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            view.sendBroadcastToScanFileInGallery(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestCameraPermissionGranted() {
        onPickPhotoWithCamera();
    }

    @Override
    public void onRequestPermissionDenied() {
        view.showToastMessagePermissionDenied(eZPhotoPickConfig.permisionDeniedErrorStringResource);
        view.finishWithNoResult();
    }

    @Override
    public void onDestroy() {
        isStoringPhoto = false;
        isOpenedPhotoPick = false;
    }


    private Handler photoPickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case STORE_SUCCESS_MSG:
                    finishPickPhotoWithSuccessResult();
                    photoUriList.clear();
                    storedPhotoNames.clear();
                    break;
                case STORE_FAIL_MSG:
                    view.showPickPhotoFromGalleryError(eZPhotoPickConfig.unexpectedErrorStringResource);
                    view.finishWithNoResult();
                    break;
            }
            isStoringPhoto = false;
            return false;
        }
    });

    private void finishPickPhotoWithSuccessResult(){
        String pickedPhotoName = storedPhotoNames.get(storedPhotoNames.size()-1);
        ArrayList<String> pickedPhotoNames = new ArrayList<>(storedPhotoNames);
        view.finishPickPhotoWithSuccessResult(pickedPhotoName, pickedPhotoNames);
    }

    private void onPickPhotoWithCamera() {
        isOpenedPhotoPick = true;
        view.openCamera();
    }

    private void onPickPhotoWithGalery() {
        isOpenedPhotoPick = true;
        view.openGallery(isAllowMultipleSelect);
    }
}
