# ImageDownloader

This app downloads images from any web page.

[![Watch the video](https://i.imgur.com/vKb2F1B.png)](https://drive.google.com/file/d/10SFaPUdW7hK5z0bRTmSlndQ9bNUJuggF/view?usp=sharing)
[![Watch the video](https://img.youtube.com/vi/T-D1KVIuvjA/maxresdefault.jpg)](https://youtu.be/T-D1KVIuvjA)


This repo has been created for the assignment purpose. More detail is given below.

- Please design and implement an Image download application for Android in Java or Kotlin.
- This application will allow the user to download all images from a web page and store them onto the device's file system.

Main requirements:
 - The application should provide a user interface to allow the user to enter a URL to a web page.
 - The application should provide the user with a preview of the web page.
 - The application should provide the user the choice of where to save the image. Default to external SD card if it exists, otherwise default to the camera
   gallery.
 - The application should provide an action trigger on the preview UI to start the download.
 - The application should provide feedback on the progress of the download.
 - The application should detect if there is no network connection and inform the user.
 - The application should run on Android Marshmallow and above.
 - The application should adopt the Marshmallow permission model.
 - Use of open source code or third party library is allowed.

Nice to have:
 - Create UI tests for the application using Espresso
 - Using AndroidX library instead of a Support Library
 - Using ViewModel
 - Using Coroutine if using Kotlin
 - Support rotation
 
 
 Solution:
  - I have tested the solution on galaxy s10 ( android 10 ) working fine on it. 
  - I don't have an external SD card so I skipped that part. Right now saving images in the device storage.
  - The primary focus was to cover the main requirements, not the UI.
  - I have attached a video URL just for reference. 
  
    https://drive.google.com/file/d/10SFaPUdW7hK5z0bRTmSlndQ9bNUJuggF/view?usp=sharing
