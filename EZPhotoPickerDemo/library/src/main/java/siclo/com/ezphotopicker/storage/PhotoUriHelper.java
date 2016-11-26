package siclo.com.ezphotopicker.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by ericta on 11/25/16.
 */

public class PhotoUriHelper {

    private Context context;

    public PhotoUriHelper(Context context) {
        this.context = context;
    }

    public Bitmap.CompressFormat getUriPhotoBitmapFormat(Uri photoUri) {
        String type = getMimeType(photoUri);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        if ("PNG".equalsIgnoreCase(type)) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        return compressFormat;
    }

    private String getMimeType(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
}
