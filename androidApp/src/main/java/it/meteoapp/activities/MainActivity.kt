package it.meteoapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import it.meteoapp.UpdateWorker
import it.meteoapp.fragments.ListFragment
import java.util.concurrent.TimeUnit

class MainActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        val mNotificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "TEMP_CHANNEL",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setDescription("Temperature Channel Notification")
            mNotificationManager.createNotificationChannel(channel)
        }
        val periodicWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(UpdateWorker::class.java, 15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UPDATE",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        return ListFragment()
    }
}