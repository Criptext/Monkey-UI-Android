
# Android UI

Monkey UIKit on Android supports 3.0 (API 11) and above.

## Using Jitpack

In your app's build.gradle file add the jitpack url to your repositories block:
```
repositories {
    ...

    maven { url "https://jitpack.io" }

}
```

Then add the following to your app's `build.gradle` file dependencies block:
```
dependencies {

    compile 'com.github.Criptext:MonkeyUIAndroid:1.3.1'
}
```

## Edit your Manifest.xml
By default, Monkey UIKit uses its own activity to display sent and received 
photos in conversations. MonkeyKit also provides another activity to edit 
photos before sending. These activities must be declared in your manifest so
that your app can launch them. If you do not wish to have photos in your chat 
you can skip this step. 

First, declare `PhotoViewActivity` in your manifest so that your app 
can start it via intent when the user clicks on a photo:

```xml
<application>
    ...
    <activity
        android:name="com.criptext.monkeykitui.photoview.PhotoViewActivity"/>
    ...
</application>
```

Second, right after `PhotoViewActivity` declare these two activies to let the 
user edit a photo before sending it:

```xml
    <!-- Crop Image -->
    <activity
        android:name="com.soundcloud.android.crop.CropImageActivity"/>

    <!-- Edit Photo -->
    <activity
        android:name="com.criptext.monkeykitui.input.photoEditor.PhotoEditorActivity" />
```

Finally, don't forget to add the necessary permissions:
```xml
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```

# The Basics

The layout for a chat needs at least two views: A `RecyclerView` and a
`BaseInputView`, which is a custom view provided by `MonkeyKit`  that contains an
`EditText` and other useful components needed to create messages.

![Chat with RecyclerView and
MediaInputView](https://cloud.githubusercontent.com/assets/14115856/14875816/22e457e2-0cd4-11e6-8096-add2cd2a3f20.jpeg)

The layout file for this chat can be found
[here.](https://github.com/Criptext/MonkeyUIAndroid/blob/master/app/src/main/res/layout/activity_main.xml) For convenience, this layout has been encapsulated in `MonkeyChatFragment`, which is the recommended way of integrating the Monkey SDK to your application, although you can still use a regular activity with the previous layout.
`FrameLayout`is the recommended container since the input view may have hidden
views that need more space when they are revealed.

We also provide a custom adapter for `RecyclerView` called `MonkeyAdapter` that
does all the wiring for you. All you need to do is implement `MonkeyItem` in
your message class and `ChatActivity` in your activity.

