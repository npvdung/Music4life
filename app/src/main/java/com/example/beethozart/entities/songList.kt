package com.example.beethozart.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.ThreadLocalRandom

enum class PlaybackMode {

}

@Parcelize
data class SongList(
    private val songList: MutableList<Song> = mutableListOf()
): Parcelable {

    val size: Int
      get() = songList.size

    private fun shuffle() : SongList {
        val rnd: Random = ThreadLocalRandom.current();

        val shuffledSongList = songList.toMutableList()
        for (song in songList)
            shuffledSongList.add(song)

        for (i in shuffledSongList.indices) {
            val index = rnd.nextInt(i + 1)
            val temp = songList[i]
            shuffledSongList[i] = shuffledSongList[index]
            shuffledSongList[index] = temp
        }

        return SongList(shuffledSongList)
    }

    operator fun get(id: Int): Song {
        return songList[id]
    }

    fun toList(): List<Song> {
        return songList
    }

    fun beginWith(song: Song?): SongList {
        for (i in songList.indices) {
            if (songList[i] == song) {
                val rotatedSongList = mutableListOf<Song>()
                for (j in songList.indices) {
                    if (j >= i)
                        rotatedSongList.add(songList[j])
                }

                for (j in songList.indices) {
                    if (j < i)
                        rotatedSongList.add(songList[j])
                }

                return SongList(rotatedSongList)
            }
        }

        return this
    }
}