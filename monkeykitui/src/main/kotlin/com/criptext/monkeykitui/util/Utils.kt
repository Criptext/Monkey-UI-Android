package com.criptext.monkeykitui.util

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat

/**
 * Created by gesuwall on 4/6/16.
 */

class Utils {
    companion object {
        val TAG = "MONKEY-UI-KIT"
        fun getHoraVerdadera(timestamp: Long) : String{
            val fechaPaelUser = SimpleDateFormat("h:mm a").format(timestamp).toUpperCase()
            return fechaPaelUser.replace("P.M.".toRegex(), "PM").replace("A.M.".toRegex(), "AM")
        }

        /**
         * Adds a RecyclerView.LayoutParams to a view
         * @param view view to set the new layout params
         * @return the view with a RecyclerView.LayoutParams object as its layout params
         *
         */
        fun getViewWithRecyclerLayoutParams(view: View) : View {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = RecyclerView.LayoutParams(params)
            return view
        }
    }
}
