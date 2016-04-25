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
    compile ('comcom.criptext:monkeyKit:1.2.2@aar') {
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