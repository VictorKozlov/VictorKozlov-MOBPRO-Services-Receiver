package ch.hslu.mobpro.uebung3.servicesreceiver

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat


class MusicPlayerService : Service() {
    private var alive: Boolean = false
    private var binder: IBinder? = null
    private var allowRebind: Boolean = false
    private var logTag = "Music App says: "

    override fun onCreate() {
        alive = true
        // The service is being created
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(logTag, "Starting music service")
        notificate("HSLU Music Player", "Playing <Beethoven - Für Elise>")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        alive = false
        Log.i(logTag, "Music service stopped")
        notificate("Stop", "Stop Music Player Service")
        stopForeground(true)
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {

        return allowRebind
    }

    private fun notificate(notificationTitle:String, notificationText:String ){
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = Notification.Builder(this,
            MainActivity.CHANNEL_NOTIFICATION_ID
        )
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setTicker("Test Notification Ticker")
            .build()

        startForeground(MainActivity.CHANNEL_ID, notification)
    }
}
