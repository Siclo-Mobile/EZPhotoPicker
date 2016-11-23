package siclo.com.photointenthelperdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

import siclo.com.photointenthelper.api.PhotoIntentHelper;
import siclo.com.photointenthelper.api.PhotoIntentStorageLoader;
import siclo.com.photointenthelper.api.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.api.models.PhotoSource;


public class MainActivity extends AppCompatActivity {

    ImageView ivPickedPhoto;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPickedPhoto = (ImageView) findViewById(R.id.iv_photo);
        findViewById(R.id.bt_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoIntentHelperConfig config = new PhotoIntentHelperConfig();
                config.photoSource = PhotoSource.GALERY;
                config.maxExportingSize = 1000;
                PhotoIntentHelper.startPhotoIntentHelperActivity(MainActivity.this, config);
            }
        });
        findViewById(R.id.bt_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoIntentHelperConfig config = new PhotoIntentHelperConfig();
                config.photoSource = PhotoSource.CAMERA;
                config.maxExportingSize = 1000;
                PhotoIntentHelper.startPhotoIntentHelperActivity(MainActivity.this, config);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == PhotoIntentHelper.PHOTO_PICK_REQUEST_CODE){
            try {
                Bitmap pickedPhoto = new PhotoIntentStorageLoader(this).loadLatestStoredPhotoBitmap();
                ivPickedPhoto.setImageBitmap(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
