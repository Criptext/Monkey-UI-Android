package com.criptext.monkeykitui.util

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
    }
}
