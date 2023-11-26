package com.simplecityapps.shuttle.ui.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.simplecityapps.playback.PlaybackState

class PlayStateImageButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    var state: PlaybackState = PlaybackState.Paused
        set(value) {
            field = value

            when (state) {
                PlaybackState.Loading -> setImageResource(com.simplecityapps.playback.R.drawable.ic_pause_black_24dp)
                PlaybackState.Playing -> setImageResource(com.simplecityapps.playback.R.drawable.ic_pause_black_24dp)
                PlaybackState.Paused -> setImageResource(com.simplecityapps.playback.R.drawable.ic_play_arrow_black_24dp)
            }
        }

    init {
        state = PlaybackState.Paused
    }
}
