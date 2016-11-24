package siclo.com.ezphotopicker.models;

/**
 * Created by ericta on 11/13/16.
 */

public class PhotoIntentException extends Exception {

    public static PhotoIntentException getNullPhotoPickConfigException(){
        return new PhotoIntentException("You are missing the config for photo pick action");
    }

    public PhotoIntentException(String message) {
        super(message);
    }
}
