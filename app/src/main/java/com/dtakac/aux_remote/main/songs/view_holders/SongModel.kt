package com.dtakac.aux_remote.main.songs.view_holders

import android.text.SpannableString
import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.epoxy.KotlinHolder

@EpoxyModelClass
abstract class SongModel: EpoxyModelWithHolder<SongHolder>(){
    @EpoxyAttribute lateinit var name: SpannableString
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