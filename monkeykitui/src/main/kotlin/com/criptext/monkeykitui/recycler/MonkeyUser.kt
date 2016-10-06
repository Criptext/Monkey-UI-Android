package com.criptext.monkeykitui.recycler

import java.util.*

/**
 * Created by hirobreak on 04/10/16.
 */
interface MonkeyUser {
    /*COMMON*/

    /**
     * @return the user's avatar url
     *
     */
    fun getMonkeyId() : String

    /**
     * @return the user's avatar url
     *
     */
    fun getAvatarUrl() : String

    /**
     * @return the user's name
     *
     */
    fun getName() : String

    /**
     * @return whether the user is online or not
     */
    fun getConnectionStatus() : String

    /**
     * @return the rol of the user
     */
    fun getRol() : String

}