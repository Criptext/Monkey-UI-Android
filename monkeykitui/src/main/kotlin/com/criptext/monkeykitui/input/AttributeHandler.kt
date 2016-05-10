package com.criptext.monkeykitui.input

import android.content.Intent
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 5/6/16.
 */

class AttributeHandler {

    companion object {
        val SEND_TEXT_DRAWABLE_INPUTVIEW = "AttributeHandler.sendTextDrawableInputView"
        val SEND_AUDIO_DRAWABLE_INPUTVIEW = "AttributeHandler.sendAudioDrawableInputView"
        val ATTACHMENT_DRAWABLE_INPUTVIEW = "AttributeHandler.attachmentDrawableInputView"
        val EDITTEXT_DRAWABLE_INPUTVIEW = "AttributeHandler.editTextDrawableInputView"
        val BACKGROUND_DRAWABLE_INPUTVIEW = "AttributeHandler.BackgroundDrawableInputView"

        val USE_DEFAULT_CAMERA_INPUTVIEW = "AttributeHandler.useDefaultCameraInputView"
        val USE_DEFAULT_GALLERY_INPUTVIEW = "AttributeHandler.useDefaultGalleryInputView"

        val OK_BUTTON_DRAWABLE_PHOTOEDITOR = "AttributeHandler.okButtonDrawablePhotoEditor"
        val OK_BUTTON_COLOR_PHOTOEDITOR = "AttributeHandler.okButtonColorPhotoEditor"
    }

    var sendTextDrawableInputView : Int = -1
    var sendAudioDrawableInputView: Int = -1
    var attachmentDrawableInputView: Int = -1
    var editTextDrawableInputView: Int = -1
    var backgroundDrawableInputView: Int = -1

    var useDefaultCameraInputView: Boolean = true
    var useDefaultGalleryInputView: Boolean = true

    var okButtonDrawablePhotoEditor: Int = -1
    var okButtonColorPhotoEditor: Int = -1

    constructor(array: TypedArray){
        sendTextDrawableInputView = array.getInt(R.styleable.InputView_sendTextDrawable, -1)
        sendAudioDrawableInputView = array.getInt(R.styleable.InputView_sendAudioDrawable, -1)
        attachmentDrawableInputView = array.getInt(R.styleable.InputView_attachmentDrawable, -1)
        editTextDrawableInputView = array.getInt(R.styleable.InputView_editTextDrawable, -1)
        backgroundDrawableInputView = array.getInt(R.styleable.InputView_backgroundDrawable, -1)

        useDefaultCameraInputView = array.getBoolean(R.styleable.InputView_useDefaultCamera, true)
        useDefaultGalleryInputView = array.getBoolean(R.styleable.InputView_useDefaultGallery, true)


        okButtonDrawablePhotoEditor = array.getInt(R.styleable.PhotoEditor_okButtonDrawable, -1)
        okButtonColorPhotoEditor = array.getInt(R.styleable.PhotoEditor_okButtonColor, -1)

    }

    constructor(intent: Intent){
        sendTextDrawableInputView = intent.getIntExtra(SEND_TEXT_DRAWABLE_INPUTVIEW, -1)
        sendAudioDrawableInputView = intent.getIntExtra(SEND_AUDIO_DRAWABLE_INPUTVIEW, -1)
        attachmentDrawableInputView = intent.getIntExtra(ATTACHMENT_DRAWABLE_INPUTVIEW, -1)
        editTextDrawableInputView = intent.getIntExtra(EDITTEXT_DRAWABLE_INPUTVIEW, -1)
        backgroundDrawableInputView = intent.getIntExtra(BACKGROUND_DRAWABLE_INPUTVIEW, -1)

        useDefaultCameraInputView = intent.getBooleanExtra(USE_DEFAULT_CAMERA_INPUTVIEW, true)
        useDefaultGalleryInputView = intent.getBooleanExtra(USE_DEFAULT_GALLERY_INPUTVIEW, true)

        okButtonDrawablePhotoEditor = intent.getIntExtra(OK_BUTTON_DRAWABLE_PHOTOEDITOR, -1)
        okButtonColorPhotoEditor = intent.getIntExtra(OK_BUTTON_COLOR_PHOTOEDITOR, -1)
    }

    fun addAttrsToIntent(intent: Intent){
        if(sendTextDrawableInputView != -1)
            intent.putExtra(SEND_TEXT_DRAWABLE_INPUTVIEW, sendTextDrawableInputView)
        if(sendAudioDrawableInputView != -1)
            intent.putExtra(SEND_AUDIO_DRAWABLE_INPUTVIEW, sendAudioDrawableInputView)
        if(attachmentDrawableInputView != -1)
            intent.putExtra(ATTACHMENT_DRAWABLE_INPUTVIEW, attachmentDrawableInputView)
        if(editTextDrawableInputView != -1)
            intent.putExtra(EDITTEXT_DRAWABLE_INPUTVIEW, editTextDrawableInputView)
        if(backgroundDrawableInputView != -1)
            intent.putExtra(BACKGROUND_DRAWABLE_INPUTVIEW, backgroundDrawableInputView)

        intent.putExtra(USE_DEFAULT_CAMERA_INPUTVIEW, useDefaultCameraInputView)
        intent.putExtra(USE_DEFAULT_GALLERY_INPUTVIEW, useDefaultGalleryInputView)

        if(okButtonDrawablePhotoEditor != -1)
            intent.putExtra(OK_BUTTON_DRAWABLE_PHOTOEDITOR, okButtonDrawablePhotoEditor)
        if(okButtonColorPhotoEditor != -1)
            intent.putExtra(OK_BUTTON_COLOR_PHOTOEDITOR, okButtonColorPhotoEditor)
    }







}