package siclo.com.photointenthelper.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class PhotoInternalStorage {


//    public static final String PHOTO_DIR = "Photos";

    private final Context context;

    public PhotoInternalStorage(Context context) {
        this.context = context;
    }

    public void storePhotoBitmap(Bitmap bitmapImage, String internalStorageDir, String fileName) {
        File photoPath = getPhotoByName(internalStorageDir, fileName);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
        }
    }

    public Bitmap loadPhoto(String fileName) throws FileNotFoundException {
        File photoPath = getPhotoByName(null, fileName);
        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(photoPath));
        return photo;
    }

    public Bitmap loadPhoto(String internalStorageDir, String fileName) throws FileNotFoundException {
        File photoPath = getPhotoByName(internalStorageDir, fileName);
        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(photoPath));
        return photo;
    }

    @NonNull
    private File getPhotoByName(String internalStorageDir, String attachmentId) {
        File file;
        if (TextUtils.isEmpty(internalStorageDir)) {
            file = new File(context.getFilesDir().getAbsolutePath());
        } else {
            file = new File(context.getFilesDir().getAbsolutePath() + "/" + internalStorageDir);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, attachmentId);
    }

    public Bitmap loadStoredPhotoBitmap(String internalStorageDir,String storedPhotoName) {
        try {
            return loadPhoto(internalStorageDir, storedPhotoName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public boolean isAttachmentExist(String attachmentId) {
//        return getPhotoByName(attachmentId).exists();
//    }
}
