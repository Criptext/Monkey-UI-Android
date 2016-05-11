package com.criptext.monkeykitui.recycler.holders

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.util.Utils
import com.innovative.circularaudioview.CircularAudioView

/**
 * Created by gesuwall on 4/11/16.
 */
open class MonkeyHolder : RecyclerView.ViewHolder {
    /* COMMON */
    var datetimeTextView : TextView? = null
    var bubbleLayout : ViewGroup? = null
    var tailImageView : ImageView? = null
    var selectedImageView : ImageView? = null
    /* COMMON OUTGOING */
    var checkmarkImageView : ImageView? = null
    var errorImageView : ImageView? = null
    var sendingProgressBar : ProgressBar? = null
    /* COMMON INCOMING */
    var senderNameTextView : TextView? = null
    var privateDatetimeTextView : TextView? = null
    var privacyCoverView : LinearLayout? = null
    var privacyTextView : TextView? = null

    /* CONTACT */
    var contactAvatarImageView : ImageView? = null
    var contactNameTextView : TextView? = null
    var createNewTextView : TextView? = null
    var addExisitingTextView : TextView? = null
    /* COMMON IMAGE & FILE & AUDIO */
    var downloadingView : ProgressBar? = null
    var filesizeTextView : TextView? = null
    /* FILE */
    var fileLogoImageView : ImageView? = null
    var filenameTextView : TextView? = null

    constructor(view : View) : super(getViewWithRecyclerLayoutParams(view)) {
        datetimeTextView = view.findViewById(R.id.datetime) as TextView?
        bubbleLayout = view.findViewById(R.id.content_message) as ViewGroup?
        tailImageView = view.findViewById(R.id.tail) as ImageView?
        selectedImageView = view.findViewById(R.id.imageViewChecked) as ImageView?

        senderNameTextView = view.findViewById(R.id.sender_name) as TextView?

        checkmarkImageView = view.findViewById(R.id.imageViewCheckmark) as ImageView?
        errorImageView = view.findViewById(R.id.net_error) as ImageView?
        sendingProgressBar = view.findViewById(R.id.sendingWheel) as ProgressBar?
        sendingProgressBar?.indeterminateDrawable?.setColorFilter(Color.parseColor("#014766"), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

/**
 * Revisa si el mensaje ya fue leído, y coloca los vistos respectivos para 'entregado' y leído.
 *
 * @param positionMessage El mensaje que se va a revisar
 * @param datetext El TextView donde se coloca la fecha y los vistos
 */
open fun updateReadStatus(status : MonkeyItem.OutgoingMessageStatus){
    if(status == MonkeyItem.OutgoingMessageStatus.read){
        checkmarkImageView!!.setImageDrawable(ContextCompat.getDrawable(
                checkmarkImageView!!.context, R.drawable.mk_checkmark_read));
        checkmarkImageView!!.visibility = View.VISIBLE
    } else if(status == MonkeyItem.OutgoingMessageStatus.delivered){
        checkmarkImageView!!.visibility = View.VISIBLE
        checkmarkImageView!!.setImageDrawable(ContextCompat.getDrawable(
                checkmarkImageView!!.context, R.drawable.mk_checkmark_sent));
    } else {
        checkmarkImageView!!.visibility = View.GONE
    }
}

    /**
 * Revisa si ya se envio el mensaje. Si no, le pone un alpha de 0.5. Si no hay internet
 * coloca el mensaje de error. Si ya lo envio, el alpha regresa a ser normal.
 * @param positionMessage Mensaje a enviar
 * @param view view que contiene el mensaje
 * @param holder holder con todos los views del mensaje
 */
open fun updateSendingStatus(status : MonkeyItem.OutgoingMessageStatus, isOnline : Boolean, messageTimestamp: Long){
    if(status == MonkeyItem.OutgoingMessageStatus.sending){
        checkmarkImageView!!.visibility = View.GONE
        if(isOnline){
            errorImageView!!.visibility = View.GONE
            sendingProgressBar!!.visibility = View.VISIBLE
        }else{
            errorImageView!!.visibility = View.VISIBLE
            sendingProgressBar!!.visibility = View.GONE
        }
        //COMPARO TIMESTAMPS
        if((System.currentTimeMillis()/1000)- messageTimestamp >= 15){
            errorImageView!!.visibility = View.VISIBLE
            sendingProgressBar!!.visibility = View.GONE
        }
    }else{
        sendingProgressBar!!.visibility = View.GONE
        checkmarkImageView!!.visibility = View.VISIBLE
        errorImageView!!.visibility = View.GONE
    }
}

        open fun setSenderName(name : String, color : Int){
            contactNameTextView!!.text = name
            contactNameTextView!!.setTextColor(color)
        }

        open fun setMessageDate(timestamp : Long){
            datetimeTextView!!.text = Utils.getHoraVerdadera(timestamp)
        }

        open fun setOnLongClickListener(listener: View.OnLongClickListener){
            bubbleLayout!!.setOnLongClickListener { v ->
                listener.onLongClick(v)
            }
        }

        /**
         * Revisa si el mensaje esta seleccionado
         *
         * @param positionMessage El mensaje que se va a revisar
         */
        open fun updateSelectedStatus(isSelected: Boolean){
            if(isSelected) {
                selectedImageView?.visibility = View.VISIBLE
                bubbleLayout?.alpha = 0.5f
                tailImageView?.alpha = 0.5f
            } else{
                selectedImageView?.visibility = View.INVISIBLE
                bubbleLayout?.alpha = 1f
                tailImageView?.alpha = 1f
            }
        }

    companion object {
        fun getViewWithRecyclerLayoutParams(view: View) : View{
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = RecyclerView.LayoutParams(params)
            return view
        }

    }

    }