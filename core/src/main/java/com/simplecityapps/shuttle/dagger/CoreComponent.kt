package com.simplecityapps.shuttle.dagger

import android.content.SharedPreferences
import com.simplecityapps.mediaprovider.repository.AlbumArtistRepository
import com.simplecityapps.mediaprovider.repository.AlbumRepository
import com.simplecityapps.mediaprovider.repository.PlaylistRepository
import com.simplecityapps.mediaprovider.repository.SongRepository
import com.simplecityapps.playback.NoiseManager
import com.simplecityapps.playback.PlaybackManager
import com.simplecityapps.playback.PlaybackNotificationManager
import com.simplecityapps.playback.PlaybackWatcher
import com.simplecityapps.playback.dagger.PlaybackModule
import com.simplecityapps.playback.mediasession.MediaSessionManager
import com.simplecityapps.playback.persistence.PlaybackPreferenceManager
import com.simplecityapps.playback.queue.QueueManager
import com.simplecityapps.playback.queue.QueueWatcher
import com.simplecityapps.playback.sleeptimer.SleepTimer
import com.simplecityapps.taglib.ArtworkProvider
import com.simplecityapps.taglib.FileScanner
import dagger.Component
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class,
        NetworkingModule::class,
        PlaybackModule::class,
        PersistenceModule::class,
        TagLibModule::class
    ]
)
interface CoreComponent {

    fun getSongRepository(): SongRepository

    fun getAlbumRepository(): AlbumRepository

    fun getAlbumArtistRepository(): AlbumArtistRepository

    fun getPlaylistRepository(): PlaylistRepository

    fun getOkHttpClient(): OkHttpClient

    fun getPlaybackManager(): PlaybackManager

    fun getPlaybackWatcher(): PlaybackWatcher

    fun getQueueManager(): QueueManager

    fun getQueueWatcher(): QueueWatcher

    fun getPlaybackNotificationManager(): PlaybackNotificationManager

    fun getSharedPreferences(): SharedPreferences

    fun getPlaybackPreferenceManager(): PlaybackPreferenceManager

    fun getMediaSessionManager(): MediaSessionManager

    fun getNoiseManager(): NoiseManager

    fun getSleepTimer(): SleepTimer

    fun getFileScanner(): FileScanner

    fun getArtworkProvider(): ArtworkProvider

    fun getGsonConverterFactory(): GsonConverterFactory

    @Component.Builder
    interface Builder {
        fun build(): CoreComponent
        fun repositoryModule(module: RepositoryModule): Builder
        fun playbackModule(module: PlaybackModule): Builder
        fun persistenceModule(module: PersistenceModule): Builder
    }
}