package siclo.com.ezphotopicker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.models.PhotoIntentConstants;
import siclo.com.ezphotopicker.models.PhotoIntentException;
import siclo.com.ezphotopicker.storage.PhotoGenerator;
import siclo.com.ezphotopicker.storage.PhotoIntentContentProvider;
import siclo.com.ezphotopicker.storage.PhotoIntentHelperStorage;
import siclo.com.ezphotopicker.storage.PhotoUriHelper;
import siclo.com.photointenthelper.R;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelperActivity
        extends AppCompatActivity implements PhotoIntentHelperContract.View {

    private static final int REQUEST_CAMERA_PERMISSION = 2001;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2002;
    private static final int PICK_PHOTO_FROM_GALLERY = 1001;
    private static final int PICK_PHOTO_FROM_CAMERA = 1002;
    EZPhotoPickConfig EZPhotoPickConfig;

    PhotoIntentHelperContract.Presenter presenter;
    View loadingView;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenOrientation = getIntent().getIntExtra(PhotoIntentConstants.SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setRequestedOrientation(screenOrientation);

        setContentView(R.layout.photo_pick_activity);
        loadingView = findViewById(R.id.loading_view);
        EZPhotoPickConfig = (EZPhotoPickConfig) getIntent().getSerializableExtra(PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY);
        presenter = new PhotoIntentHelperPresenter(this,
                new PhotoUriHelper(this),
                new PhotoGenerator(this),
                PhotoIntentHelperStorage.getInstance(this), EZPhotoPickConfig);
        try {
            presenter.onCreate(savedInstanceState);
        } catch (PhotoIntentException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishPickPhotoWithSuccessResult() {
        setResult(RESULT_OK);
        finishedWithoutAnimation();
    }

    private void finishedWithoutAnimation() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showPickPhotoFromGalleryError(int unexpectedErrorStringResource) {
        String message = "Unexpected error, please try again";
        if (unexpectedErrorStringResource != 0) {
            message = getString(unexpectedErrorStringResource);
        }
        Toast.makeText(PhotoIntentHelperActivity.this, message
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishWithNoResult() {
        finishedWithoutAnimation();
    }

    @Override
    public void requestCameraAndExternalStoragePermission(boolean needToAddToGallery) {
        String[] permissions;
        if (needToAddToGallery) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA};
        }
        ActivityCompat.requestPermissions(PhotoIntentHelperActivity.this,
                permissions,
                REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoIntentContentProvider.getContentUri(this));
        startActivityForResult(takePictureIntent, PICK_PHOTO_FROM_CAMERA);
    }

    @Override
    public void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(PhotoIntentHelperActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void showToastMessagePermissionDenied(int permisionDeniedErrorStringResource) {
        String message = "Permission denied, cannot complete the action";
        if (permisionDeniedErrorStringResource != 0) {
            message = getString(permisionDeniedErrorStringResource);
        }
        Toast.makeText(PhotoIntentHelperActivity.this, message
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendBroadcastToScanFileInGallery(File file) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length == 0) {
            presenter.onRequestPermissionDenied();
            return;
        }

        boolean isGrantedPermission = true;
        for (Integer grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGrantedPermission = false;
                break;
            }
        }

        if (!isGrantedPermission) {
            presenter.onRequestPermissionDenied();
            return;
        }

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                presenter.onRequestCameraPermissionGranted();
                break;
            }
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION: {
                presenter.onRequestReadExternalPermissionGranted();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            finishedWithoutAnimation();
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLERY) {
                presenter.onPhotoPickedFromGallery(data);
            } else if (requestCode == PICK_PHOTO_FROM_CAMERA) {
                presenter.onPhotoPickedFromCamera(getFilesDir());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
