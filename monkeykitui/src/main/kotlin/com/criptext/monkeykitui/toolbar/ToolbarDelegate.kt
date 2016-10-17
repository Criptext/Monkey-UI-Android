package com.criptext.monkeykitui.toolbar

/**
 * Created by daniel on 9/21/16.
 */

interface ToolbarDelegate{

    fun onClickToolbar(monkeyID: String, name: String, lastSeen: String, avatarURL: String)

}