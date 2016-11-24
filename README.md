#EZPhotoPicker

A simple library to select images from the gallery and camera. It will help you for storing picked photo in background(with handle scaling big photo), loading picked photo and reuseable everytime you need it.

###Intergrating gradle

Add this to your module's build.gradle
```javascript
repositories {
    mavenCentral()
    maven {
        url 'https://dl.bintray.com/siclo/SicloAndroidOSS'
    }
}
```

```javascript
compile 'com.siclo.ezphotopick:library:1.0.1'
```

###Usage
For full example, please refer to sample

####Start image picker activity

```javascript
EZPhotoPickConfig config = new EZPhotoPickConfig();
config.photoSource = PhotoSource.GALERY; // or PhotoSource.CAMERA
config.maxExportingSize = 1000;
EZPhotoPick.startPhotoPickActivity(MainActivity.this, config);
```
*For more configurations, check EZPhotoPickConfig class*

####Receive result

```javascript
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == EZPhotoPick.PHOTO_PICK_REQUEST_CODE){
            try {
                Bitmap pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                ivPickedPhoto.setImageBitmap(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```
*For more public api for the storage, check EZPhotoPickStorage class*

####Mofidification License
