package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/28/16.
 */

interface GroupChat {
    fun getMemberName(sessionId : String) : String

    fun getMemberColor(sessionId: String) : Int

}
