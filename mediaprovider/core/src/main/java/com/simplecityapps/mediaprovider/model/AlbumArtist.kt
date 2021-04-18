package com.simplecityapps.mediaprovider.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
@Parcelize
data class AlbumArtist(
    val name: String?,
    val artists: List<String>,
    val albumCount: Int,
    val songCount: Int,
    val playCount: Int,
    val groupKey: ArtistGroupKey
) : Parcelable

val AlbumArtist.friendlyName: String?
    get() {
        return name
            ?: if (artists.size == 1) {
                artists.first()
            } else {
                artists.groupBy { it.toLowerCase(Locale.getDefault()).removeArticles() }
                    .map { map -> map.value.maxByOrNull { it.length } }
                    .joinToString(", ")
                    .ifEmpty { "Unknown" }
            }
    }