package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        MatchedPeerEntity::class,
        ChatMessageEntity::class,
        LeaderboardEntryEntity::class,
        SafetyReportEntity::class,
        PointTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RollCallDatabase : RoomDatabase() {
    abstract fun rollCallDao(): RollCallDao

    companion object {
        @Volatile
        private var INSTANCE: RollCallDatabase? = null

        fun getInstance(context: Context): RollCallDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RollCallDatabase::class.java,
                    "roll_call_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
