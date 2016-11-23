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

    public void storeLatestStoredPhotoName(String randomPhotoName) {
        pathStorage.storeLatestStoredPhotoName(randomPhotoName);
    }

    public String loadLatestStoredPhotoName() {
        return pathStorage.loadLastetStoredPhotoName();
    }

    public void storeLatestStoredPhotoDir(String photoDir) {
        pathStorage.storeLastetStoredPhotoDir(photoDir);
    }

    public String loadLatestStoredPhotoDir() {
        return pathStorage.loadLastetStoredPhotoDir();
    }

    public Bitmap loadLatestStoredPhotoBitmap() throws IOException {
        return loadLatestStoredPhotoBitmap(0);
    }

    public Bitmap loadLatestStoredPhotoBitmap(int maxScaleSize) throws IOException {
        String storedPhotoDir = loadLatestStoredPhotoDir();
        String storedPhotoName = loadLatestStoredPhotoName();
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }
    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName) throws IOException {
        return loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, 0);
    }

    public Bitmap loadStoredPhotoBitmap(String storedPhotoDir, String storedPhotoName, int maxScaleSize) throws IOException {
        return photoInternalStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }
}
