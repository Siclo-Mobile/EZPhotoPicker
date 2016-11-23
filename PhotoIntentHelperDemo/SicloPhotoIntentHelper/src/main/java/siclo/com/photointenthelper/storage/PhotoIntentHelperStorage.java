package siclo.com.photointenthelper.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

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

    public Bitmap loadLastestStoredPhotoBitmap() throws IOException {
        return loadLastestStoredPhotoBitmap(0);
    }

    public Bitmap loadLastestStoredPhotoBitmap(int maxScaleSize) throws IOException {
        String storedPhotoDir = loadLastetStoredPhotoDir();
        String storedPhotoName = loadLastestStoredPhotoName();
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }
    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName) throws IOException {
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, 0);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName, int maxScaleSize) throws IOException {
        return photoInternalStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }
}
