package com.sunshine.freeform.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author sunshine
 * @date 2021/1/31
 * 软件数据库
 */
@Database(entities = [FreeFormAppsEntity::class, NotificationAppsEntity::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract val freeFormAppsDao: FreeFormAppsDao
    abstract val notificationAppsDao: NotificationAppsDao

    companion object {
        private var database: MyDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): MyDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, "database.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return database!!
        }
    }
}