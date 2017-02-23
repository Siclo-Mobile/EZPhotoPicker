package siclo.com.ezphotopicker.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class PhotoInternalStorage {

    private final Context context;

    public PhotoInternalStorage(Context context) {
        this.context = context;
    }

    public Bitmap storePhotoBitmap(Bitmap bitmapImage, Bitmap.CompressFormat compressFormat ,String internalStorageDir, String fileName) {
        File photoPath = getPhotoFile(internalStorageDir, fileName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(compressFormat, 100, fos);
            fos.close();
        } catch (Exception e) {
        }
        return bitmapImage;
    }



    public Bitmap loadPhoto(String internalStorageDir, String fileName, int maxScaleSize) throws IOException {
        File photoPath = getPhotoFile(internalStorageDir, fileName);
        PhotoGenerator photoGenerator = new PhotoGenerator(context);
        return photoGenerator.generatePhotoWithValue(photoPath.getAbsolutePath(), maxScaleSize);
    }


    @NonNull
    private File getPhotoFile(String internalStorageDir, String fileName) {
        File file;
        if (TextUtils.isEmpty(internalStorageDir)) {
            file = new File(context.getFilesDir().getAbsolutePath());
        } else {
            file = new File(context.getFilesDir().getAbsolutePath() + "/" + internalStorageDir);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, fileName);
    }

    public Bitmap loadStoredPhotoBitmap(String internalStorageDir, String storedPhotoName, int maxScaleSize) throws IOException {
        return loadPhoto(internalStorageDir, storedPhotoName, maxScaleSize);
    }

    public boolean removePhoto(String storedPhotoDir, String storedPhotoName) {
        File file = getPhotoFile(storedPhotoDir, storedPhotoName);
        if (file.exists()) {
           return file.mkdirs();
        }
        return false;
    }

    public String getAbsolutePathOfStoredPhoto(String storedPhotoDir, String storedPhotoName) {
        return getPhotoFile(storedPhotoDir, storedPhotoName).getAbsolutePath();
    }
}
