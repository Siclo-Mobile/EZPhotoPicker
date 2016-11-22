package siclo.com.photointenthelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import siclo.com.photointenthelper.models.PhotoIntentException;
import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.storage.PhotoGenerator;
import siclo.com.photointenthelper.storage.PhotoIntentContentProvider;
import siclo.com.photointenthelper.storage.PhotoIntentHelperStorage;

import static siclo.com.photointenthelper.models.PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY;
import static siclo.com.photointenthelper.models.PhotoIntentConstants.SCREEN_ORIENTATION;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelperActivity
        extends AppCompatActivity implements PhotoIntentHelperContract.View {

    private static final int PICK_PHOTO_FROM_GALLERY = 1001;
    private static final int PICK_PHOTO_FROM_CAMERA = 1002;
    PhotoIntentHelperConfig photoIntentHelperConfig;

    PhotoIntentHelperContract.Presenter presenter;
    View loadingView;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenOrientation = getIntent().getIntExtra(SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setRequestedOrientation(screenOrientation);

        setContentView(R.layout.photo_pick_activity);
        loadingView = findViewById(R.id.loading_view);
        photoIntentHelperConfig = (PhotoIntentHelperConfig) getIntent().getSerializableExtra(PHOTO_PICK_CONFIG_KEY);
        presenter = new PhotoIntentHelperPresenter(this,
                new PhotoGenerator(this),
                PhotoIntentHelperStorage.getInstance(this), photoIntentHelperConfig);
        try {
            presenter.onCreate(savedInstanceState);
        } catch (PhotoIntentException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_FROM_GALLERY);
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishPickPhotoWithSuccessResult() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showPickPhotoFromGalleryError() {
        new AlertDialog.Builder(this).setMessage("Unexpected error, please try again");
    }

    @Override
    public void finishWithNoResult() {
        finish();
    }

    @Override
    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(PhotoIntentHelperActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoIntentContentProvider.CONTENT_URI);
        startActivityForResult(takePictureIntent, PICK_PHOTO_FROM_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onRequestCameraPermissionGranted();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    presenter.onRequestCameraPermissionDenied();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(PhotoIntentHelperActivity.this,
                            "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
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
