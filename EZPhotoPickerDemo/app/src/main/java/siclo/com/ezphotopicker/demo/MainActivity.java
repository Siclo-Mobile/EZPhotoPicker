package siclo.com.ezphotopicker.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import siclo.com.ezphotopicker.R;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;


public class MainActivity extends AppCompatActivity {

    private static final String DEMO_PHOTO_PATH = "MyDemoPhotoDir";

    LinearLayout llPhotoContainer;
    EZPhotoPickStorage ezPhotoPickStorage;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llPhotoContainer = (LinearLayout) findViewById(R.id.photo_container);
        findViewById(R.id.bt_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EZPhotoPickConfig config = new EZPhotoPickConfig();
                config.photoSource = PhotoSource.GALERY;
                config.needToExportThumbnail = true;
                config.isAllowMultipleSelect = true;
                config.storageDir = DEMO_PHOTO_PATH;
                config.exportingThumbSize = 200;
                config.exportingSize = 1000;
                EZPhotoPick.startPhotoPickActivity(MainActivity.this, config);
            }
        });
        findViewById(R.id.bt_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EZPhotoPickConfig config = new EZPhotoPickConfig();
                config.photoSource = PhotoSource.CAMERA;
                config.storageDir = DEMO_PHOTO_PATH;
                config.needToAddToGallery = true;
                config.exportingSize = 1000;
                EZPhotoPick.startPhotoPickActivity(MainActivity.this, config);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == EZPhotoPick.PHOTO_PICK_REQUEST_CODE){
            try {
                ArrayList<String> pickedPhotoNames = data.getStringArrayListExtra(EZPhotoPick.PICKED_PHOTO_NAMES_KEY);
                showPickedPhotos(DEMO_PHOTO_PATH ,pickedPhotoNames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPickedPhotos(String photoDir, List<String> photoNames) throws IOException {
        llPhotoContainer.removeAllViews();
        if(ezPhotoPickStorage == null){
            ezPhotoPickStorage = new EZPhotoPickStorage(this);
        }

        for(String photoName: photoNames){
            Bitmap pickedPhoto = ezPhotoPickStorage.loadStoredPhotoBitmap(photoDir, photoName, 300);
            showPickedPhoto(pickedPhoto);
        }
    }

    private void showPickedPhoto(Bitmap pickedPhoto) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(pickedPhoto);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        llPhotoContainer.addView(iv);
    }

}
