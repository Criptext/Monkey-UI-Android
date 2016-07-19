package com.criptext.monkeykitui.recycler.holders

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.criptext.monkeykitui.R
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by daniel on 4/12/16.
 */

open class MonkeyImageHolder : MonkeyHolder, MonkeyFile {

    var retryDownloadLayout: LinearLayout? = null
    var retryUploadLayout: LinearLayout? = null
    var photoImageView : ImageView? = null
    var photoLoadingView : ProgressBar? = null

    constructor(view : View) : super(view) {
        photoImageView = view.findViewById(R.id.image_view) as ImageView
        retryDownloadLayout = view.findViewById(R.id.layoutRetryDownload) as LinearLayout?
        retryUploadLayout = view.findViewById(R.id.layoutRetryUpload) as LinearLayout?
        photoLoadingView = view.findViewById(R.id.progressBarImage) as ProgressBar?

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

    override fun setWaitingForDownload(){

        photoImageView!!.setImageBitmap(null)
        photoLoadingView!!.visibility = View.VISIBLE
        retryDownloadLayout?.visibility = View.INVISIBLE
        retryUploadLayout?.visibility = View.INVISIBLE
        retryUploadLayout?.setOnClickListener(null)
        retryDownloadLayout?.setOnClickListener(null)

    }
    override fun setWaitingForUpload(){

        retryUploadLayout!!.visibility = View.GONE
        sendingProgressBar?.visibility = View.VISIBLE

    }
    override fun setErrorInUpload(retryListener: View.OnClickListener){

        //photoLoadingView?.visibility = View.GONE
        retryUploadLayout!!.visibility = View.VISIBLE
        retryUploadLayout!!.setOnClickListener(retryListener)
        sendingProgressBar?.visibility = View.INVISIBLE

    }

    override fun setErrorInDownload(retryListener: View.OnClickListener){

        photoLoadingView!!.visibility = View.GONE
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout!!.setOnClickListener(retryListener)

    }

    open fun setOnClickListener(listener : View.OnClickListener?){
        photoImageView!!.setOnClickListener(listener)
    }

}
