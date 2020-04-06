package ch.hslu.mobpro.uebung3.servicesreceiver

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        @JvmField var CHANNEL_ID: Int = 1
        @JvmField var CHANNEL_NOTIFICATION_ID = "ch.hslu.mobpro.uebung3.servicesreceiver"
    }

    private var musicServiceConnection: MusicPlayerConnection? = null
    private val myBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            receiver_txt_message.text = intent.getStringExtra(getString(R.string.key_message))
        }
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
        service_btn_next.setOnClickListener{
            nextSong()
        }
        service_btn_get_history.setOnClickListener{
            showHistory()
        }
        receiver_btn_send_broadcast.setOnClickListener{
            sendBroadcast()
        }
        ckb_receiver.setOnClickListener{
            initBroadcast()
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

    private fun initBroadcast(){
        if(ckb_receiver.isChecked){
            var filter =  IntentFilter("ACTION_MY_BROADCAST");
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(myBroadcastReceiver, filter);
            Log.d("receiver", "Registered receiver");
        }
        else{
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(myBroadcastReceiver)
            Log.d("receiver", "Unregistered receiver");
        }
    }

    private fun sendBroadcast(){
        Log.d("sender", "Broadcasting message");
        val localBroadcast = Intent("ACTION_MY_BROADCAST")
        localBroadcast.putExtra("message", "Here is my local broadcast message!")
        LocalBroadcastManager.getInstance(this).sendBroadcast(localBroadcast)
    }
}
