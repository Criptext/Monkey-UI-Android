package com.criptext.monkeykitui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by daniel on 8/11/16.
 */

open class MonkeyInfoFragment: Fragment(){

    open val conversationsLayout: Int
        get() = R.layout.fragment_info

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(conversationsLayout, null)

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
