package com.example.labelguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.labelguard.data.model.ProductScanEntity

@Database(entities = [ProductScanEntity::class], version = 1, exportSchema = false)
abstract class LabelGuardDatabase : RoomDatabase() {

    abstract fun productScanDao(): ProductScanDao

    companion object {
        @Volatile
        private var INSTANCE: LabelGuardDatabase? = null

        fun getDatabase(context: Context): LabelGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabelGuardDatabase::class.java,
                    "label_guard_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
