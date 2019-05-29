package com.simplecityapps.localmediaprovider.data.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.simplecityapps.localmediaprovider.data.room.database.MediaDatabase
import com.simplecityapps.localmediaprovider.data.room.entity.PlaylistData
import java.util.concurrent.Executors

class DatabaseProvider constructor(private val context: Context) {

    val database: MediaDatabase by lazy {
        Room.databaseBuilder(context, MediaDatabase::class.java, "song.db")
            .addMigrations(MIGRATION_23_24)
            .addCallback(callback)
            .build()
    }

    private val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            Executors.newSingleThreadScheduledExecutor().execute {
                database.playlistDataDao().insert(PlaylistData(favoritesName))
            }
        }
    }

    private val favoritesName: String = "Favorites"

    private val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `playlist_song_join` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `playlistId` INTEGER NOT NULL, `songId` INTEGER NOT NULL, FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`songId`) REFERENCES `songs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            database.execSQL("CREATE  INDEX `index_playlist_song_join_songId` ON `playlist_song_join` (`songId`)")
            database.execSQL("CREATE  INDEX `index_playlist_song_join_playlistId` ON `playlist_song_join` (`playlistId`)")
            database.execSQL("INSERT INTO playlists (name) VALUES('$favoritesName')")
        }
    }
}