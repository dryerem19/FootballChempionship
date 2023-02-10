package com.example.russianfootballchempionship.Entities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Game :: class, Team :: class], version = 2)
abstract class DbContext : RoomDatabase() {

    abstract fun gameDao() : GameDao
    abstract fun teamDao() : TeamDao

    companion object {

        @Volatile
        private var INSTANCE : DbContext? = null

        fun getContext(context: Context) : DbContext {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbContext::class.java, "championship"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}