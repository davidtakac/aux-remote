package com.dtakac.aux_remote.main.queue.view_holders

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.epoxy.KotlinHolder

@EpoxyModelClass
abstract class QueuedSongInfoModel: EpoxyModelWithHolder<QueuedSongInfoHolder>(){
    @EpoxyAttribute
    lateinit var owner: String

    override fun getDefaultLayout(): Int = R.layout.cell_queued_song_info
    override fun bind(holder: QueuedSongInfoHolder) {
        holder.tvOwner.text = owner
    }
}

class QueuedSongInfoHolder: KotlinHolder(){
    val tvOwner by bind<TextView>(R.id.tvOwnerId)
}