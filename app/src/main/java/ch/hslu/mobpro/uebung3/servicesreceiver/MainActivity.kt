package ch.hslu.mobpro.uebung3.servicesreceiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        @JvmField var CHANNEL_ID: Int = 1
        @JvmField var CHANNEL_NOTIFICATION_ID = "ch.hslu.mobpro.uebung3.servicesreceiver"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureUI()
        createNotificationChannel()
    }

    private fun configureUI(){
        service_btnStart.setOnClickListener{
            startPlayerClicked()
        }
        service_btnStop.setOnClickListener{
            stopPlayerClicked()
        }
    }

    private fun startPlayerClicked() {
        startService(Intent(this, MusicPlayerService::class.java))
    }

    private fun stopPlayerClicked() {
        stopService(Intent(this, MusicPlayerService::class.java))
    }

    override fun onDestroy() {
        //TODO:if Service alive -> stop
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

            val name = getString(R.string.musicplayerservice_Channel_name)
            val descriptionText = getString(R.string.musicplayerservice_Channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_NOTIFICATION_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
    }
}
