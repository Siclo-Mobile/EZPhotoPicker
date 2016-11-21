package siclo.com.photointenthelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.models.PhotoIntentException;
import siclo.com.photointenthelper.storage.PhotoGenerator;
import siclo.com.photointenthelper.storage.PhotoIntentHelperStorage;

import static siclo.com.photointenthelper.models.PhotoIntentConstants.PHOTO_PICK_CONFIG_KEY;
import static siclo.com.photointenthelper.models.PhotoIntentConstants.SCREEN_ORIENTATION;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelperActivity
        extends AppCompatActivity implements PhotoIntentHelperContract.View{

    private static final int PICK_PHOTO_FROM_GALLERY = 1001;
    PhotoIntentHelperConfig photoIntentHelperConfig;

    PhotoIntentHelperContract.Presenter presenter;
    View loadingView;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenOrientation =  getIntent().getIntExtra(SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Log.i("TCV","screenOrientation "+screenOrientation);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLERY) {
                presenter.onPhotoPickedFromGallery(data);
            }
//            else if (requestCode == PhotoFragment.PICK_PHOTO_FROM_CAMERA) {
//                getContext().getContentResolver().notifyChange(PhotoUtils.currentCapturingPhotoURI, null);
//                ContentResolver cr = getContext().getContentResolver();
//                Bitmap bitmap;
//                try
//                {
//                    bitmap = PhotoUtils.generatePhotoWithValue(PhotoUtils.currentCapturingPhotoURI, getResources().getDimensionPixelSize(R.dimen.image_adjust_size));
//                    presenter.onPhotoPicked(bitmap);
//                }
//                catch (Exception e)
//                {
//                    Log.d("TCV", "Failed to load", e);
//                }
//            }
        }
    }
}
