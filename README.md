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
Monkey UIKit use a photoviewer to show the photos that you send and receive. If you want to use it you need declare PhotoViewActivity in your manifest:
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
Monkey UIKIT use several components:
- MonkeyAdapter (RecyclerView.Adapter)
- MonkeyHolder (RecyclerView.ViewHolder)
- MonkeyView (Bubbles)
- MonkeyItem 
- ChatActivity
- BaseInputView

### MonkeyAdapter
We use a custom Adapter called MonkeyAdapter for the RecyclerView. The MonkeyAdapter guides the way the messages are shown in the UI. We have written useful methods:
- addNewData
- smoothlyAddNewData
- smoothlyAddNewItem

### MonkeyHolder
We use a custom ViewHolder called MonkeyHolder for the RecyclerView. MonkeyHolder contain a reference to all common controls(TextView, ImageView, etc) we have to handle in our layout. We have some classes that extends of MonkeyHolder for example: MonkeyTextHolder, MonkeyImageHolder, etc.
[Example here](monkeykitui/src/main/kotlin/com/criptext/monkeykitui/recycler/holders/MonkeyTextHolder.kt)

### MonkeyView
The class MonkeyView is a custom View that contains all the common controls and methods that the bubbles has. For example: datetimeTextView, errorImageView, sendingProgressBar, etc. We have some classes that extends of MonkeyView for example: AudioMessageView, TextMessageView, etc.
[Example here](monkeykitui/src/main/kotlin/com/criptext/monkeykitui/bubble/TextMessageView.kt)

### MonkeyItem
To create your chat activity you need to create a Class that represent a message. This class has to implement an interface called MonkeyItem and implement his methods.
[Example here](app/src/main/java/com/criptext/uisample/MessageItem.java)

### ChatActivity
To create a chat activity you need to have an activity and make it implements the interface ChatActivity. This interface has useful methods that you have to implement.
[Example here](app/src/main/java/com/criptext/uisample/MainActivity.java)

### BaseInputView
Monkey UIKIT provides you with differents InputView: 
- **TextInputView:** View that implements an editText and a send button
- **AttachmentInputView:** View that implements the TextInputView and an attachment button with custom options
- **AudioInputView:** View that implements the TextInputView and an audio button that implements methods to record audio
- **MediaInputView:** View that implements all the InputViews

To add your InputView inside your layout just add these lines:
```
<com.criptext.monkeykitui.input.TextInputView
        android:id="@+id/inputView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/white"/>
```
Implementation in your activity:
```
final TextInputView inputTextView = (TextInputView) findViewById(R.id.inputView);
if(inputTextView != null) {
    inputTextView.setOnSendButtonClickListener(new OnSendButtonClickListener() {
        @Override
        public void onSendButtonClick(String text) {
            addTextMessageToConversation(text);
        }
    });
}
```
In order to create your own InputView you need to create a Class that extends the Class BaseInputView and override two optional methods: **setRightButton** and **setLeftButton**. These methods help you to implement your own buttons inside the InputView.





