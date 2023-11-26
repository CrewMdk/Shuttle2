package com.simplecityapps.localmediaprovider.local.repository

import com.simplecityapps.localmediaprovider.local.data.room.dao.SongDataDao
import com.simplecityapps.mediaprovider.repository.artists.AlbumArtistQuery
import com.simplecityapps.mediaprovider.repository.artists.AlbumArtistRepository
import com.simplecityapps.mediaprovider.repository.artists.comparator
import com.simplecityapps.shuttle.model.AlbumArtist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class LocalAlbumArtistRepository(val scope: CoroutineScope, private val songDataDao: SongDataDao) : AlbumArtistRepository {

    private val albumArtistsRelay: StateFlow<List<AlbumArtist>?> by lazy {
        songDataDao
            .getAll()
            .map { songs ->
                songs
                    .groupBy { song -> song.albumArtistGroupKey }
                    .map { (key, songs) ->
                        AlbumArtist(
                            name = songs.firstOrNull { it.albumArtist != null }?.albumArtist,
                            artists = songs.flatMap { it.artists }.distinct(),
                            albumCount = songs.distinctBy { it.album }.size,
                            songCount = songs.size,
                            playCount = songs.minOfOrNull { it.playCount } ?: 0,
                            groupKey = key,
                            mediaProviders = songs.map { it.mediaProvider }.distinct()
                        )
                    }
            }
            .flowOn(Dispatchers.IO)
            .stateIn(scope, SharingStarted.Lazily, null)
    }

    override fun getAlbumArtists(query: AlbumArtistQuery): Flow<List<AlbumArtist>> {
        return albumArtistsRelay
            .filterNotNull()
            .map { albumArtists ->
                albumArtists
                    .filter(query.predicate)
                    .toMutableList()
                    .sortedWith(query.sortOrder.comparator)
            }
            .flowOn(Dispatchers.IO)
    }
}
