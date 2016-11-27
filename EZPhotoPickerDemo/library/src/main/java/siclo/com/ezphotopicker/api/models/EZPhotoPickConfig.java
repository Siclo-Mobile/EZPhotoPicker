package siclo.com.ezphotopicker.api.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import siclo.com.ezphotopicker.models.PhotoIntentConstants;

/**
 * Created by ericta on 11/13/16.
 */

public class EZPhotoPickConfig implements Serializable {

    /**
     * Source: {@link PhotoSource}
     * CAMERA | GALLERY
     */
    public PhotoSource photoSource;
    /**
     * storage folder
     * for eg: "abc/def"
     */
    public String storageDir;

    /**
     * rotate the photo to right direction by exif value
     * it happen many times in Samsung phone
     * default True
     */
    public boolean needToRotateByExif = true;

    /**
     * export thumbnail photo in advance,
     * it is useful in case you want to display a list of thumbnail in the list view
     * and dont want to generate small thumb in realtime by function
     * {@link siclo.com.ezphotopicker.api.EZPhotoPickStorage#loadStoredPhotoBitmap(String, String, int targetBitmapSize)}
     * thumb will store in  {@link storageDir}
     * with suffix is {@link PhotoIntentConstants#THUMB_NAME_SUFFIX}
     * if you wont set thumbnail size, default value will be 200, thumbnail size must be smaller than
     * the original photo and small than {@link exportingSize}
     */
    public boolean needToExportThumbnail = false;
    public int exportingThumbSize = 200;

    /**
     * generate file name base on the current time
     * or keep it hard name as
     * {@link PhotoIntentConstants#TEMP_STORING_PHOTO_NAME}
     */
    public boolean isGenerateUniqueName = false;
    /**
     * the exported photo name if you do not want the app to random the name base on time
     * with {@link isGenerateUniqueName} = true or default name if false
     */
    public String exportedPhotoName = null;

    /**
     * exporting photo size to internal storage,
     * default is 0, mean original size
     */
    public int exportingSize = 0;

    /**
     * Error message resource id
     * default is hard string by english
     * use it when u want to support multiple language
     */
    public int permisionDeniedErrorStringResource;
    public int unexpectedErrorStringResource;


    public ExtraAction extraAction;
    public interface ExtraAction {
        /**
         * Do anything in background if you want with the stored bitmap
         * while the loading dialog is still being shown
         *
         * @param bitmap : Stored bitmap in internal storage
         * @param thumbnail : exported thumbnail
         */
        void doExtraAction(Bitmap bitmap, Bitmap thumbnail);
    }
}
