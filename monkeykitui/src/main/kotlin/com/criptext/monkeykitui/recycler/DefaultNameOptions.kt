package com.criptext.monkeykitui.recycler

import android.content.res.Resources
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.listeners.OnMessageOptionClicked
import java.util.*

/**
 * Created by gesuwall on 9/13/16.
 */

class DefaultNameOptions {
    companion object {
        fun initSentMessageOptions(res: Resources): HashMap<Int, MutableList<OnMessageOptionClicked>>
        {
            val optionsMap = HashMap<Int, MutableList<OnMessageOptionClicked>>()
            optionsMap.put(MonkeyItem.MonkeyItemType.text.ordinal, mutableListOf( //text messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_copy)){
                    override fun invoke(p1: MonkeyItem) {
                        //TODO copy to clipboard
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                    override fun invoke(p1: MonkeyItem) {
                        //TODO unsend
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        //TODO del
                    }
                }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.audio.ordinal, mutableListOf( //audio messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_save_gallery)) {
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.photo.ordinal, mutableListOf( //photo messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_save_gallery)) {
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.file.ordinal, mutableListOf( //file messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))

            return optionsMap
        }

        fun initReceivedMessageOptions(res: Resources): HashMap<Int, MutableList<OnMessageOptionClicked>>
        {
            val optionsMap = HashMap<Int, MutableList<OnMessageOptionClicked>>()
            optionsMap.put(MonkeyItem.MonkeyItemType.text.ordinal, mutableListOf( //text messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_copy)){
                    override fun invoke(p1: MonkeyItem) {
                        //TODO copy to clipboard
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        //TODO del
                    }
                }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.audio.ordinal, mutableListOf( //audio messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_save_gallery)) {
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.photo.ordinal, mutableListOf( //photo messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_save_gallery)) {
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))
            optionsMap.put(MonkeyItem.MonkeyItemType.file.ordinal, mutableListOf( //file messages options
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO unsend
                        }
                    },
                    object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                        override fun invoke(p1: MonkeyItem) {
                            //TODO del
                        }
                    }
            ))

            return optionsMap
        }
    }
}