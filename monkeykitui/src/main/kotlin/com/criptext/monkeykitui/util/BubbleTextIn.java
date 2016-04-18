package com.criptext.monkeykitui.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.criptext.monkeykitui.R;

/**
 * Created by daniel on 4/5/16.
 */

public class BubbleTextIn extends LinearLayout {

    //Dither and smooth :)
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final RectF mBound = new RectF();
    private final RectF mBound2 = new RectF();
    private final float radius;

    public BubbleTextIn(Context context) {
        this(context, null);
    }

    public BubbleTextIn(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public BubbleTextIn(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundDrawable(null);
        mPaint.setColor(Color.parseColor("#d6d6d6"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint2.setColor(Color.parseColor("#f8f6f7"));
        mPaint2.setStyle(Paint.Style.FILL);
        radius = getResources().getDimension(R.dimen.bubble_corner_radius);
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mBound.set(l, t, r, b);
        mBound2.set(l, t, r, b-(getResources().getDimension(R.dimen.bubble_button)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(mBound, radius, radius, mPaint);
        canvas.drawRoundRect(mBound2, radius, radius, mPaint2);

        canvas.drawRect(mBound.left, mBound.top + radius, mBound.right - radius, mBound.bottom, mPaint);
        canvas.drawRect(mBound2.left, mBound2.top + radius, mBound2.right - radius, mBound2.bottom, mPaint2);
    }
}
