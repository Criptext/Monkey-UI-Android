package com.criptext.monkeykitui.info

import android.content.Context
import com.criptext.monkeykitui.recycler.MonkeyInfo

/**
 * Created by hirobreak on 12/10/16.
 */
class InfoOptions(val ctx: Context) {
    var deleteMember: ((MonkeyInfo) -> Unit)? = null
    var addRolFunction : ((MonkeyInfo) -> Unit)? = null
}