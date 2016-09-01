package com.criptext.monkeykitui.dialog

import android.R
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ArrayAdapter

/**
 * Created by gesuwall on 9/1/16.
 */

abstract class AbstractDialog<T>(val options: MutableList<T>){

    fun show(context: Context){
        val adapter = ArrayAdapter<T>(context, R.layout.select_dialog_item, options)
        val builder = AlertDialog.Builder(context)

        builder.setCancelable(true)
        val dialog = builder.setAdapter(adapter, { dialog, item ->
            executeCallback(options[item])
        }).show()
        if(android.os.Build.VERSION.SDK_INT < 20)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
    }

    abstract fun executeCallback(selectedOption: T)

}
