package com.criptext.monkeykitui.recycler.holders

import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 7/15/16.
 */

open class MonkeyFileHolder: MonkeyHolder {
    /* FILE */
    var fileLogoImageView : ImageView? = null
    var filenameTextView : TextView? = null

    constructor(view : View) : super(view) {
        fileLogoImageView = view.findViewById(R.id.imageViewLogoFile) as ImageView
        filenameTextView = view.findViewById(R.id.textViewFilename) as TextView
        filesizeTextView = view.findViewById(R.id.textViewFileSize) as TextView
    }

    open fun showFileData(filename: String, fileSize: String){
        filenameTextView!!.text = filename
        filesizeTextView!!.text = fileSize
    }

    open fun showFileIcon(ext: String){
        val context = fileLogoImageView!!.context
        Log.d("MonkeyFileHolder", ext)
        if(ext.compareTo("xls")==0 || ext.compareTo("xlsx")==0)
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.excel_file));
        else if(ext.compareTo("doc")==0 || ext.compareTo("docx")==0)
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.word_file));
        else if(ext.compareTo("ppt")==0 || ext.compareTo("pptx")==0)
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ppt_file));
        else if(ext.compareTo("pdf")==0)
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pdf_file));
        else if(ext.compareTo("zip")==0)
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.zip_file));
        else
            fileLogoImageView!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_file));
    }
    open fun setWaitingForDownload(){

        /*
        photoLoadingView!!.visibility = View.VISIBLE
        retryDownloadLayout?.visibility = View.INVISIBLE
        retryUploadLayout?.visibility = View.INVISIBLE
        retryUploadLayout?.setOnClickListener(null)
        retryDownloadLayout?.setOnClickListener(null)
        */

    }
    open fun setWaitingForUpload(){

        /*
        sendingProgressBar?.visibility = View.VISIBLE
        retryUploadLayout!!.visibility = View.GONE
        sendingProgressBar?.visibility = View.VISIBLE
        */

    }
    open fun setRetryUploadButton(retryListener: View.OnClickListener){

        /*
        photoLoadingView!!.visibility = View.GONE
        retryUploadLayout!!.visibility = View.VISIBLE
        retryUploadLayout!!.setOnClickListener(retryListener)
        sendingProgressBar?.visibility = View.INVISIBLE
        */

    }

    open fun setRetryDownloadButton(retryListener: View.OnClickListener){
        /*
        photoLoadingView!!.visibility = View.GONE
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout!!.setOnClickListener(retryListener)
        */

    }

}