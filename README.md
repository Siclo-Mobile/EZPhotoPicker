#EZPhotoPicker
If you want to pick up a photo from  the gallery and camera, store it somewhere then do something, this library will be the best choice for you. It will handle all the storing, scaling, rotating, threading, loading dialog. Easy to start a photo intent, easy to get the result, you won't need to code a lot as what you used to do.
 
It also help you to handle about realtime permission without any lines of code.

Try it, you will see what I mean.

##Intergrating gradle

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
compile 'com.siclo.ezphotopick:library:1.0.5'
```

##Usage
For full example, please refer to sample

####Start image picker activity

```javascript
EZPhotoPickConfig config = new EZPhotoPickConfig();
config.photoSource = PhotoSource.GALERY; // or PhotoSource.CAMERA
config.isAllowMultipleSelect = true; // only for GALERY pick and API >18
config.maxExportingSize = 1000;
EZPhotoPick.startPhotoPickActivity(MainActivity.this, config);
```
*For more configurations, check EZPhotoPickConfig class*

####Receive result `onActivityResult`
###### For simplest way, load and use the bitmap, and dont care about the photo name/path:
```
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_GALERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
              Bitmap pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
              //do something with the bitmap
        }
    }
```
###### Or you need the photo name
```javascript
    String photoName = data.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
    Bitmap pickedPhoto = ezPhotoPickStorage.loadStoredPhotoBitmap(photoDir, photoName, 300);
```
###### Or you need the absolute stored photo path for doing something
```javascript
    String photoName = data.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
    String photoPath = ezPhotoPickStorage.getAbsolutePathOfStoredPhoto(photoDir, photoName);
    //do something with the path
```
###### Or multiple photos (Only for galery pick)
```javascript
    ArrayList<String> pickedPhotoNames = data.getStringArrayListExtra(EZPhotoPick.PICKED_PHOTO_NAMES_KEY);
    for(String photoName: photoNames){
       Bitmap pickedPhoto = ezPhotoPickStorage.loadStoredPhotoBitmap(photoDir, photoName, 300);
    }
```
*For more public api for the storage, check EZPhotoPickStorage class*

##Contribution

####Questions
If you have any questions regarding EZPhotoPicker,create an Issue

####Feature request, new features?
We are still working on it to add more useful option/feature,
to create a new feature request, open an issue

I'll try to answer as soon as I find the time.

####Pull requests welcome

Feel free to contribute to EZPhotoPicker.

Either you found a bug or have created a new and awesome feature, just create a pull request.


##License
Copyright 2016 Siclo Mobile Vietnam
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
