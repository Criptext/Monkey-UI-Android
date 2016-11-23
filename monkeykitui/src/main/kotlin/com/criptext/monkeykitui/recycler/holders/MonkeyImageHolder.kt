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
    var button: ImageView? = null

    constructor(view : View) : super(view) {
        photoImageView = view.findViewById(R.id.image_view) as ImageView
        button = view.findViewById(R.id.button) as ImageView?
        retryDownloadLayout = view.findViewById(R.id.layoutRetryDownload) as LinearLayout?
        retryUploadLayout = view.findViewById(R.id.layoutRetryUpload) as LinearLayout?
        photoLoadingView = view.findViewById(R.id.progressBarImage) as ProgressBar?

        sendingProgressBar?.indeterminateDrawable?.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    open fun setEmptyImage() {
        photoImageView?.setImageDrawable(null)
    }

    open fun setDownloadedImage(file : File, context : Context){
        photoLoadingView?.visibility = View.GONE
        photoImageView?.setImageDrawable(null)
        Picasso.with(context)
                .load(file)
                .resize(200, 200)
                .centerCrop()
                .into(photoImageView)
        retryDownloadLayout?.isClickable = false
        retryUploadLayout?.isClickable = false
        photoImageView?.isClickable = true
    }

    override fun setWaitingForDownload(){

        photoImageView!!.setImageDrawable(null)
        button?.setImageResource(R.drawable.mk_file_download)
        photoImageView?.isClickable = false
        sendingProgressBar?.visibility = View.INVISIBLE
        photoLoadingView!!.visibility = View.VISIBLE
        retryDownloadLayout?.visibility = View.INVISIBLE
        retryUploadLayout?.visibility = View.INVISIBLE
        retryDownloadLayout?.isClickable = false
        retryUploadLayout?.isClickable = false
        retryUploadLayout?.setOnClickListener(null)
        retryDownloadLayout?.setOnClickListener(null)

    }
    override fun setWaitingForUpload(){

        retryUploadLayout?.visibility = View.GONE
        sendingProgressBar?.visibility = View.VISIBLE
        button?.setImageResource(R.drawable.ic_action_upload)
        retryDownloadLayout?.isClickable = false
        retryUploadLayout?.isClickable = false

    }
    override fun setErrorInUpload(listener: View.OnClickListener){

        //photoLoadingView?.visibility = View.GONE
        retryUploadLayout?.visibility = View.VISIBLE
        retryUploadLayout?.isClickable = true
        retryUploadLayout?.setOnClickListener(listener)
        sendingProgressBar?.visibility = View.INVISIBLE

    }

    override fun setErrorInDownload(listener: View.OnClickListener){

        photoLoadingView!!.visibility = View.GONE
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout?.isClickable = true
        retryDownloadLayout!!.setOnClickListener(listener)

    }

    open fun setOnClickListener(listener : View.OnClickListener?){
        photoImageView!!.setOnClickListener(listener)
        retryDownloadLayout?.setOnClickListener(null)
        retryUploadLayout?.setOnClickListener(null)
    }

}
