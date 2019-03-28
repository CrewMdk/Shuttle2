package au.com.simplecityapps.shuttle.imageloading.glide.loader.remote.lastm

import au.com.simplecityapps.shuttle.imageloading.glide.loader.remote.RemoteArtworkModelLoader
import au.com.simplecityapps.shuttle.imageloading.glide.loader.remote.RemoteArtworkProvider
import au.com.simplecityapps.shuttle.imageloading.networking.lastfm.LastFmService
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.simplecityapps.mediaprovider.model.Album
import com.simplecityapps.mediaprovider.model.Song
import java.io.InputStream

class LastFmRemoteSongArtworkModelLoader(
    private val lastFm: LastFmService.LastFm,
    private val remoteArtworkModelLoader: RemoteArtworkModelLoader
) : ModelLoader<Song, InputStream> {

    override fun buildLoadData(model: Song, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val album = Album(-1, model.albumName, model.albumArtistId, model.albumArtistName, 0, 0, 0)
        return remoteArtworkModelLoader.buildLoadData(LastFmRemoteAlbumArtworkModelLoader.LastFmAlbumRemoteArtworkProvider(lastFm, album), width, height, options)
    }

    override fun handles(model: Song): Boolean {
        return true
    }


    class Factory(
        private val lastFm: LastFmService.LastFm
    ) : ModelLoaderFactory<Song, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Song, InputStream> {
            return LastFmRemoteSongArtworkModelLoader(lastFm, multiFactory.build(RemoteArtworkProvider::class.java, InputStream::class.java) as RemoteArtworkModelLoader)
        }

        override fun teardown() {

        }
    }
}