package com.criptext.monkeykitui.recycler.holders

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 12/28/16.
 */

interface MessageListUI {

    fun notifyDataSetChanged()

    fun notifyItemChanged(pos: Int)

    fun notifyItemInserted(pos: Int)

    fun notifyItemRangeInserted(pos: Int, count: Int)

    fun notifyItemRangeRemoved(pos: Int, count: Int)

    fun notifyItemRemoved(pos: Int)

    fun removeLoadingView()

    fun rebindMonkeyItem(item: MonkeyItem)

    fun findLastVisibleItemPosition(): Int

    fun scrollToPosition(pos: Int)

    fun scrollWithOffset(newItemsCount: Int)
}