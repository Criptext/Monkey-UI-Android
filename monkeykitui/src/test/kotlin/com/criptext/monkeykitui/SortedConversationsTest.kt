package com.criptext.monkeykitui

import com.criptext.monkeykitui.conversation.ConversationsList
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
class SortedConversationsTest {
    val conversations = ConversationsList()

    fun newConversation(timestamp: Long, id: String, status: Int): MonkeyConversation {
        return object : MonkeyConversation {
            override fun getAvatarFilePath() = ""
            override fun getDatetime() = timestamp
            override fun getGroupMembers() = ""
            override fun getConvId() = id
            override fun getSecondaryText() = ""
            override fun getName() = ""
            override fun getStatus() = status
            override fun getTotalNewMessages() = 0
            override fun isGroup() = false
            override fun getAdmins() = ""
        }
    }

    fun newConversation(timestamp: Long, id: String) = newConversation(timestamp, id,
            MonkeyConversation.ConversationStatus.receivedMessage.ordinal)

    fun assertThatListIsSorted(list: List<MonkeyConversation>){
        for(i in 1..(list.size-1))
            assert(MonkeyConversation.defaultComparator.compare(list[i - 1], list[i]) != 1)
            //System.out.println("${list[i].getMessageTimestampOrder()}")
    }

    fun assertThatConversationsAreNotRepeated(list: List<MonkeyConversation>){
        for(i in 0..(list.size-2))
            for(j in (i+1)..(list.size-1)) {
                assert(list[i].getConvId() != list[j].getConvId())
            }
    }

    @Test
    @Throws (Exception::class)
    fun newConversationsAddedAreSorted() {
        val time = System.currentTimeMillis()
        conversations.addNewConversation(newConversation(time, "123"))
        conversations.addNewConversation(newConversation(time + 1, "124"))
        conversations.addNewConversation(newConversation(time + 2, "125"))
        conversations.addNewConversation(newConversation(time + 4, "126"))

        conversations.addNewConversation(newConversation(time + 3, "126"))
        conversations.addNewConversation(newConversation(time + -1, "127"))
        conversations.addNewConversation(newConversation(time + 3, "128"))

        assertThatListIsSorted(conversations)

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

        conversations.addOldConversations(newPage, true)
        assertThatListIsSorted(conversations)
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

        conversations.insertConversations(newPage, true)
        assertThatListIsSorted(conversations)
        assertThatConversationsAreNotRepeated(conversations)
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
            conversations.addNewConversation(newConversation(time, "151",
                    MonkeyConversation.ConversationStatus.moreConversations.ordinal))
        } catch (ex: IllegalArgumentException) {
            crashWithNewConversation = true
        }

        assert(crashWithNewConversation)


        val oldPage = createPage(time)
        oldPage.add(newConversation(time + 1, "155", 0))

        try {
            conversations.addOldConversations(oldPage, true)
        } catch (ex: IllegalArgumentException){
            crashWithOldConversations = true
        }

        assert(crashWithOldConversations)


        val firstPage = createPage(time)
        oldPage.add(newConversation(time + 1, "155", 0))

        try {
            conversations.insertConversations(oldPage, true)
        } catch (ex: IllegalArgumentException){
            crashWithInsertConversations = true
        }

        assert(crashWithInsertConversations)
    }

    @Test
    @Throws (Exception::class)
    fun conversationsCantBeRepeated() {
        val time = System.currentTimeMillis()
        conversations.addNewConversation(newConversation(time, "151"))
        conversations.addNewConversation(newConversation(time + 1 , "152"))
        conversations.addNewConversation(newConversation(time + 2 , "153"))

        fun addOldPage(){
            val newPage = ArrayList<MonkeyConversation>()
            newPage.add(newConversation(time - 5, "146"))
            newPage.add(newConversation(time - 4, "147"))
            newPage.add(newConversation(time - 3, "148"))
            newPage.add(newConversation(time - 2, "151"))
            newPage.add(newConversation(time - 1, "152"))
            newPage.add(newConversation(time, "153"))

            conversations.addOldConversations(newPage, true)
        }

        addOldPage()
        addOldPage()
        conversations.addNewConversation(newConversation(time + 4 , "156"))

        assert(conversations.size == 7)
        assertThatListIsSorted(conversations)
        assertThatConversationsAreNotRepeated(conversations)

    }
}