package siclo.com.photointenthelper.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class PhotoInternalStorage {

    private final Context context;

    public PhotoInternalStorage(Context context) {
        this.context = context;
    }

    public void storePhotoBitmap(Uri photoUri, Bitmap bitmapImage, String internalStorageDir, String fileName) {
        String type = getMimeType(photoUri);

        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        if("PNG".equalsIgnoreCase(type)){
            compressFormat = Bitmap.CompressFormat.PNG;
        }

        File photoPath = getPhotoByName(internalStorageDir, fileName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(compressFormat, 100, fos);

            fos.close();
        } catch (Exception e) {
        }
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


    public Bitmap loadPhoto(String fileName) throws FileNotFoundException {
        File photoPath = getPhotoByName(null, fileName);
        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(photoPath));
        return photo;
    }

    public Bitmap loadPhoto(String internalStorageDir, String fileName) throws FileNotFoundException {
        File photoPath = getPhotoByName(internalStorageDir, fileName);
        Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(photoPath));
        return photo;
    }

    @NonNull
    private File getPhotoByName(String internalStorageDir, String attachmentId) {
        File file;
        if (TextUtils.isEmpty(internalStorageDir)) {
            file = new File(context.getFilesDir().getAbsolutePath());
        } else {
            file = new File(context.getFilesDir().getAbsolutePath() + "/" + internalStorageDir);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, attachmentId);
    }

    public Bitmap loadStoredPhotoBitmap(String internalStorageDir,String storedPhotoName) {
        try {
            return loadPhoto(internalStorageDir, storedPhotoName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public boolean isAttachmentExist(String attachmentId) {
//        return getPhotoByName(attachmentId).exists();
//    }
}
