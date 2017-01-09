package com.criptext.monkeykitui.recycler

import com.criptext.monkeykitui.BuildConfig
import com.criptext.monkeykitui.util.SimpleMonkeyItem
import org.amshove.kluent.`should be`
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by gesuwall on 1/9/17.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, manifest = "TestManifest.xml")
class `MonkeyAdapter Test`: AdapterTestCase() {

    @Test
    fun `removeEndOfRecyclerView() correctly removes de EndItem from the MessagesList when it is empty`() {
        val messages = adapter.messages
        messages.hasReachedEnd = false
        messages.actualSize `should be` 1
        adapter.removeEndOfRecyclerView()
        messages.actualSize `should be` 0
    }

    @Test
    fun `removeEndOfRecyclerView() correctly removes de EndItem from the MessagesList when it is not empty`() {
        val messages = adapter.messages
        //set hasReachedEnd true and add messages
        messages.addOldMessages(listOf(SimpleMonkeyItem("0", 12), SimpleMonkeyItem("0", 13)), false)
        messages.actualSize `should be` 3
        adapter.removeEndOfRecyclerView()
        messages.actualSize `should be` 2
    }

}