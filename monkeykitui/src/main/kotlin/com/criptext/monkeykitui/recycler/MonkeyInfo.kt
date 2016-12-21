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
    fun getInfoId() : String

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

    /**
     * @return the color of the user
     */
    fun getColor() : Int

    /**
     * Set the right title for user info
     */
    fun setRightTitle(rightTitle : String)

    /**
     * Set the subtitle for user info
     */
    fun setSubtitle(subtitle : String)

    /**
     * Set the color for user info
     */
    fun setColor(color : Int)
}