package it.meteoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.meteoapp.model.Location

@Database(entities = [Location::class], version = 3, exportSchema = false)
abstract class DBManager : RoomDatabase() {
    abstract fun locationDao(): LocationDao?

    companion object {
        const val DATABASE_NAME = "location_db"
        var instance: DBManager? = null
            private set

        fun init(context: Context): DBManager? {
            if (instance == null) instance = Room.databaseBuilder(
                context.applicationContext,
                DBManager::class.java, DATABASE_NAME
            )
                .build()
            return instance
        }
    }
}