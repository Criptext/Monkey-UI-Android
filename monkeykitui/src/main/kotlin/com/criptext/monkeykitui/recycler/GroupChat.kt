package com.criptext.monkeykitui.recycler

/**
 * Object that MonkeyAdapter uses to display relevant information about Group Conversations.
 * Created by gesuwall on 4/28/16.
 */

interface GroupChat {
    /**
     * Maps Monkey ID's to user names to display in messages of Group chats.
     *@param monkeyId Monkey ID of a member of the currently displayed group conversation
     *@param the user name associated to the monkey ID
     */
    fun getMemberName(monkeyId : String) : String

    /**
     * Maps Monkey ID's to colors to display with the sender's name in messages of Group chats. Each
     * ID should have an unique color.
     *@param monkeyId Monkey ID of a member of the currently displayed group conversation
     *@param the color to use to display the sender's name.
     */
    fun getMemberColor(monkeyId: String) : Int

}
