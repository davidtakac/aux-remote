package com.dtakac.aux_remote.songs_pager.all_songs.view_holders

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.base.KotlinHolder

@EpoxyModelClass
abstract class SongModel: EpoxyModelWithHolder<SongHolder>(){
    @EpoxyAttribute lateinit var name: String
    @EpoxyAttribute(hash = false) lateinit var clickListener: View.OnClickListener

    override fun getDefaultLayout(): Int = R.layout.cell_song
    override fun bind(holder: SongHolder) {
        holder.name.text = name
        holder.view.setOnClickListener { clickListener.onClick(it) }
    }
}

class SongHolder: KotlinHolder(){
    val name by bind<TextView>(R.id.tvSong)
}