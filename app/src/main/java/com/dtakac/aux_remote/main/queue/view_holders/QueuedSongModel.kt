package com.dtakac.aux_remote.main.queue.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.epoxy.KotlinHolder

@EpoxyModelClass
abstract class QueuedSongModel: EpoxyModelWithHolder<QueuedSongHolder>(){
    @EpoxyAttribute lateinit var position: String
    @EpoxyAttribute lateinit var name: String
    @EpoxyAttribute var userIconVisibility = View.GONE
    @EpoxyAttribute (hash = false) lateinit var onClick: View.OnClickListener
    @EpoxyAttribute var expanded: Boolean = false

    override fun getDefaultLayout(): Int = R.layout.cell_queued_song
    override fun bind(holder: QueuedSongHolder) {
        holder.position.text = position
        holder.name.text = name
        holder.userIcon.visibility = userIconVisibility
        holder.clQueuedSong.setOnClickListener(onClick)
    }
}

class QueuedSongHolder: KotlinHolder(){
    val clQueuedSong by bind<ConstraintLayout>(R.id.clQueuedSong)
    val position by bind<TextView>(R.id.tvPosition)
    val name by bind<TextView>(R.id.tvQueuedSong)
    val userIcon by bind<ImageView>(R.id.ivUserIcon)
}