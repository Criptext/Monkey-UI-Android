package com.criptext.monkeykitui.recycler.holders

import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 7/15/16.
 */

open class MonkeyFileHolder: MonkeyHolder, MonkeyFile {
    /* FILE */
    var fileLogoImageView : ImageView? = null
    var filenameTextView : TextView? = null
    var downloadProgressBar : ProgressBar? = null

    constructor(view : View) : super(view) {
        fileLogoImageView = view.findViewById(R.id.imageViewLogoFile) as ImageView
        filenameTextView = view.findViewById(R.id.textViewFilename) as TextView
        filesizeTextView = view.findViewById(R.id.textViewFileSize) as TextView
        downloadProgressBar = view.findViewById(R.id.downloadProgress) as ProgressBar?
    }

    open fun showFileData(filename: String, fileSize: String){
        filenameTextView!!.text = filename
        filesizeTextView!!.text = fileSize
    }

    open fun showFileIcon(ext: String){
        val context = fileLogoImageView!!.context
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
    override fun setWaitingForDownload(){

        downloadProgressBar?.visibility = View.VISIBLE
        fileLogoImageView!!.visibility = View.INVISIBLE
        fileLogoImageView!!.setOnClickListener(null)
    }
    override fun setWaitingForUpload(){

        fileLogoImageView!!.visibility = View.INVISIBLE
        fileLogoImageView!!.setOnClickListener(null)
        sendingProgressBar!!.visibility = View.VISIBLE

    }
    override fun setErrorInUpload(listener: View.OnClickListener, uploadSize: Long){

        fileLogoImageView!!.visibility = View.VISIBLE
        fileLogoImageView!!.setOnClickListener(listener)
        sendingProgressBar!!.visibility = View.GONE

    }

    override fun setErrorInDownload(listener: View.OnClickListener, downloadSize: Long){

        downloadProgressBar?.visibility = View.INVISIBLE
        fileLogoImageView!!.visibility = View.VISIBLE
        fileLogoImageView!!.setOnClickListener(listener)
        /*
        photoLoadingView!!.visibility = View.GONE
        retryDownloadLayout!!.visibility = View.VISIBLE
        retryDownloadLayout!!.setOnClickListener(retryListener)
        */

    }

}