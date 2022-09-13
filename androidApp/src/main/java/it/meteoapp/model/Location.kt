package it.meteoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "location")
class Location {
    @PrimaryKey(autoGenerate = false)
    var id: String
    var name: String? = null
    var weather_descr: String? = null
    var weather_icon: String? = null
    var temp = 0.0
    var temp_min = 0.0
    var temp_max = 0.0
    var pressure = 0.0
    var humidity = 0.0
    override fun toString(): String {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", weather_descr='" + weather_descr + '\'' +
                ", weather_icon='" + weather_icon + '\'' +
                ", temp=" + temp +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                '}'
    }

    init {
        id = UUID.randomUUID().toString()
    }
}