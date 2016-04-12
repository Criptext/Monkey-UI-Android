package com.criptext.monkeykitui.bubble

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.criptext.monkeykitui.*

/**
 * Created by gesuwall on 3/30/16.
 */

class ImageMessageView : MonkeyView {
    override val inLayoutId = R.layout.image_message_view_in
    override val outLayoutId = R.layout.image_message_view_out

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, incoming: Boolean) : super(context, incoming) {
    }


    override fun setBubbleBackground(resId: Int) {
    }

    override val sendingProgressBar: ProgressBar
    get() {
        val progBar = findViewById(R.id.sendingWheel) as ProgressBar
        progBar.indeterminateDrawable.setColorFilter(Color.parseColor("#014766"), android.graphics.PorterDuff.Mode.MULTIPLY);
        return progBar

    }
    val photoSizeTextView : TextView
        get() = findViewById(R.id.textViewTamano) as TextView

    val photoCoverImageView : ImageView
        get() = findViewById(R.id.image_loading) as ImageView

    val photoLoadingView : ProgressBar
        get() = findViewById(R.id.progressBarImage) as ProgressBar

    val retryDownloadLayout : LinearLayout
        get() {
            if(!isIncomingMessage)
                throw IllegalStateException(IN_ERROR_MSG)
            return findViewById(R.id.layoutRetryDownload) as LinearLayout
        }

     val photoImageView : ImageView
        get() {
            return findViewById(R.id.image_view) as ImageView
        }

    override fun initView(){
        val child: View
        if (isIncomingMessage) {
            child = LayoutInflater.from(context).inflate(R.layout.image_message_view_in, null)
        } else {
            child = LayoutInflater.from(context).inflate(R.layout.image_message_view_out, null)
        }
        this.addView(child)
    }



}
