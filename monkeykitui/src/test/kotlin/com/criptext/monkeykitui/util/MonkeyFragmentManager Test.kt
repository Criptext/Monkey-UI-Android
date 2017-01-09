package com.criptext.monkeykitui.util

import android.view.View
import com.criptext.monkeykitui.BuildConfig
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.TestActivity
import org.amshove.kluent.`should equal`
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Created by gesuwall on 12/21/16.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, manifest = "TestManifest.xml")
class `MonkeyFragmentManager Test` {

    @Test
    fun `Should transition from a conversations fragment to a chat fragment`() {
        var controller = Robolectric.buildActivity(TestActivity::class.java)
                .create().start().visible()
        var act = controller.get()
        val activityTitle = "Test Activity"
        val mkFragmentStack = Stack<MonkeyFragmentManager.FragmentTypes>()
        val mfm = MonkeyFragmentManager(act, activityTitle, mkFragmentStack)

        //set the conversations fragment
        mfm.setContentLayout(null, true)

        //init toolbar variables before asserting
        val monkeyToolbar = mfm.monkeyToolbar!!
        val customToolbar = monkeyToolbar.customToolbar
        val appBarLayout = monkeyToolbar.appBarLayout

        customToolbar.title.text.toString() `should equal` activityTitle
        customToolbar.imageView.visibility `should equal` View.GONE
        customToolbar.subtitle.visibility `should equal` View.GONE
        appBarLayout.isActivated `should equal` false


        //Now set the chats fragment
        val contactName = "John Smith"
        val chatFragment = MonkeyChatFragment.Builder("0", contactName)
            .setLastRead(System.currentTimeMillis())
            .build();

        mfm.setChatFragment(chatFragment)
        customToolbar.title.text `should equal` contactName
        customToolbar.imageView.visibility `should equal` View.VISIBLE

    }

    @Test
    fun `MonkeyFragmentManager can set a click listener to the toolbar`() {
         var controller = Robolectric.buildActivity(TestActivity::class.java)
                .create().start().visible()
        var act = controller.get()
        val activityTitle = "Test Activity"
        val mkFragmentStack = Stack<MonkeyFragmentManager.FragmentTypes>()
        val mfm = MonkeyFragmentManager(act, activityTitle, mkFragmentStack)

        //set the layout with toolbar
        mfm.setContentLayout(null, true)

        //init toolbar variables before asserting
        val monkeyToolbar = mfm.monkeyToolbar!!
        val customToolbar = monkeyToolbar.customToolbar

        var clicks = 0
        mfm.setToolbarOnClickListener(View.OnClickListener {
            clicks++
        })

        clicks `should equal` 0

        customToolbar.secondContainer.callOnClick()
        clicks `should equal` 1

        customToolbar.secondContainer.callOnClick()
        clicks `should equal` 2

        customToolbar.secondContainer.callOnClick()
        customToolbar.secondContainer.callOnClick()
        customToolbar.secondContainer.callOnClick()
        clicks `should equal` 5
    }


}