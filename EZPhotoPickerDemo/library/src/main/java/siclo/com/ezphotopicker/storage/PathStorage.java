package siclo.com.ezphotopicker.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ericta on 11/13/16.
 */

class PathStorage {

    public static final String PHOTO_PICK_SHARE_PREF = "PhotoPref";
    public static final String PHOTO_NAME_KEY = "PhotoName";
    public static final String PHOTO_DIR_KEY = "PhotoDir";



    private Context context;

    public PathStorage(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences(String prefKey) {
        return context
                .getSharedPreferences(prefKey, Context.MODE_PRIVATE);
    }

    public void storeLatestStoredPhotoName(String photoName){
        storeString(PHOTO_NAME_KEY, photoName);
    }

    public String loadLastetStoredPhotoName() {
        return loadStringByKey(PHOTO_NAME_KEY);
    }

    public void storeLastetStoredPhotoDir(String photoDir){
        storeString(PHOTO_DIR_KEY, photoDir);
    }

    public String loadLastetStoredPhotoDir() {
        return loadStringByKey(PHOTO_DIR_KEY);
    }

    private void storeString(String key, String value) {
        SharedPreferences betaPref = getSharedPreferences(PHOTO_PICK_SHARE_PREF);
        betaPref.edit().putString(key, value).commit();
    }


    private String loadStringByKey(String key) {
        SharedPreferences betaPref = getSharedPreferences(PHOTO_PICK_SHARE_PREF);
        return betaPref.getString(key, null);
    }

    private String loadStringByKey(String key, String defaul) {
        SharedPreferences betaPref = getSharedPreferences(PHOTO_PICK_SHARE_PREF);
        return betaPref.getString(key, defaul);
    }


}
