package com.criptext.monkeykitui.bubble

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 4/1/16.
 */

class ContactMessageView : MonkeyView {
    override val inLayoutId : Int
            get() = R.layout.contact_message_view_in
    override val outLayoutId : Int
            get() = R.layout.contact_message_view_out

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, incoming: Boolean) : super(context, incoming) {
    }


    override fun setBubbleBackground(resId: Int) {
    }

    val contactAvatarImageView : ImageView
        get() = findViewById(R.id.photo_msg_friend) as ImageView

    val contactNameTextView : TextView
        get() = findViewById(R.id.fullname_friend) as TextView

    val createNewTextView : TextView
        get() {
            if(!isIncomingMessage)
                throw IllegalStateException(IN_ERROR_MSG)
            return findViewById(R.id.textViewCreateNew) as TextView
        }

    val addExistingTextView : TextView
        get() {
            if(!isIncomingMessage)
                throw IllegalStateException(IN_ERROR_MSG)
            return findViewById(R.id.textViewAddExisting) as TextView
        }
}