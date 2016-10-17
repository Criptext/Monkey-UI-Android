package com.criptext.monkeykitui.recycler

import java.util.*

/**
 * Created by hirobreak on 04/10/16.
 */
interface MonkeyInfo {
    /*COMMON*/

    /**
     * @return the user's avatar url
     *
     */
    fun getAvatarUrl() : String

    /**
     * @return the user's name
     *
     */
    fun getTitle() : String

    /**
     * @return whether the user is online or not
     */
    fun getSubtitle() : String

    /**
     * @return the rol of the user
     */
    fun getRightTitle() : String

}