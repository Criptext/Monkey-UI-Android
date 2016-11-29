package com.criptext.monkeykitui.util

import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 9/15/16.
 */

class SnackbarUtils {
    companion object {
        fun showUndoMessage(recycler: RecyclerView, msg: String, undoAction: (View) -> Unit,
                            callback: Snackbar.Callback){
            val snack = Snackbar.make(recycler, msg, Snackbar.LENGTH_LONG)
                snack.setCallback(callback)
                snack.setAction(recycler.context.getString(R.string.mk_undo), undoAction)
                snack.show()
        }
    }
}