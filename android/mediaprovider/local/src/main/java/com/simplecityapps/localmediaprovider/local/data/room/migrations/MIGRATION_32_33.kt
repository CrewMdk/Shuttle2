package com.simplecityapps.localmediaprovider.local.data.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_32_33 =
    object : Migration(32, 33) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE songs ADD COLUMN replayGainTrack REAL")
            db.execSQL("ALTER TABLE songs ADD COLUMN replayGainAlbum REAL")
        }
    }
