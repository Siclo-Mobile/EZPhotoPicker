package siclo.com.photointenthelper.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelperConfig implements Serializable{

    public PhotoSource photoSource;
    /**
     * internal storage folder
     * for eg: "abc/def"
     */
    public String internalStorageDir;
    public int maxExportingSize;
    public ExtraAction extraAction;

    /**
     * Error message resource id
     * default is hard string by english
     * use it when u want to support multiple language
     */
    public int permisionDeniedErrorStringResource;
    public int unexpectedErrorStringResource;


    public interface ExtraAction{
        /**
         * Do anything in background if you want with the stored bitmap
         * while the loading dialog is still being shown
         * @param bitmap : Stored bitmap in internal storage
         */
        void doExtraAction(Bitmap bitmap);
    }
}
