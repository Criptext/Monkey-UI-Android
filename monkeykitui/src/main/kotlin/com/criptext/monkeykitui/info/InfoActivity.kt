package com.criptext.monkeykitui.info

import com.criptext.monkeykitui.MonkeyInfoFragment
import com.criptext.monkeykitui.recycler.MonkeyInfo
import java.util.*

/**
 * Created by hirobreak on 06/10/16.
 */
interface InfoActivity {
    /**
     * This callback is executed on the onAttach() and onDetach() callbacks of the Conversations
     * fragment, the purpose is to update the activity's reference to the Conversation Fragment.
     * The activity should only have a reference to the fragment while it is attached.
     */
    fun setInfoFragment(infoFragment: MonkeyInfoFragment?)

    /**
     * This method is called by the Conversations fragment when it is initializing to retrieve the
     * list of conversations. loading the conversations should be asynchronous, so when you are done
     * call the fragment's insertConversations() method.
     */
    fun requestUsers()

    fun getInfo(): ArrayList<MonkeyInfo>

    fun onUserClick(user: MonkeyInfo)

}