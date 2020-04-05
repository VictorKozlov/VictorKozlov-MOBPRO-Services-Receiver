package ch.hslu.mobpro.uebung3.servicesreceiver

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log


class MusicPlayerService : Service() {
    private var alive: Boolean = false
    private var musicPlayerApi: IBinder? = null
    private var allowRebind: Boolean = false
    private var logTag = "Music App says"

    private var songs: List<Song>? = null
    private var songsIterator : ListIterator<Song>? = null
    private var history: MutableList<Song>? = null

    //val model: ViewModelMain by viewModels

    override fun onCreate() {
        initializeService()
        configureService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(logTag, "Starting music service")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        alive = false
        Log.i(logTag, "Music service stopped")
        notificate("Stop", "Stop Music Player Service")
        if(alive){
            stopForeground(true)}
    }

    fun getSongs(): List<Song>? {
        Log.i(logTag, "Returning list of songs")
        return songs
    }

    private fun configureService(){
        Log.i(logTag, "Configuring Service")
        alive = true
    }

    private fun initializeService(){
        Log.i(logTag, "Initializing music service")
        setSongs()
        songsIterator = songs?.listIterator()
        history = mutableListOf()
        musicPlayerApi = MusicPlayerApiImpl(history)
        playNext()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(logTag, "Binding music service")
        // A client is binding to the service with bindService()
        return musicPlayerApi
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i(logTag, "Unbinding music service")
        return allowRebind
    }

    private fun notificate(notificationTitle:String, notificationText:String ){
        Log.i(logTag, "Notification handling")
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = Notification.Builder(this,
            MainActivity.CHANNEL_NOTIFICATION_ID
        )
            .setOngoing(true)
            .setContentTitle(notificationTitle)
            .setTicker(notificationText)
            .setContentText(notificationText)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setWhen(System.currentTimeMillis())
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(MainActivity.CHANNEL_ID, notification)
    }

    private fun setSongs() {
        Log.i(logTag, "Set songs")
        songs =  listOf<Song>(
            Song("Mozart ","Eine kleine Nachtmusik"),
            Song("Beethoven","Für Elise"),
            Song("Puccini","O mio babbino caro"),
            Song("Beethoven","Symphony No. 3"),
            Song("Shostakovich","Symphony No. 11"),
            Song("Chopin","Ballade No. 1"),
            Song("Stockhausen","Klavierstück VI"),
            Song("Anna Thorvaldsdottir","Aequilibria"))
    }

    fun playNext(){
        Log.i(logTag, "Play next")
        if (songsIterator!!.hasNext()) {
            val nextSong = songsIterator?.next()
            addSongToHistory(nextSong as Song)
            notificate(nextSong!!.songName, nextSong.bandName)
        }
    }

    fun playPrev(){
        Log.i(logTag, "Play previous")
        if (songsIterator!!.hasPrevious()) {
            val prevSong = songsIterator?.previous()
            addSongToHistory(prevSong as Song)
            notificate(prevSong!!.songName, prevSong.bandName)
        }
    }

    private fun addSongToHistory(song : Song){
        Log.i(logTag, "Adding song to history")
        history?.add(song)
    }

    fun gethistory() : List<Song>?{
        Log.i(logTag, "Getting history")
        return history
    }

    inner class MusicPlayerApiImpl(override val history: List<Song>?) : Binder(), MusicPlayerApi {
        override fun playNextItem() {
            return this@MusicPlayerService.playNext()
        }
        override fun queryHistory(): List<Song>? {
            return this@MusicPlayerService.gethistory()
        }
    }
}

