package it.meteoapp.model

import android.content.Context
import it.meteoapp.tasks.UpdateLocationInfoTask
import java.util.ArrayList
import java.util.concurrent.ExecutionException

class LocationsHolder private constructor(context: Context) {
    val locations: MutableList<Location>
    fun getLocation(id: String): Location? {
        for (location in locations) if (location?.id == id) return location
        return null
    }

    companion object {
        private var sLocationsHolder: LocationsHolder? = null
        @JvmStatic
        operator fun get(context: Context): LocationsHolder? {
            if (sLocationsHolder == null) sLocationsHolder = LocationsHolder(context)
            return sLocationsHolder
        }
    }

    init {
        locations = ArrayList()
        val updateLocationInfoTask = UpdateLocationInfoTask()
        try {
            updateLocationInfoTask.execute(locations).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}