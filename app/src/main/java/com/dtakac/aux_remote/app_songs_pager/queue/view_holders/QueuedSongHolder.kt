package com.dtakac.aux_remote.app_songs_pager.queue.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.epoxy.KotlinHolder

@EpoxyModelClass
abstract class QueuedSongModel: EpoxyModelWithHolder<QueuedSongHolder>(){
    @EpoxyAttribute lateinit var position: String
    @EpoxyAttribute lateinit var name: String
    @EpoxyAttribute var userIconVisibility = View.GONE

    override fun getDefaultLayout(): Int = R.layout.cell_queued_song
    override fun bind(holder: QueuedSongHolder) {
        holder.position.text = position
        holder.name.text = name
        holder.userIcon.visibility = userIconVisibility
    }
}

class QueuedSongHolder: KotlinHolder(){
    val position by bind<TextView>(R.id.tvPosition)
    val name by bind<TextView>(R.id.tvQueuedSong)
    val userIcon by bind<ImageView>(R.id.ivUserIcon)
}