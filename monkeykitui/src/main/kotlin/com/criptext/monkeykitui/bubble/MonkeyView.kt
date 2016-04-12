package com.criptext.monkeykitui.bubble

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 3/28/16.
 */
abstract class MonkeyView : FrameLayout {
    var isIncomingMessage: Boolean = false
        private set

    abstract val inLayoutId : Int
    abstract val outLayoutId : Int

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initWithAttrs(attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initWithAttrs(attrs, 0)

    }

    constructor(context: Context, incoming: Boolean) : super(context) {
        isIncomingMessage = incoming
        initView()
    }

    private fun initWithAttrs(attrs: AttributeSet, defStyle: Int) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.MonkeyView, defStyle, 0)
        isIncomingMessage = attrArray.getBoolean(R.styleable.MonkeyView_incoming, false)
        initView()
        initAttributes(attrArray)
        attrArray.recycle()
    }

    open protected fun initView(){
        val child: View
        if (isIncomingMessage) {
            child = LayoutInflater.from(context).inflate(inLayoutId, null)
        } else {
            child = LayoutInflater.from(context).inflate(outLayoutId, null)
        }
        this.addView(child)
    }

    private fun initAttributes(attrArray: TypedArray) {

        //Datetime
        val datetimeText = attrArray.getString(R.styleable.MonkeyView_datetimeText)
        val datetimeColor = attrArray.getInt(R.styleable.MonkeyView_datetimeColor, -1)
        val datetimeSize = attrArray.getDimensionPixelSize(R.styleable.MonkeyView_datetimeSize, -1)

        if (datetimeText != null || datetimeColor != -1 || datetimeSize != -1) {
            val datetime = datetimeTextView
            if (datetimeText != null)
                datetime.text = datetimeText
            if (datetimeColor != -1)
                datetime.setBackgroundColor(datetimeColor)
            if (datetimeSize != -1)
                datetime.textSize = datetimeSize.toFloat()
        }

        //SelectedImageView
        val selectedImageViewSrc = attrArray.getInt(R.styleable.MonkeyView_datetimeColor, -1)


    }

    open val viewForListView: View
        get() {
            val view = this.getChildAt(0)
            val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.bottomMargin = fromDP(5)
            view.layoutParams = AbsListView.LayoutParams(params)
            return view

        }
    open val viewForRecyclerView: View
        get() {
            val view = this.getChildAt(0)
            val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //params.bottomMargin = fromDP(5)
            view.layoutParams = RecyclerView.LayoutParams(params)
            return view
        }


    val datetimeTextView: TextView
        get() = findViewById(R.id.datetime) as TextView

    open val tailImageView : ImageView
        get() = findViewById(R.id.tail) as ImageView

    val selectedImageView : ImageView
        get() = findViewById(R.id.imageViewChecked) as ImageView

    val bubbleLayout : ViewGroup
        get() = findViewById(R.id.content_message) as ViewGroup;

    val checkmarkImageView: ImageView
        get() {
            if(isIncomingMessage)
                throw IllegalStateException(OUT_ERROR_MSG)
            return findViewById(R.id.imageViewCheckmark) as ImageView
        }

    val errorImageView: ImageView
        get() {
            if(isIncomingMessage)
                throw IllegalStateException(OUT_ERROR_MSG)
            return findViewById(R.id.net_error) as ImageView
        }


    open val sendingProgressBar: ProgressBar
    get() {
        val progBar = findViewById(R.id.sendingWheel) as ProgressBar
        progBar.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        return progBar

    }

    val senderNameTextView : TextView
    get() {
        if(!isIncomingMessage)
            throw IllegalStateException(IN_ERROR_MSG)
        return findViewById(R.id.sender_name) as TextView
    }

    val privateDatetimeTextView : TextView
    get() {
        if(!isIncomingMessage)
            throw IllegalStateException(IN_ERROR_MSG)
        return findViewById(R.id.datetime_private) as TextView
    }

    val privacyCoverView : LinearLayout
    get() {
        if(!isIncomingMessage)
            throw IllegalStateException(IN_ERROR_MSG)
        return findViewById(R.id.tap_messages) as LinearLayout
    }

    val privacyTextView : TextView
    get() {
        if(!isIncomingMessage)
            throw IllegalStateException(IN_ERROR_MSG)
        return findViewById(R.id.text_private) as TextView
    }

    abstract fun setBubbleBackground(resId: Int)

    fun fromDP(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    companion object {
        val OUT_ERROR_MSG = "Requested view is only available in outgoing messages"
        val IN_ERROR_MSG = "Requested view is only available in incoming messages"
    }

}
