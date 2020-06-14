package com.dtakac.aux_remote.pager.queue.view_holders

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.epoxy.KotlinHolder

@EpoxyModelClass
abstract class NowPlayingSongModel: EpoxyModelWithHolder<NowPlayingSongHolder>(){
    @EpoxyAttribute lateinit var name: String

    override fun getDefaultLayout(): Int = R.layout.cell_now_playing
    override fun bind(holder: NowPlayingSongHolder) {
        holder.name.text = name
    }
}

class NowPlayingSongHolder: KotlinHolder(){
    val name by bind<TextView>(R.id.tvNowPlayingSong)
}