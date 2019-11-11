package com.dtakac.aux_remote.songs_pager.all_songs

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.dtakac.aux_remote.data.song.Song

const val NO_COLOR = -1
class SongWrapper(
    val song: Song,
    highlightText: String,
    highlightColor: Int
){
    val highlightedName = SpannableString(song.name)

    init {
        if(!(highlightText.isBlank() && highlightColor == NO_COLOR)){
            val startIndex = song.name.indexOf(highlightText, 0, true)

            if(startIndex != -1){
                val endIndex = startIndex + highlightText.length
                highlightedName.setSpan(
                    ForegroundColorSpan(highlightColor),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}