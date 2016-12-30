package com.criptext.monkeykitui.recycler

import com.criptext.monkeykitui.util.SimpleMonkeyItem
import org.amshove.kluent.`should equal`
import org.junit.Test

/**
 * Created by gesuwall on 12/30/16.
 */

class `MonkeyItem Comparator Test` {

    @Test
    fun `Should return -1 if left hand item has a lower timestamp than right hand item`() {
        val lhi = SimpleMonkeyItem("0", 1L)
        val rhi = SimpleMonkeyItem("1", 2L)
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` -1
    }

    @Test
    fun `Should return 1 if left hand item has a higher timestamp than right hand item`() {
        val lhi = SimpleMonkeyItem("0", 2L)
        val rhi = SimpleMonkeyItem("1", 1L)
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` 1
    }

    @Test
    fun `Should default to comparing id strings if both timestamps are equal`() {
        val lhi = SimpleMonkeyItem("0", 1L)
        val rhi = SimpleMonkeyItem("1", 1L)
        val stringCmpRes = lhi.getMessageId().compareTo(rhi.getMessageId())
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` stringCmpRes
    }
}