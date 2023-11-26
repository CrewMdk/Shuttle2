package com.simplecityapps.shuttle.di

import com.simplecityapps.playback.di.PlaybackModule
import com.simplecityapps.provider.emby.di.EmbyMediaProviderModule
import com.simplecityapps.provider.jellyfin.di.JellyfinMediaProviderModule
import com.simplecityapps.provider.plex.di.PlexMediaProviderModule
import com.simplecityapps.shuttle.remote_config.RemoteConfigModule
import com.simplecityapps.trial.di.TrialModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        AppModule::class,
        PlaybackModule::class,
        RepositoryModule::class,
        NetworkingModule::class,
        CoroutineModule::class,
        PersistenceModule::class,
        MediaProviderModule::class,
        EmbyMediaProviderModule::class,
        JellyfinMediaProviderModule::class,
        PlexMediaProviderModule::class,
        ImageLoaderModule::class,
        TrialModule::class,
        RemoteConfigModule::class
    ]
)
interface AppComponent
