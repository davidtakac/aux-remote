package com.dtakac.aux_remote.app_songs_pager.all_songs.wrapper

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class SongWrapper(
    val id: Int,
    val name: String,
    highlightText: String,
    highlightColor: Int
){
    companion object{
        const val NO_COLOR = -1
        const val NO_HIGHLIGHT = ""
    }

    val highlightedName = SpannableString(name)

    init {
        if(!(highlightText == NO_HIGHLIGHT && highlightColor == NO_COLOR)){
            val startIndex = name.indexOf(highlightText, 0, true)

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