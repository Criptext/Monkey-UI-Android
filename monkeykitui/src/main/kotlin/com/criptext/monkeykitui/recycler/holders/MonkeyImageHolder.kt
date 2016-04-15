package com.criptext.monkeykitui.recycler.holders

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.ImageMessageView
import com.criptext.monkeykitui.bubble.MonkeyView
import com.criptext.monkeykitui.photoview.PhotoViewActivity
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by daniel on 4/12/16.
 */

class MonkeyImageHolder : MonkeyHolder {

    var photoSizeTextView : TextView? = null

    var placeholderImageView: ImageView? = null

    var photoLoadingView : ProgressBar? = null

    var retryDownloadLayout : LinearLayout? = null

    var photoImageView : ImageView? = null

    constructor(view : View) : super(view) {

        photoSizeTextView = view.findViewById(R.id.textViewTamano) as TextView
        placeholderImageView = view.findViewById(R.id.image_placeholder) as ImageView
        photoLoadingView = view.findViewById(R.id.progressBarImage) as ProgressBar
        retryDownloadLayout = view.findViewById(R.id.layoutRetryDownload) as LinearLayout
        photoImageView = view.findViewById(R.id.image_view) as ImageView
    }

    constructor(view : MonkeyView, type : Int) : super(view, type) {
        val tmv = view as ImageMessageView
        photoSizeTextView = tmv.photoSizeTextView
        placeholderImageView = tmv.photoCoverImageView
        photoLoadingView = tmv.photoLoadingView
        retryDownloadLayout = tmv.retryDownloadLayout
        photoImageView = tmv.photoImageView
    }

    fun setDownloadedImage(file : File, context : Context){
        Picasso.with(context).load(file).into(photoImageView)
        photoLoadingView!!.visibility = View.GONE
    }

    fun setNotDownloadedImage(item : MonkeyItem, context: Context){
        val filePlaceholder = File(item.getPlaceholderFilePath())
        if(filePlaceholder.exists())
            Picasso.with(context).load(filePlaceholder).into(placeholderImageView)
        photoLoadingView!!.visibility = View.VISIBLE
        retryDownloadLayout!!.visibility = View.GONE
    }

    fun setRetryDownloadButton(position : Int, item: MonkeyItem, chatActivity: ChatActivity){
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                chatActivity.onFileDownloadRequested(position, item)
            }
        })
    }

    fun setClickListener(chatActivity: ChatActivity, item : MonkeyItem){
        photoImageView!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var intent = Intent(chatActivity as Context,PhotoViewActivity::class.java)
                intent!!.putExtra("data_path",item.getFilePath())
                chatActivity.startActivity(intent)
            }
        })
    }
}
