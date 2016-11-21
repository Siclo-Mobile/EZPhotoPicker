package siclo.com.photointenthelper.models;

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
    public int scaleSize;
}
