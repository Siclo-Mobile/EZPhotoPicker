package siclo.com.ezphotopicker.storage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by ericta on 11/22/16.
 */

public class PhotoIntentContentProvider extends ContentProvider {

    public static final String TEMP_PHOTO_NAME = "temp_photo.jpg";
    private static Uri CONTENT_URI;
    private static final HashMap<String, String> MIME_TYPES =
            new HashMap<>();

    static {
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".jpeg", "image/jpeg");
    }

    public static Uri getContentUri(Context context) {
        if (CONTENT_URI == null) {
            String packageId = context.getPackageName();
            CONTENT_URI = Uri.parse
                    ("content://" + packageId + ".ezphotopicker.provider/");
        }
        return CONTENT_URI;
    }

    @Override
    public boolean onCreate() {

        try {
            File mFile = new File(getContext().getFilesDir(), TEMP_PHOTO_NAME);
            if (!mFile.exists()) {
                mFile.createNewFile();
            }
            Log.i("TCV", "CONTENT_URI " + getContentUri(getContext()));
            getContext().getContentResolver().notifyChange(getContentUri(getContext()), null);
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public String getType(Uri uri) {
        String path = uri.toString();

        for (String extension : MIME_TYPES.keySet()) {
            if (path.endsWith(extension)) {
                return (MIME_TYPES.get(extension));
            }
        }
        return (null);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {

        File f = new File(getContext().getFilesDir(), TEMP_PHOTO_NAME);
        if (f.exists()) {
            return (ParcelFileDescriptor.open(f,
                    ParcelFileDescriptor.MODE_READ_WRITE));
        }
        throw new FileNotFoundException(uri.getPath());
    }
}
