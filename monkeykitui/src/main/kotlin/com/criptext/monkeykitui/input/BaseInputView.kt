package com.criptext.monkeykitui.input

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton

/**
 * Superclass for all InputViews. An InputView is a FrameLayout with an EditText and buttons to the
 * left and right of it.
 *
 * An InputView should always match its width to its parent, but its height is arbitrary. The only
 * limitation is that the height should be at least 50dp because that is the default height that the
 * InputView uses for its EditText and background. A taller InputView may be used when the left or
 * right button has hidden views that need more vertical space, however, the background will not grow
 * accordingly.
 *
 * This class only configures the EditText and the background. child classes should implement their
 * own left and right buttons overriding the setLeftButton and setRightButton methods.
 *
 * Created by Gabriel Aumala on 4/21/16.
 */

open class BaseInputView : FrameLayout {

    protected var barBackground: View? = null
    protected lateinit var editText : EditText
    protected lateinit var leftButtonView : View

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0)
        init(a)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0)
        init(a)
    }

    internal fun View.safelySetBackground(customBackground: Drawable){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(customBackground);
        } else {
            this.background = customBackground
        }
    }
    /**
     * Sets a custom background for the EditText
     * @param customBackground drawable to use as background for EditText
     */
    fun setEditTextBackground(customBackground: Drawable){
        editText.safelySetBackground(customBackground)
    }

    /**
     * Initializes the InputView. Instantiates an EditText and sets the left and right buttons if there
     * are any to their respective positions. You may override this method to customize the EditText.
     */
    open protected fun init(typedArray: TypedArray){
        setBarBackground(typedArray)
        editText = EditText(context);
        editText.maxLines = 4
        editText.hint = context.resources.getString(R.string.text_message_write_hint)
        editText.setTextColor(Color.BLACK)
        editText.setEms(10)

        val customBackground = typedArray.getDrawable(R.styleable.InputView_editTextDrawable)
        if(customBackground != null)
            setEditTextBackground(customBackground)

        editText.inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.BOTTOM
        params.bottomMargin = dpToPx(5, context)

        editText.layoutParams = params
        addView(editText)

        val leftBtn = setLeftButton(typedArray)
        if(leftBtn != null) {
            leftButtonView = leftBtn.button
            params.leftMargin = leftBtn.visibleWidth
            (leftBtn.button.layoutParams as LayoutParams).gravity = Gravity.LEFT or Gravity.BOTTOM
            addView(leftBtn.button)
        }

        val rightBtn = setRightButton(typedArray)
        if(rightBtn != null) {
            params.rightMargin = rightBtn.visibleWidth
            (rightBtn.button.layoutParams as LayoutParams).gravity = Gravity.RIGHT or Gravity.BOTTOM
            addView(rightBtn.button)
        }

    }

/**
     * Sets a rectangular background to the InputView. The rectangle isn't necessarily the same height
     * as the actual InputView because hidden views may take more space. You may override this method
     * to customize the background.
     * @param customBackground drawable to set as background
     */
    fun setBarBackground(customBackground: Drawable?){
        if(barBackground != null)
            removeView(barBackground)

        val view = View(context)

        if(customBackground != null) {
             view.safelySetBackground(customBackground)
         } else
            view.setBackgroundColor(Color.WHITE)


        val  params = LayoutParams(LayoutParams.MATCH_PARENT, context.resources.getDimension(R.dimen.default_inputview_height).toInt())
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params
        addView(view)

        barBackground = view
    }

    /**
     * Sets a rectangular background to the InputView. The rectangle isn't necessarily the same height
     * as the actual InputView because hidden views may take more space. You may override this method
     * to use a different XML attribute for the background
     * @param typedArray TypedArray with the customizations set in the XML layout file. If
     * backgroundDrawableInputView has been set in the TypedArray, it will use that Drawable as
     * background.
     */
    protected fun setBarBackground(typedArray: TypedArray){
        val customBackground = typedArray.getDrawable(R.styleable.InputView_backgroundDrawable)
        setBarBackground(customBackground)
    }

    /**
     * Creates a View that will be placed to the left of the EditText
     * @param a AttributeHandler with the customizations set in the XML layout file.
     * @return A SideButton instance that contains the View to place to the left to the EditText and
     * the margin distance that should be between the screen's left edge and the EditText to make room
     * for the new View.
     */
    open protected fun setLeftButton(typedArray: TypedArray) : SideButton? = null
    /**
     * Creates a View that will be placed to the right of the EditText
     * @param a AttributeHandler with the customizations set in the XML layout file.
     * @return A SideButton instance that contains the View to place to the right to the EditText and
     * the margin distance that should be between the screen's right edge and the EditText to make room
     * for the new View.
     */
    open protected fun setRightButton(typedArray: TypedArray) : SideButton? = null

    companion object {
        fun dpToPx(dp: Int, context: Context): Int {
            val displayMetrics = context.resources.displayMetrics;
            val px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return px;

        }
    }

    fun clearText(){
        editText.text.clear()
    }


}
