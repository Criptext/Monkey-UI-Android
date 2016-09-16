package com.criptext.monkeykitui.recycler

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.widget.Toast
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.listeners.OnMessageOptionClicked
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 9/13/16.
 */

class MessageOptions(val ctx: Context) {

    var deleteFunction: ((MonkeyItem) -> Unit)? = null
    var unsendFunction: ((MonkeyItem) -> Unit)? = null

    constructor(ctx: Context, delete: (MonkeyItem) -> Unit, unsend: (MonkeyItem) -> Unit): this(ctx){
        deleteFunction = delete
        unsendFunction = unsend
    }

    fun copyToClipboard(item: MonkeyItem){
        val clipboard =  ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MonkeyKitCopy", item.getMessageText());
        clipboard.primaryClip = clip;
        Toast.makeText(ctx, "Message copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareText(item: MonkeyItem){
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, item.getMessageText())
            ctx.startActivity(Intent.createChooser(share, ctx.resources.getString(R.string.mk_action_share_with)))
    }

    fun sharePhoto(item: MonkeyItem){
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(item.getFilePath())))
        ctx.startActivity(Intent.createChooser(share, ctx.resources.getString(R.string.mk_action_share_with)))
    }

    fun shareAudio(item: MonkeyItem){
        val share = Intent(Intent.ACTION_SEND)
        share.type = "audio/*"
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(item.getFilePath())))
        ctx.startActivity(Intent.createChooser(share, ctx.resources.getString(R.string.mk_action_share_with)))
    }

    fun initSentMessageOptions(): HashMap<Int, MutableList<OnMessageOptionClicked>>
    {
        val res = ctx.resources
        val optionsMap = HashMap<Int, MutableList<OnMessageOptionClicked>>()
        optionsMap.put(MonkeyItem.MonkeyItemType.text.ordinal, mutableListOf( //text messages options
            object: OnMessageOptionClicked(res.getString(R.string.mk_action_copy)){
                override fun invoke(p1: MonkeyItem) {
                    copyToClipboard(p1)
                }
            },
            object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                override fun invoke(p1: MonkeyItem) {
                    unsendFunction?.invoke(p1)
                }
            },
            object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                override fun invoke(p1: MonkeyItem) {
                    deleteFunction?.invoke(p1)
                }
            }
        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.audio.ordinal, mutableListOf( //audio messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                    override fun invoke(p1: MonkeyItem) {
                        shareAudio(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                    override fun invoke(p1: MonkeyItem) {
                        unsendFunction?.invoke(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }
        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.photo.ordinal, mutableListOf( //photo messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                    override fun invoke(p1: MonkeyItem) {
                        sharePhoto(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                    override fun invoke(p1: MonkeyItem) {
                        unsendFunction?.invoke(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }

        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.file.ordinal, mutableListOf( //file messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_unsend)){
                    override fun invoke(p1: MonkeyItem) {
                        unsendFunction?.invoke(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }
        ))

        return optionsMap
    }

    fun initReceivedMessageOptions(): HashMap<Int, MutableList<OnMessageOptionClicked>>
    {
        val res = ctx.resources
        val optionsMap = HashMap<Int, MutableList<OnMessageOptionClicked>>()
        optionsMap.put(MonkeyItem.MonkeyItemType.text.ordinal, mutableListOf( //text messages options
            object: OnMessageOptionClicked(res.getString(R.string.mk_action_copy)){
                override fun invoke(p1: MonkeyItem) {
                    copyToClipboard(p1)
                }
            },
            object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                override fun invoke(p1: MonkeyItem) {
                    deleteFunction?.invoke(p1)
                }
            }
        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.audio.ordinal, mutableListOf( //audio messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                    override fun invoke(p1: MonkeyItem) {
                        shareAudio(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }
        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.photo.ordinal, mutableListOf( //photo messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_share)){
                    override fun invoke(p1: MonkeyItem) {
                        sharePhoto(p1)
                    }
                },
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }
        ))
        optionsMap.put(MonkeyItem.MonkeyItemType.file.ordinal, mutableListOf( //file messages options
                object: OnMessageOptionClicked(res.getString(R.string.mk_action_del)){
                    override fun invoke(p1: MonkeyItem) {
                        deleteFunction?.invoke(p1)
                    }
                }
        ))

        return optionsMap
    }
}