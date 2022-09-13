package it.meteoapp

import androidx.room.*
import it.meteoapp.model.Location

@Dao
interface LocationDao {
    @get:Query("SELECT * FROM location")
    val locations: List<Location?>?

    @Query("SELECT * FROM location WHERE name = :locationName ")
    fun getLocation(locationName: String?): Location?

    @Insert
    fun insertLocation(location: Location?): Long

    @Delete
    fun deleteLocation(location: Location?)

    @Update
    fun updateLocation(location: Location?)
}