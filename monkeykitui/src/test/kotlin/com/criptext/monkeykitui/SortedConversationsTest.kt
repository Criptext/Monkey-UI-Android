package com.criptext.monkeykitui

import com.criptext.monkeykitui.conversation.MonkeyConversation
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Created by gesuwall on 9/6/16.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class SortedConversationsTest : ConversationsAdapterTestCase() {

    fun newConversation(timestamp: Long, id: String, status: Int): MonkeyConversation {
        return object : MonkeyConversation {
            override fun getAvatarFilePath() = ""
            override fun getDatetime() = timestamp
            override fun getGroupMembers() = ""
            override fun getId() = id
            override fun getSecondaryText() = ""
            override fun getName() = ""
            override fun getStatus() = status
            override fun getTotalNewMessages() = 0
            override fun isGroup() = false
        }
    }

    fun newConversation(timestamp: Long, id: String) = newConversation(timestamp, id,
            MonkeyConversation.ConversationStatus.receivedMessage.ordinal)

    fun assertThatListIsSorted(list: ArrayList<MonkeyConversation>){
        for(i in 1..(list.size-1))
            assert(MonkeyConversation.defaultComparator.compare(list[i - 1], list[i]) != 1)
            //System.out.println("${list[i].getMessageTimestampOrder()}")
    }

    fun assertThatConversationsAreNotRepeated(list: ArrayList<MonkeyConversation>){

        for(i in 0..(list.size-2))
            for(j in (i+1)..(list.size-1)) {
                assert(list[i].getId() != list[j].getId())
            }
    }

    @Test
    @Throws (Exception::class)
    fun newConversationsAddedAreSorted() {
        val time = System.currentTimeMillis()
        adapter.addNewConversation(newConversation(time, "123"))
        adapter.addNewConversation(newConversation(time + 1, "124"))
        adapter.addNewConversation(newConversation(time + 2, "125"))
        adapter.addNewConversation(newConversation(time + 4, "126"))

        adapter.addNewConversation(newConversation(time + 3, "126"))
        adapter.addNewConversation(newConversation(time + -1, "127"))
        adapter.addNewConversation(newConversation(time + 3, "128"))

        val list = adapter.takeAllConversations()
        assertThatListIsSorted(list as ArrayList<MonkeyConversation>)

    }

    @Test
    @Throws (Exception::class)
    fun oldConversationsAddedAreSorted() {
        val newPage = ArrayList<MonkeyConversation>()
        val time = System.currentTimeMillis()
        newPage.add(newConversation(time + 1, "124"))
        newPage.add(newConversation(time + 2, "125"))
        newPage.add(newConversation(time + 2, "126"))
        newPage.add(newConversation(time + 3, "127"))
        newPage.add(newConversation(time + 4, "128"))
        newPage.add(newConversation(time + 2, "129"))
        newPage.add(newConversation(time + 1, "130"))

        adapter.addOldConversations(newPage, true, recycler!!)
        val list = adapter.takeAllConversations()
        assertThatListIsSorted(list as ArrayList<MonkeyConversation>)
    }

    @Test
    @Throws (Exception::class)
    fun insertedConversationsAreSorted() {
        val newPage = ArrayList<MonkeyConversation>()
        val time = System.currentTimeMillis()
        newPage.add(newConversation(time + 1, "124"))
        newPage.add(newConversation(time + 2, "125"))
        newPage.add(newConversation(time + 2, "126"))
        newPage.add(newConversation(time + 3, "127"))
        newPage.add(newConversation(time + 3, "132"))
        newPage.add(newConversation(time + 4, "128"))
        newPage.add(newConversation(time + 2, "129"))
        newPage.add(newConversation(time + 1, "130"))
        newPage.add(newConversation(time - 1, "131"))

        adapter.insertConversations(newPage, true)
        val list = adapter.takeAllConversations()
        assertThatListIsSorted(list as ArrayList<MonkeyConversation>)
        assertThatConversationsAreNotRepeated(list)
    }


    fun createPage(time: Long): ArrayList<MonkeyConversation> {
        val newPage = ArrayList<MonkeyConversation>()
        newPage.add(newConversation(time - 5, "146"))
        newPage.add(newConversation(time - 4, "147"))
        newPage.add(newConversation(time - 3, "148"))
        newPage.add(newConversation(time - 2, "151"))
        newPage.add(newConversation(time - 1, "152"))
        newPage.add(newConversation(time, "153"))
        return newPage
    }

    @Test
    @Throws (Exception::class)
    fun crashesIfInsertNewEndItem() {
        var crashWithNewConversation = false
        var crashWithOldConversations = false
        var crashWithInsertConversations = false

        val time = System.currentTimeMillis()
        try {
            adapter.addNewConversation(newConversation(time, "151",
                    MonkeyConversation.ConversationStatus.moreConversations.ordinal))
        } catch (ex: IllegalArgumentException) {
            crashWithNewConversation = true
        }

        assert(crashWithNewConversation)


        val oldPage = createPage(time)
        oldPage.add(newConversation(time + 1, "155", 0))

        try {
            adapter.addOldConversations(oldPage, true, recycler!!)
        } catch (ex: IllegalArgumentException){
            crashWithOldConversations = true
        }

        assert(crashWithOldConversations)


        val firstPage = createPage(time)
        oldPage.add(newConversation(time + 1, "155", 0))

        try {
            adapter.insertConversations(oldPage, true)
        } catch (ex: IllegalArgumentException){
            crashWithInsertConversations = true
        }

        assert(crashWithInsertConversations)
    }
    /**
    @Test
    @Throws (Exception::class)
    fun conversationsCantBeRepeated() {
        val time = System.currentTimeMillis()
        adapter.addNewConversation(newConversation(time, "151"))
        adapter.addNewConversation(newConversation(time + 1 , "152"))
        adapter.addNewConversation(newConversation(time + 2 , "153"))

        fun addOldPage(){
            val newPage = ArrayList<MonkeyConversation>()
            newPage.add(newConversation(time - 5, "146"))
            newPage.add(newConversation(time - 4, "147"))
            newPage.add(newConversation(time - 3, "148"))
            newPage.add(newConversation(time - 2, "151"))
            newPage.add(newConversation(time - 1, "152"))
            newPage.add(newConversation(time, "153"))

            adapter.addOldConversations(newPage, true, recycler!!)
        }

        addOldPage()
        addOldPage()

        val list = adapter.takeAllConversations()
        assert(list.size == 12)
        assertThatListIsSorted(list as ArrayList<MonkeyConversation>)
        assertThatConversationsAreNotRepeated(list)
    }*/
}