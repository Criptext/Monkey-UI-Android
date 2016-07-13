package com.criptext.monkeykitui.recycler.holders

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by daniel on 4/12/16.
 */

open class MonkeyImageHolder : MonkeyHolder {

    var retryDownloadLayout: LinearLayout? = null
    var retryUploadLayout: LinearLayout? = null
    var photoImageView : ImageView? = null
    var photoLoadingView : ProgressBar? = null

    constructor(view : View) : super(view) {
        photoImageView = view.findViewById(R.id.image_view) as ImageView
        if(view.findViewById(R.id.layoutRetryDownload)!=null)
            retryDownloadLayout = view.findViewById(R.id.layoutRetryDownload) as LinearLayout
        if(view.findViewById(R.id.layoutRetryUpload) != null)
            retryUploadLayout = view.findViewById(R.id.layoutRetryUpload) as LinearLayout
        if(view.findViewById(R.id.progressBarImage) != null)
            photoLoadingView = view.findViewById(R.id.progressBarImage) as ProgressBar

        sendingProgressBar?.indeterminateDrawable?.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    open fun setDownloadedImage(file : File, context : Context){
        photoLoadingView?.visibility = View.GONE
        Picasso.with(context)
                .load(file)
                .resize(200, 200)
                .centerCrop()
                .into(photoImageView)
    }

    open fun setNotDownloadedImage(item : MonkeyItem, context: Context){

        photoImageView!!.setImageBitmap(null)
        photoLoadingView!!.visibility = View.VISIBLE
        retryDownloadLayout!!.visibility = View.GONE
        retryUploadLayout!!.visibility = View.GONE

    }

    open fun setRetryUploadButton(position : Int, item: MonkeyItem, chatActivity: ChatActivity){

        photoLoadingView!!.visibility = View.GONE
        retryUploadLayout!!.visibility = View.VISIBLE
        retryUploadLayout!!.setOnClickListener {
            chatActivity.onFileUploadRequested(position, item)
            sendingProgressBar?.visibility = View.VISIBLE
            retryUploadLayout!!.visibility = View.GONE
        }

    }

    open fun setRetryDownloadButton(position : Int, item: MonkeyItem, chatActivity: ChatActivity){

        photoLoadingView!!.visibility = View.GONE
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout!!.setOnClickListener {
            chatActivity.onFileDownloadRequested(position, item)
            photoLoadingView!!.visibility = View.VISIBLE
            retryDownloadLayout!!.visibility = View.GONE
        }

    }

    open fun setOnClickListener(listener : View.OnClickListener){
        photoImageView!!.setOnClickListener(listener)
    }

}
