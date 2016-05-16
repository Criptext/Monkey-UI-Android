# Getting Started

Monkey UIKit on Android supports 3.0 (API 11) and above.

## Using Gradle + Maven

In your app's build.gradle file add the jitpack url to your repositories block:
```
repositories {
    ...

    maven { url "https://jitpack.io" }

}
```

Then add the following to your app's build.gradle file dependencies block:
```
dependencies {

    compile 'com.github.GAumala.MonkeyUIAndroid:monkeykitui:dev-SNAPSHOT'
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

```
<application
    ...
    <activity
        android:name="com.criptext.monkeykitui.photoview.PhotoViewActivity"
        android:theme="@style/Theme.CustomTranslucent"/>
    ...
</application>
```

Second, right after `PhotoViewActivity` declare these two activies to let the 
user edit a photo before sending it:

```
    <!-- Crop Image -->
    <activity
        android:name="com.soundcloud.android.crop.CropImageActivity"
        android:theme="@style/CustomTheme"/>

    <!-- Edit Photo -->
    <activity
        android:name="com.criptext.monkeykitui.input.photoEditor.PhotoEditorActivity" />
```

# The Basics

The layout for a chat needs at least two views: A `RecyclerView` and a
`BaseInputView`, which is a custom view provided by `MonkeyKit`  that contains an
`EditText` and other useful components needed to create messages.

![Chat with RecyclerView and
MediaInputView](https://cloud.githubusercontent.com/assets/14115856/14875816/22e457e2-0cd4-11e6-8096-add2cd2a3f20.jpeg)

The layout file for this chat can be found
[here.](https://github.com/Criptext/MonkeyUIAndroid/blob/master/app/src/main/res/layout/activity_main.xml)
`FrameLayout`is the recommended container since the input view may have hidden
views that need more space when they are revealed.

We also provide a custom adapter for `RecyclerView` called `MonkeyAdapter` that
does all the wiring for you. All you need to do is implement `MonkeyItem` in
your message class and `ChatActivity` in your activity.

## ChatActivity

`MonkeyAdapter` may need to interact with your activity in certain events or to
gather additional data. To instatiate `MonkeyAdapter` you must pass a `Context`
reference that implements `ChatActivity`. For more information about the methods
 you must implement please see the [ChatActivity source code with 
documentation.](https://github.com/Criptext/MonkeyUIAndroid/blob/master/monkeykitui/src/main/kotlin/com/criptext/monkeykitui/recycler/ChatActivity.kt)
You can also find an example [here.](app/src/main/java/com/criptext/uisample/MainActivity.java)


## MonkeyAdapter and MonkeyItem

No matter how you design the class that holds each individual chat message in
your app, integration with our UI Kit is a breeze. Just implement the the
`MonkeyItem` interface in your class, instantiate  a `MonkeyAdapter` class 
using an `ArrayList` of your `MonkeyItem` messages and when you set the adapter to
the `RecyclerView` your messages will be displayed on screen. For more
information about the methods you must implement please see the [MonkeyItem 
source code with 
documentation.](https://github.com/Criptext/MonkeyUIAndroid/blob/master/monkeykitui/src/main/kotlin/com/criptext/monkeykitui/recycler/MonkeyItem.kt)
You can also find an implementation example [here.](app/src/main/java/com/criptext/uisample/MessageItem.java)
##InputView

A subclass of `BaseInputView` is needed in your layout for the user to be able
to compose new messages. You could create your own, or use one of our defaults:

- **TextInputView:** only sends text.
- **AttachmentInputView:** sends text and attachments such as photos.
- **AudioInputView:** sends text and voice notes.
- **MediaInputView:** sends text, attachments and voicenotes. It is a
  combination of `AttachmentInputView` and `MediaInputView.

For your activity to listen to new messages to send, set the appropiate
listener to the InputView. You can also extend any of these views to add any
behaviour that you wish. By overriding methods `setLeftButton()` and
`setRightButton()` you can change the buttons at the left and right of the
`InputView`.

After following all these steps you'll have the UI ready for a messaging
application. For a full implementation please see our sample app's source code
[here,](app/src/main/java/com/criptext/uisample/) or clone this repository and
build it with Android Studio.

