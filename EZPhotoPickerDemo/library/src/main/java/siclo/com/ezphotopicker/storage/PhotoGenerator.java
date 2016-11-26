package siclo.com.ezphotopicker.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;

/**
 * Created by ericta on 12/16/15.
 */
public class PhotoGenerator {

    private Context context;

    public PhotoGenerator(Context context) {
        this.context = context;
    }


    private int getCapturedExifOrientation(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            return 0;
        }
    }

    private int getImageOrientation(int exifOrientation) {
        int rotate = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            default:
                break;
        }
        return rotate;
    }

    private int getImageOrientation(String path) {
        int exif = getCapturedExifOrientation(path);
        return getImageOrientation(exif);
    }

    private Bitmap generatePhotoByPathWithMaxSize(String photoPath, int maxSize) throws FileNotFoundException {
        FileInputStream is = new FileInputStream(photoPath);
        BitmapFactory.Options bmOptions = generateBitmapFactoryOption(maxSize, is);
        is = new FileInputStream(photoPath);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    private Bitmap generatePhotoByUriWithMaxSize(Uri pickedStringURI, int maxSize) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(pickedStringURI);
        BitmapFactory.Options bmOptions = generateBitmapFactoryOption(maxSize, is);
        is = context.getContentResolver().openInputStream(pickedStringURI);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    @NonNull
    private BitmapFactory.Options generateBitmapFactoryOption(int maxSize, InputStream is) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        if (maxSize > 0) {
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            bmOptions.inSampleSize = Math.max(photoW / maxSize, photoH / maxSize);
        }
        bmOptions.inJustDecodeBounds = false;
        return bmOptions;
    }

    public Bitmap generatePhotoWithValue(String photoPath, int maxSize) throws IOException {
        Bitmap generatingPhotoBitmap = generatePhotoByPathWithMaxSize(photoPath, maxSize);
        generatingPhotoBitmap = scalePhotoByMaxSize(maxSize, generatingPhotoBitmap);
        return generatingPhotoBitmap;
    }


    public Bitmap generatePhotoWithValue(Uri pickedStringURI, EZPhotoPickConfig eZPhotoPickConfig) throws IOException {
        Bitmap generatingPhotoBitmap = generatePhotoByUriWithMaxSize(pickedStringURI, eZPhotoPickConfig.exportingSize);
        generatingPhotoBitmap = scalePhotoByMaxSize(eZPhotoPickConfig.exportingSize, generatingPhotoBitmap);
        generatingPhotoBitmap = rotatePhotoIfNeed(generatingPhotoBitmap, pickedStringURI, eZPhotoPickConfig.needToRotateByExif);
        return generatingPhotoBitmap;
    }

    private Bitmap rotatePhotoIfNeed(Bitmap generatingPhotoBitmap, Uri pickedStringURI, boolean needToRotateByExif) {

        if(!needToRotateByExif){
            return generatingPhotoBitmap;
        }

        int rotate = getImageOrientation(pickedStringURI.getPath());
        if (rotate == 0) {
            return generatingPhotoBitmap;
        }
        /**
         * rotate before return result
         */
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        generatingPhotoBitmap = Bitmap.createBitmap(generatingPhotoBitmap, 0, 0, generatingPhotoBitmap.getWidth(),
                generatingPhotoBitmap.getHeight(), matrix, true);
        return generatingPhotoBitmap;
    }

    public Bitmap scalePhotoByMaxSize(int maxSize, Bitmap generatingPhotoBitmap) {

        if (maxSize <= 0) {
            return generatingPhotoBitmap;
        }

        int photoW = generatingPhotoBitmap.getWidth();
        int photoH = generatingPhotoBitmap.getHeight();

        if (photoW <= maxSize && photoH <= maxSize) {
            return generatingPhotoBitmap;
        }
        float scaleSize = Math.max((float) photoW / maxSize, (float) photoH / maxSize);
        generatingPhotoBitmap = Bitmap.createScaledBitmap(generatingPhotoBitmap,
                (int) (photoW / scaleSize), (int) (photoH / scaleSize),
                true);
        return generatingPhotoBitmap;
    }

}
