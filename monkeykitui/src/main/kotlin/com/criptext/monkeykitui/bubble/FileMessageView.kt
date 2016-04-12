package com.criptext.monkeykitui.bubble

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 3/31/16.
 */

class FileMessageView : MonkeyView {
    override val inLayoutId : Int
            get() = R.layout.file_message_view_in
    override val outLayoutId : Int
            get() = R.layout.file_message_view_out

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, incoming: Boolean) : super(context, incoming) {
    }


    override fun setBubbleBackground(resId: Int) {
    }

    val FileLogoImageView : ImageView
        get() = findViewById(R.id.imageViewLogoFile) as ImageView

    val FileNameTextView : TextView
        get() = findViewById(R.id.textViewFilename) as TextView

    val FileSizeTextView : TextView
        get() = findViewById(R.id.textViewFileSize) as TextView
}
