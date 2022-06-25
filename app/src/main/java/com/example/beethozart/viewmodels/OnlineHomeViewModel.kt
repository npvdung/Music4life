package com.example.beethozart.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beethozart.databases.daos.SongDatabaseDao
import com.example.beethozart.entities.Song
import com.example.beethozart.entities.SongList
import com.example.beethozart.network.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class OnlineHomeViewModel(private val database: SongDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _historySongList = MutableLiveData<List<Song>>()
    val historySongList : LiveData<List<Song>>
    get() = _historySongList

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong

    private val _listSong = MutableLiveData<List<Song>>()
    val listSong: LiveData<List<Song>>
        get() = _listSong

    init {
        _currentSong.value = null
    }

    var currentUser = database.getUser()

    fun clearUser() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteUser()
            }
        }
    }


    fun postHistory(history : HistoryProperty) {
        Api.retrofitService.pushHistory(history).enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }

        })
    }


    fun getHistory(username : String) {
        Api.retrofitService.getHistory(UserFromServer(username)).enqueue(object : retrofit2.Callback<List<Song>>{
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                _listSong.value = response?.body()
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Log.d("aaa" , "fail")
            }

        })
    }

    fun getSong(titleOrArtist: String) {
        Api.retrofitService.getSearchSong(SearchSongProperty(titleOrArtist))
            .enqueue(object : retrofit2.Callback<List<Song>> {
                override fun onResponse(
                    call: Call<List<Song>>,
                    response: Response<List<Song>>
                ) {
                    val httpCode = response.code()
                    if (httpCode == 404) {

                    } else if (httpCode == 200) {
                        _listSong.value = response.body()
                    }
                }
                override fun onFailure(call: Call<List<Song>>, t: Throwable) {

                }
            })
    }
    fun getSongList(): SongList {
        return SongList(listSong.value!!.toMutableList())
    }

    fun onSongClicked(song: Song) {
        _currentSong.value = song
    }

    fun onPlayerNavigated() {
        _currentSong.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}