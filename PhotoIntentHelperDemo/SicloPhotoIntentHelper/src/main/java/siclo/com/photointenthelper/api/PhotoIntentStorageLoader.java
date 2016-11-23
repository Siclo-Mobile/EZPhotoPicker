package siclo.com.photointenthelper.api;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

import siclo.com.photointenthelper.storage.PhotoIntentHelperStorage;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentStorageLoader {

    private PhotoIntentHelperStorage photoIntentHelperStorage;

    public PhotoIntentStorageLoader(Context context) {
        photoIntentHelperStorage = PhotoIntentHelperStorage.getInstance(context);
    }

    public String loadLastestStoredPhotoName() {
        return photoIntentHelperStorage.loadLastestStoredPhotoName();
    }

    public String loadLastetStoredPhotoDir() {
        return photoIntentHelperStorage.loadLastetStoredPhotoDir();
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
        return photoIntentHelperStorage.loadStoredPhotoBitmap(storedPhotoDir, storedPhotoName, maxScaleSize);
    }
}
