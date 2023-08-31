package com.example.data.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AlarmDbModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {

        private var database: AppDatabase? = null
        private val LOCK = Any()
        private const val NAME_DB = "alarms.db"

        fun getInstance(application: Application): AppDatabase {

            database?.let {
                return it
            }
            synchronized(LOCK){
                database?.let{
                    return it
                }
            }
            val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    NAME_DB
                ).build()

            database = db

            return db
        }
    }
}