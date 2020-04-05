package ch.hslu.mobpro.uebung3.servicesreceiver

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class MusicPlayerConnection : ServiceConnection {
    private var musicPlayerApi: MusicPlayerApi? = null
    override fun onServiceDisconnected(name: ComponentName?) {
        musicPlayerApi = null
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        musicPlayerApi = service as MusicPlayerApi
    }

    fun getApi(): MusicPlayerApi?{
        return musicPlayerApi
    }
}