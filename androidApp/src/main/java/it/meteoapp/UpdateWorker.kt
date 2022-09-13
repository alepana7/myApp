package it.meteoapp

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import it.meteoapp.model.LocationsHolder
import it.meteoapp.tasks.UpdateLocationInfoTask
import java.lang.StringBuilder
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutionException

class UpdateWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    @SuppressLint("WrongThread")
    override fun doWork(): Result {
        val updateLocationInfoTask = UpdateLocationInfoTask()
        try {
            val updatedList = LocationsHolder[applicationContext]!!.locations
            updateLocationInfoTask.execute(updatedList.toMutableList()).get()
            var counter = 0
            for (location in updatedList) {
                val stringBuilder = StringBuilder()
                if (location?.temp_min!! < TEMP_MIN) stringBuilder.append(
                    """${location?.name!!.uppercase(Locale.getDefault())} :
 min_temp = ${location.temp_min}
"""
                )
                if (location.temp_min > TEMP_MAX) stringBuilder.append(
                    """${location.name!!.uppercase(Locale.getDefault())} :
 max_temp = ${location.temp_max}
"""
                )
                if (stringBuilder.length > 0) {
                    var mBuilder: NotificationCompat.Builder? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mBuilder = NotificationCompat.Builder(applicationContext, "default")
                            .setSmallIcon(R.drawable.ic_menu_info_details)
                            .setContentTitle(
                                LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
                            )
                            .setContentText(stringBuilder.toString())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    }
                    val managerCompat = NotificationManagerCompat.from(
                        applicationContext
                    )
                    managerCompat.notify(counter, mBuilder!!.build())
                    counter++
                }
            }
            return Result.success()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return Result.failure()
    }

    companion object {
        private const val TEMP_MIN = 20.0
        private const val TEMP_MAX = 30.0
    }
}