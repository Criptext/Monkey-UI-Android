package com.criptext.monkeykitui.toolbar

import android.content.Context
import android.media.Image
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import com.criptext.monkeykitui.R

/**
 * Created by hirobreak on 10/10/16.
 */
class HeaderViewBehavior(context: Context, attrs: AttributeSet? = null) : CoordinatorLayout.Behavior<HeaderView>(context, attrs){

    private val MIN_AVATAR_PERCENTAGE_SIZE = 0.3f
    private val EXTRA_FINAL_AVATAR_PADDING = 80

    private val TAG = "behavior"
    private val mContext: Context = context

    private val mCustomFinalHeight: Float = 0.toFloat()

    private var mStartToolbarPosition: Float = 0.toFloat()
    private var mStartYPosition: Int = 0
    private var mFinalYPosition: Int = 0
    private var mStartHeight: Int = 0
    private var mfontSize: Float = 20.toFloat()
    private var mChangeBehaviorPoint: Float = 0.toFloat()

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: HeaderView?, dependency: View?): Boolean {
        var hello = (dependency is AppBarLayout)
        return hello
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: HeaderView, dependency: View): Boolean {
        maybeInitProperties(child, dependency)

        val maxScrollDistance = - mContext.resources.getDimension(R.dimen.mk_header_scroll)
        val expandedPercentageFactor = dependency.y / maxScrollDistance
        Log.d("TEST", dependency.y.toString())
        Log.d("TEST", maxScrollDistance.toString())


        if (expandedPercentageFactor < mChangeBehaviorPoint) {
            val heightFactor = (mChangeBehaviorPoint - expandedPercentageFactor) / mChangeBehaviorPoint

            val distanceYToSubtract = (mStartYPosition - mFinalYPosition) * (1f - expandedPercentageFactor) + child.getHeight() / 2

            child.setY(mStartYPosition - distanceYToSubtract)

            val heightToSubtract = (mStartHeight - mCustomFinalHeight) * heightFactor

            val lp = child.layoutParams as CoordinatorLayout.LayoutParams

            child.layoutParams = lp
        } else {
            val distanceYToSubtract = (mStartYPosition - mFinalYPosition) * (1f - expandedPercentageFactor)

            child.setY(mStartYPosition - distanceYToSubtract)

            if(mStartYPosition - distanceYToSubtract < mStartYPosition){
                child.setY(mStartYPosition.toFloat())
            }else if(mStartYPosition - distanceYToSubtract > mFinalYPosition){
                child.setY(mFinalYPosition.toFloat())
            }

            child.title.textSize = mfontSize - (mfontSize - 25) * (1f - expandedPercentageFactor)
            child.subtitle.textSize = 15 - (15 - 20) * (1f - expandedPercentageFactor)

            child.imageView.layoutParams.height = (126 - (126 - 226) * (1f - expandedPercentageFactor)).toInt()
            child.imageView.layoutParams.width = (126 - (126 - 226) * (1f - expandedPercentageFactor)).toInt()

        }
        return true
    }

    private fun maybeInitProperties(child: HeaderView, dependency: View) {
        if (mStartYPosition === 0)
            mStartYPosition = 0

        if (mFinalYPosition === 0)
            mFinalYPosition = mContext.resources.getDimension(R.dimen.mk_header_scroll).toInt()

        if (mStartHeight === 0)
            mStartHeight = child.getHeight()

        if (mStartToolbarPosition === 0.toFloat())
            mStartToolbarPosition = dependency.y

        if (mChangeBehaviorPoint === 0.toFloat())
            mChangeBehaviorPoint = (child.height - mCustomFinalHeight) / (2f * (mStartYPosition - mFinalYPosition))
    }


}