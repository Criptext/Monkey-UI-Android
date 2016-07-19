package com.criptext.monkeykitui.recycler.holders

import android.view.View

/**
 * Created by gesuwall on 7/18/16.
 */

interface MonkeyFile {
    /**
     * Adjust the UI to display a state of waiting for the file download to complete
     */
    fun setWaitingForDownload()
    /**
     * Adjust the UI to display a state of error during the file download. It should also display
     * a button to retry the download
     * @param listener A click listener to retry the download.
     */
    fun setErrorInDownload(listener: View.OnClickListener)
    /**
     * Adjust the UI to display a state of waiting for the file upload to complete
     */
    fun setWaitingForUpload()
    /**
     * Adjust the UI to display a state of error during the file upload. It should also display
     * a button to retry the upload
     * @param listener A click listener to retry the upload.
     */
    fun setErrorInUpload(listener: View.OnClickListener)
}