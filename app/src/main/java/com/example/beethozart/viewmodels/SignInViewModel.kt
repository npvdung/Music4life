package com.example.beethozart.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.beethozart.databases.daos.SongDatabaseDao
import com.example.beethozart.entities.User
import kotlinx.coroutines.*

class SignInViewModel(private val database: SongDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var userGet = database.getUser()

    fun addUser(username: String, password: String) {
        val user = User(username, password)
        if (userGet.value?.size == 0) {
            uiScope.launch {
                insert(user)
            }
        }
    }

    private suspend fun insert(user: User) {
        Log.d("aaa" , "Ditmemay")
        withContext(Dispatchers.IO) {
            database.insertUser(user)
            Log.d("aaa" , "Add user successful")
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}