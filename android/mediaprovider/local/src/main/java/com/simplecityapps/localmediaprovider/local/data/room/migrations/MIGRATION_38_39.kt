package com.simplecityapps.localmediaprovider.local.data.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_38_39 =
    object : Migration(38, 39) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS playlists2 (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, sortOrder TEXT NOT NULL DEFAULT 'Position', `mediaProvider` TEXT NOT NULL DEFAULT 'Shuttle',`externalId` TEXT)")
            db.execSQL(
                "INSERT INTO playlists2 (id, name, sortOrder) " +
                    "SELECT id, name, sortOrder " +
                    "FROM playlists"
            )
            db.execSQL("DROP TABLE playlists")
            db.execSQL("ALTER TABLE playlists2 RENAME TO playlists")
        }
    }
