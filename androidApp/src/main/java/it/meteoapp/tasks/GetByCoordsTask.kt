package it.meteoapp.tasks

import android.os.AsyncTask
import android.util.Log
import it.meteoapp.Constants
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

class GetByCoordsTask : AsyncTask<Double?, Void?, Location?>() {
    protected override fun doInBackground(vararg p0: Double?): Location {
        var loc: Location = Location()
        try {
            val url =
                URL("https://api.openweathermap.org/data/2.5/weather?lat=" + p0[0] + "&lon=" + p0[1] + "&units=metric&lang=it&appid=" + Constants.KEY)
            Log.i(Constants.OPEN_WEATHER, url.toString())
            val connection = url.openConnection() as HttpURLConnection
            val `in` = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(`in`))
            val stringBuilder = StringBuilder()
            var input: String?
            while (bufferedReader.readLine()
                    .also { input = it } != null
            ) stringBuilder.append(input)
            bufferedReader.close()
            `in`.close()
            val jsonObject = JSONObject(stringBuilder.toString())
            loc = OpenWeatherResponseParser.instance?.getLocationInfo(jsonObject)!!
            Log.i(Constants.OPEN_WEATHER, loc.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return loc
    }
}