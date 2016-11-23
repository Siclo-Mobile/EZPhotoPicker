package siclo.com.photointenthelper.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentHelperStorage {

    private PathStorage pathStorage;
    private PhotoInternalStorage photoInternalStorage;

    private static PhotoIntentHelperStorage photoIntentHelperStorage;


    public PhotoIntentHelperStorage(Context context) {
        pathStorage = new PathStorage(context);
        photoInternalStorage = new PhotoInternalStorage(context);
    }


    public static PhotoIntentHelperStorage getInstance(Context context) {
        if (photoIntentHelperStorage == null) {
            photoIntentHelperStorage = new PhotoIntentHelperStorage(context);
        }
        return photoIntentHelperStorage;
    }

    public Bitmap storePhotoBitmap(Uri photoUri, Bitmap pickingPhoto, String internalStorageDir, String randomPhotoName) {
        return photoInternalStorage.storePhotoBitmap(photoUri, pickingPhoto, internalStorageDir, randomPhotoName);
    }

    public void storeLastetStoredPhotoName(String randomPhotoName) {
        pathStorage.storeLastestStoredPhotoName(randomPhotoName);
    }

    public String loadLastestStoredPhotoName() {
        return pathStorage.loadLastetStoredPhotoName();
    }

    public void storeLastetStoredPhotoDir(String photoDir) {
        pathStorage.storeLastetStoredPhotoDir(photoDir);
    }

    public String loadLastetStoredPhotoDir() {
        return pathStorage.loadLastetStoredPhotoDir();
    }


    public Bitmap loadLastestStoredPhotoBitmap() {
        String storedPhotoDir = loadLastetStoredPhotoDir();
        String storedPhotoName = loadLastestStoredPhotoName();
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName) {
        return photoInternalStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName);
    }
}
