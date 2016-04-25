# Getting Started

Monkey UIKit on Android supports 3.0 (API 11) and above.

## Using Gradle + Maven

In your app's build.gradle file add the following block to your repositories block:
```
repositories { 
    maven {
        url 'https://dl.bintray.com/criptext/maven'
    } 
}
```

Then add the following to your app's build.gradle file dependencies block:
```
dependencies {
    compile ('com.criptext.monkeykitui:monkeykitui:1.3@aar') {
        transitive = true;
    }
}
```

## Edit your Manifest.xml
Monkey UIKit uses a photoviewer to show the photos that you send and receive. If you want to use it you need declare PhotoViewActivity in your manifest:
```
<application
    ...
    <activity
        android:name="com.criptext.monkeykitui.photoview.PhotoViewActivity"
        android:theme="@style/Theme.CustomTranslucent"/>
    ...
</application>
```
## Components
Monkey UIKIT uses several components:
- MonkeyAdapter (RecyclerView)
- MonkeyHolder (RecyclerView.ViewHolder)
- MonkeyView (Bubbles)
- MonkeyItem 
- ChatActivity
- BaseInputView

### BaseInputView
Monkey UIKIT provides you with differents InputView: 
- **TextInputView:** View that implements an editText and a send button
- **AttachmentInputView:** View that implements the TextInputView and an attachment button with custom options
- **AudioInputView:** View that implements the TextInputView and an audio button that implements methods to record audio
- **MediaInputView:** View that implements all the InputViews

In order to create your own InputView you need to create a Class that extends the Class BaseInputView and override two optional methods: **setRightButton** and **setLeftButton**. These methods help you to implement your own buttons inside the InputView.

To add your InputView inside your layout just add these lines:
```
<com.criptext.monkeykitui.input.TextInputView
        android:id="@+id/inputView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/white"/>
```





