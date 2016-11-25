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
####License
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
