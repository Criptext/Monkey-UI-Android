package com.criptext.monkeykitui.util

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.criptext.monkeykitui.BuildConfig
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.ShadowActionBar
import com.criptext.monkeykitui.TestActivity
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.internal.ShadowExtractor
import org.robolectric.util.ActivityController
import java.util.*

/**
 * Created by gesuwall on 12/21/16.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, shadows = arrayOf(ShadowActionBar::class), manifest = "TestManifest.xml")
class `MonkeyFragmentManager Test` {
    lateinit var controller: ActivityController<TestActivity>
    lateinit var act: AppCompatActivity
    val activityTitle = "Test Activity"
    lateinit var mkFragmentStack: Stack<MonkeyFragmentManager.FragmentTypes>
    lateinit var mfm: MonkeyFragmentManager

    @Before
    fun initialize() {
        controller = Robolectric.buildActivity(TestActivity::class.java)
                .create().start().visible()
        act = controller.get()
        mkFragmentStack = Stack<MonkeyFragmentManager.FragmentTypes>()
        mfm = MonkeyFragmentManager(act, activityTitle, mkFragmentStack)
    }

    @Test
    fun `Should transition from a conversations fragment to a chat fragment`() {
        //set the conversations fragment
        mfm.setContentLayout(null, true)
        val sab = ShadowExtractor.extract(act.supportActionBar) as ShadowActionBar

        //init toolbar variables before asserting
        val monkeyToolbar = mfm.monkeyToolbar!!
        val customToolbar = monkeyToolbar.customToolbar
        val appBarLayout = monkeyToolbar.appBarLayout

        sab.affordanceDisplayed `should equal` false
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
        sab.affordanceDisplayed `should equal` true
        customToolbar.title.text `should equal` contactName
        customToolbar.imageView.visibility `should equal` View.VISIBLE

    }

    @Test
    fun `When alwaysShowBackButton is enabled, both fragments show the back buton`() {
        mfm.alwaysShowBackButton = true
        mfm.setContentLayout(null, true)
        val sab = ShadowExtractor.extract(act.supportActionBar) as ShadowActionBar

        sab.affordanceDisplayed `should equal` true

        //Now set the chats fragment
        val contactName = "John Smith"
        val chatFragment = MonkeyChatFragment.Builder("0", contactName)
            .setLastRead(System.currentTimeMillis())
            .build();

        mfm.setChatFragment(chatFragment)
        sab.affordanceDisplayed `should equal` true
    }

    @Test
    fun `MonkeyFragmentManager can set a click listener to the toolbar`() {
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