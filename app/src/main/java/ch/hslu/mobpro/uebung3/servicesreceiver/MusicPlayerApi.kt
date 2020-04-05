package ch.hslu.mobpro.uebung3.servicesreceiver

interface MusicPlayerApi {
    fun playNextItem(): Unit
    fun queryHistory(): List<Song>?
    val history: List<Song>?
}