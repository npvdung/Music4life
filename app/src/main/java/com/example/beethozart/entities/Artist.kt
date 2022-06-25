package com.example.beethozart.entities


data class Artist(
        val artistName: String = "Unknown",
        var songs: MutableList<Song> = mutableListOf()
) {

    fun addSong(song: Song) {
        songs.add(song)
    }

    fun getNumSongs(): Int {
        return songs.size
    }
}