package it.meteoapp

import it.meteoapp.model.Location
import org.json.JSONException
import org.json.JSONObject

class OpenWeatherResponseParser {
    fun getLocationInfo(jsonObject: JSONObject): Location {
        val location = Location()
        try {
            location.name = jsonObject.getString("name").replace("\"", "")
            val weather =
                JSONObject(jsonObject.getString("weather").replace("[", "").replace("]", ""))
            val main = jsonObject.getJSONObject("main")
            location.weather_descr = weather.getString("description").replace("\"", "")
            val stringBuilder = "x" +
                    weather.getString("icon").replace("\"", "") +
                    "2x"
            location.weather_icon = stringBuilder
            location.temp = Math.floor(main.getDouble("temp") * 10) / 10
            location.temp_min = Math.floor(main.getDouble("temp_min") * 10) / 10
            location.temp_max = Math.floor(main.getDouble("temp_max") * 10) / 10
            location.pressure = Math.floor(main.getDouble("pressure") * 10) / 10
            location.humidity = Math.floor(main.getDouble("humidity") * 10) / 10
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return location
    }

    companion object {
        var instance: OpenWeatherResponseParser? = null
            get() {
                if (field == null) field = OpenWeatherResponseParser()
                return field
            }
            private set
    }
}