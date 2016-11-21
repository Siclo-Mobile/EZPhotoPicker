package siclo.com.photointenthelperdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import siclo.com.photointenthelper.PhotoIntentHelper;
import siclo.com.photointenthelper.models.PhotoIntentHelperConfig;
import siclo.com.photointenthelper.models.PhotoIntentConstants;
import siclo.com.photointenthelper.models.PhotoSource;
import siclo.com.photointenthelper.storage.PhotoIntentHelperStorage;

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
                config.scaleSize = 1000;
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

        if(requestCode == PhotoIntentConstants.PHOTO_PICK_REQUEST_CODE){
            Bitmap pickedPhoto = PhotoIntentHelperStorage.getInstance(this).loadLastestStoredPhotoBitmap();
            ivPickedPhoto.setImageBitmap(pickedPhoto);
        }

    }
}
