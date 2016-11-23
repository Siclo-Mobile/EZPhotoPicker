package siclo.com.photointenthelper.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

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

    public int getImageOrientation(String path) {
        int exif = getCapturedExifOrientation(path);
        return getImageOrientation(exif);
    }

    public Bitmap generatePhotoWithValue(Uri pickedStringURI, int maxSize) throws IOException {

        Bitmap generatingPhotoBitmap = scalePhotoWithMaxSampleSize(pickedStringURI, maxSize);

        int photoW = generatingPhotoBitmap.getWidth();
        int photoH = generatingPhotoBitmap.getHeight();
        generatingPhotoBitmap = scalePhotoByMaxSize(maxSize, photoW, photoH, generatingPhotoBitmap);

        int rotate = getImageOrientation(pickedStringURI.getPath());
        if (rotate == 0) {
            return generatingPhotoBitmap;
        }
        generatingPhotoBitmap = rotatePhotoByExif(generatingPhotoBitmap, rotate);

        return generatingPhotoBitmap;
    }

    private Bitmap rotatePhotoByExif(Bitmap generatingPhotoBitmap, int rotate) {
        /**
         * rotate before return result
         */
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        generatingPhotoBitmap = Bitmap.createBitmap(generatingPhotoBitmap, 0, 0, generatingPhotoBitmap.getWidth(),
                generatingPhotoBitmap.getHeight(), matrix, true);
        return generatingPhotoBitmap;
    }

    private Bitmap scalePhotoWithMaxSampleSize(Uri pickedStringURI, int maxSize) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(pickedStringURI);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        if(maxSize != 0){
            bmOptions.inJustDecodeBounds = false;
            BitmapFactory.decodeStream(is, null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            is = context.getContentResolver().openInputStream(pickedStringURI);
            bmOptions.inSampleSize = Math.max(photoW / maxSize, photoH / maxSize);
        }
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    private Bitmap scalePhotoByMaxSize(int maxSize, int photoW, int photoH, Bitmap generatingPhotoBitmap) {
        if(maxSize == 0){
            return generatingPhotoBitmap;
        }

        if (photoW < maxSize && photoH < maxSize) {
            return generatingPhotoBitmap;
        }
        float scaleSize = Math.max((float)photoW / maxSize, (float)photoH / maxSize);
        generatingPhotoBitmap = Bitmap.createScaledBitmap(generatingPhotoBitmap,
                (int) (photoW / scaleSize), (int) (photoH / scaleSize),
                true);
        return generatingPhotoBitmap;
    }

}
