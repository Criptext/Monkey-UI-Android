package com.criptext.monkeykitui

import org.amshove.kluent.`should equal`
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by gesuwall on 12/20/16.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class `Chat Fragment Tests` {

    @Test
    fun `Builder object should add all variables to the fragments arguments bundle`() {
        val chatId = "123456789"
        val chatName = "My Test Chat"
        val avatarURL = "/monkeykitphotos/user/123456789"
        val lastRead = 1000000000L
        val layoutId = 100
        val fragment = MonkeyChatFragment.Builder(chatId, chatName)
                        .setAvatarURL(avatarURL)
                        .setLastRead(lastRead)
                        .setReachedEnd(true)
                        .setLayoutId(layoutId)
                        .build()

        val args = fragment.arguments
        args.getString(MonkeyChatFragment.chatConversationId) `should equal` chatId
        args.getString(MonkeyChatFragment.chatTitleName) `should equal` chatName
        args.getString(MonkeyChatFragment.chatAvatarUrl) `should equal` avatarURL
        args.getLong(MonkeyChatFragment.initalLastReadValue) `should equal` lastRead
        args.getInt(MonkeyChatFragment.chatLayoutId) `should equal` layoutId
        args.getBoolean(MonkeyChatFragment.chatHasReachedEnd) `should equal` true


    }
}
