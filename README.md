#EZPhotoPicker

A simple library to pick photo from the gallery and camera.
It also handle all the storing, scaling, rotating, threading, loading for you.
Easy to start a photo intent, easy to get the result, you won't need to code a lot as what you use to do.
Let's try it, you will see what I mean.

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
compile 'com.siclo.ezphotopick:library:1.0.1'
```

##Usage
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
