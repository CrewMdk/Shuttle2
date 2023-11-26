package com.simplecityapps.localmediaprovider.local.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simplecityapps.shuttle.model.MediaProviderType
import com.simplecityapps.shuttle.sorting.PlaylistSongSortOrder

@Entity(
    tableName = "playlists"
)
data class PlaylistData(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sortOrder") val sortOrder: PlaylistSongSortOrder,
    @ColumnInfo(name = "mediaProvider") var mediaProviderType: MediaProviderType = MediaProviderType.Shuttle,
    @ColumnInfo(name = "externalId") val externalId: String? = null
)
