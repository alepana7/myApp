package it.meteoapp.tasks

import android.os.AsyncTask
import android.util.Log
import it.meteoapp.Constants
import it.meteoapp.DBManager
import org.json.JSONObject
import it.meteoapp.OpenWeatherResponseParser
import it.meteoapp.model.Location
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class UpdateLocationInfoTask : AsyncTask<MutableList<Location>, Void?, MutableList<Location>>() {

    override fun doInBackground(vararg p0: MutableList<Location>?): MutableList<Location>? {
        var locations = p0[0]
        if (locations!!?.isEmpty()) locations.addAll(
            DBManager.instance?.locationDao()?.locations as Collection<Location>
        ) else locations = DBManager.instance?.locationDao()?.locations as MutableList<Location>?
        var index = 0
        for (loc in locations!!) {
            try {
                val url =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=" + loc?.name + "&units=metric&lang=it&appid=" + Constants.KEY)
                Log.i(Constants.OPEN_WEATHER, url.toString())
                val connection = url.openConnection() as HttpURLConnection
                val `in` = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(`in`))
                val stringBuilder = StringBuilder()
                var input: String?
                while (bufferedReader.readLine().also { input = it } != null) stringBuilder.append(
                    input
                )
                bufferedReader.close()
                `in`.close()
                val jsonObject = JSONObject(stringBuilder.toString())
                val locId = loc.id
                var loc_ = loc
                loc_ = OpenWeatherResponseParser.instance?.getLocationInfo(jsonObject)!!
                loc_?.id = locId!!
                Log.i(Constants.OPEN_WEATHER, loc_.toString())
                locations[index] = loc_
                DBManager.instance?.locationDao()?.updateLocation(loc_)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            index++
        }
        return locations
    }
}