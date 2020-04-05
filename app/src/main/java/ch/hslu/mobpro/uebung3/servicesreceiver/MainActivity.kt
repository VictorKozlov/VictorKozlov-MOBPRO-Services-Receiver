package ch.hslu.mobpro.uebung3.servicesreceiver

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        @JvmField var CHANNEL_ID: Int = 1
        @JvmField var CHANNEL_NOTIFICATION_ID = "ch.hslu.mobpro.uebung3.servicesreceiver"
    }

    private var musicServiceConnection: MusicPlayerConnection? = null

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
        service_btn_next.setOnClickListener{
            nextSong()
        }
        service_btn_get_history.setOnClickListener{
            showHistory()
        }
        ckb_bound.isChecked = false
    }

    private fun startPlayerClicked() {
        startService(Intent(this, MusicPlayerService::class.java))
        bindService()
    }

    private fun stopPlayerClicked() {
        stopService(Intent(this, MusicPlayerService::class.java))
        unbindService()
    }

    private fun nextSong(){
        musicServiceConnection?.getApi()?.playNextItem()
    }

    private fun showHistory(){
        var data = musicServiceConnection?.getApi()?.queryHistory()
        var dataAsStringList : MutableList<String> = mutableListOf()
        data?.forEach { dataAsStringList.add("${it.bandName} - ${it.songName}") }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("Music player history")
        builder.setNeutralButton("OK, GOT IT"){ _, _ -> }
        builder.setItems(dataAsStringList.toTypedArray()){ _, _ -> }
        builder.create()
        builder.show()
    }

    override fun onDestroy() {
        stopService(Intent(this, MusicPlayerService::class.java))
        super.onDestroy()
    }

    private fun createNotificationChannel() {
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

    private fun bindService() {
        val demoService = Intent(this, MusicPlayerService::class.java)
        musicServiceConnection = MusicPlayerConnection()
        musicServiceConnection?.let { it ->
            bindService(demoService, it, Context.BIND_AUTO_CREATE)
        }
        ckb_bound.isChecked = true
    }

    private fun unbindService() {
        unbindService(musicServiceConnection as ServiceConnection)
        musicServiceConnection = null
        ckb_bound.isChecked = false
    }
}
