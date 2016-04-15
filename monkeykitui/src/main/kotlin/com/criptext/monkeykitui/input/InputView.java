package com.criptext.monkeykitui.input;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.criptext.monkeykitui.R;

/**
 * Created by daniel on 4/15/16.
 */

public class InputView extends LinearLayout {

    private ImageView button_mic;
    private ImageView button_send;
    private ImageView button_attachments;
    private LinearLayout layoutRecording;
    private LinearLayout layoutSwipeCancel;
    private TextView textViewTimeRecorging;
    private EditText editText;
    private RecordingListeners recordingListeners;

    private float startX = 0, startY = 0;
    private boolean isLeft = false, isUp = false, mClockRunning = false;
    private long timeStamp, startTime;
    private boolean blocked = false;
    private int counter;
    final float maxLength = getResources().getDimension(R.dimen.max_Height);
    int viewLeftMargin;
    int viewTopMargin;
    int viewBottomMargin;
    int viewRightMargin;
    int viewLeftMarginBackup;
    int viewTopMarginBackup;
    int viewBottomMarginBackup;
    int viewRightMarginBackup;

    public InputView(Context context) {
        super(context);
        init();
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0);
        init(a);
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputView, 0, 0);
        init(a);
    }

    private void init() {
        inflate(getContext(), R.layout.input_view, this);
    }

    private void init(TypedArray a) {
        inflate(getContext(), R.layout.input_view, this);
        try {

            button_mic = (ImageView)findViewById(R.id.button_mic);
            button_send = (ImageView)findViewById(R.id.button_attchments);
            button_attachments = (ImageView)findViewById(R.id.button_attchments);
            layoutRecording = (LinearLayout)findViewById(R.id.layoutRecording);
            layoutSwipeCancel = (LinearLayout)findViewById(R.id.layoutSwipeCancel);
            textViewTimeRecorging = (TextView)findViewById(R.id.textViewTimeRecording);
            editText = (EditText)findViewById(R.id.msg_edit);

            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutSwipeCancel.getLayoutParams();
            viewLeftMarginBackup= lParams.leftMargin;
            viewTopMarginBackup=lParams.topMargin;
            viewBottomMarginBackup=lParams.bottomMargin;
            viewRightMarginBackup=lParams.rightMargin;

            if(a.getDrawable(R.styleable.InputView_attachmentButton)!=null)
                button_send.setImageDrawable(a.getDrawable(R.styleable.InputView_attachmentButton));
            if(a.getDrawable(R.styleable.InputView_micButton)!=null)
                button_mic.setImageDrawable(a.getDrawable(R.styleable.InputView_micButton));
            else{
                setLongClickListener();
            }
        } finally {
            a.recycle();
        }
    }

    private void setLongClickListener(){

        if(recordingListeners!=null)
            return;

        timeStamp = System.nanoTime() - 1800000000L;
        button_mic.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int X = (int) event.getRawX();

                if(event.getPointerCount() == 1){

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if(System.nanoTime() - timeStamp < 1800000000L)
                            blocked = true;
                        else
                            blocked = false;

                        if(blocked){
                            return true;
                        }

                        startTime = System.nanoTime();
                        startX = event.getRawX();
                        startY = event.getRawY();

                        recordingListeners.onStartRecording();
                        startRecording();
                        vibrate();

                        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutSwipeCancel.getLayoutParams();
                        viewLeftMargin = X - lParams.leftMargin;
                        viewTopMargin = lParams.topMargin;
                        viewBottomMargin = lParams.bottomMargin;
                        viewRightMargin = X - lParams.rightMargin;
                        layoutSwipeCancel.setLayoutParams(lParams);


                    } else if(event.getAction() == MotionEvent.ACTION_UP ){

                        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutSwipeCancel.getLayoutParams();
                        lParams.leftMargin=viewLeftMarginBackup;
                        lParams.topMargin=viewTopMarginBackup;
                        lParams.bottomMargin=viewBottomMarginBackup;
                        lParams.rightMargin=viewRightMarginBackup;
                        layoutSwipeCancel.setLayoutParams(lParams);

                        if(blocked){
                            return true;
                        }

                        float dy = event.getRawY() - startY;
                        float dx = event.getRawX() - startX;

                        timeStamp = System.nanoTime();

                        if(dx < -maxLength*1.4){
                            recordingListeners.onCancelRecording();
                            finishOrCancelRecording();
                        }
                        else if(dy < -maxLength*1.4){
                            recordingListeners.onCancelRecording();
                            finishOrCancelRecording();
                        }
                        else if(dy > maxLength){
                            recordingListeners.onCancelRecording();
                            finishOrCancelRecording();
                        }
                        else {
                            recordingListeners.onStopRecording();
                            finishOrCancelRecording();
                        }

                    }else if (event.getAction() == MotionEvent.ACTION_MOVE){

                        if(blocked){
                            return true;
                        }

                        float dy = event.getRawY() - startY;
                        float dx = event.getRawX() - startX;

                        if(dx < -maxLength*1.4){
                            isUp = false;
                            if(!isLeft){
                                vibrate();
                                isLeft = true;
                            }
                        } else{
                            isLeft = false;
                            if(dy < -maxLength*1.4){
                                if(!isUp){
                                    vibrate();
                                    isUp = true;
                                }
                            } else
                                isUp = false;
                        }

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutSwipeCancel.getLayoutParams();
                        layoutParams.rightMargin = X - viewRightMargin;
                        layoutParams.leftMargin = X - viewLeftMargin;
                        layoutParams.topMargin = viewTopMargin;
                        layoutParams.bottomMargin = viewBottomMargin;
                        layoutSwipeCancel.setLayoutParams(layoutParams);

                        System.out.println(dx+" vs "+(InputView.this.getWidth()/4));
                        if(Math.abs(dx) > InputView.this.getWidth()/4){
                            recordingListeners.onCancelRecording();
                            finishOrCancelRecording();
                            blocked=true;
                        }

                    }
                }
                return true;
            }
        });
    }

    public void setOnRecordListener(RecordingListeners recordListener){
        this.recordingListeners = recordListener;
    }

    private void vibrate(){
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    private void startRecording(){
        layoutRecording.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
        button_attachments.setVisibility(View.GONE);
        mClockRunning=true;
        myThreadTimerRecorder();
    }

    private void finishOrCancelRecording(){
        layoutRecording.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        button_attachments.setVisibility(View.VISIBLE);
        mClockRunning=false;
    }

    public void myThreadTimerRecorder(){
        textViewTimeRecorging.setText("00:00");
        counter=0;
        new Thread(){
            @Override
            public void run(){
                try{
                    while(mClockRunning){
                        Thread.sleep(1000);
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                counter++;
                                int minutos=0;
                                int segundos=0;
                                minutos=counter/60;
                                if(minutos>0)
                                    segundos=counter%60;
                                else
                                    segundos=counter;
                                if(minutos>9&&segundos>9)
                                    textViewTimeRecorging.setText(minutos+":"+segundos);
                                else if(minutos>9&&segundos<=9)
                                    textViewTimeRecorging.setText(minutos+":0"+segundos);
                                else if(minutos<=9&&segundos>9)
                                    textViewTimeRecorging.setText("0"+minutos+":"+segundos);
                                else
                                    textViewTimeRecorging.setText("0"+minutos+":0"+segundos);
                            }
                        });
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
