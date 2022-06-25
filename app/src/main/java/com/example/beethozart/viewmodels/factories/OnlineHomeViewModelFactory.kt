package com.example.beethozart.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beethozart.databases.daos.SongDatabaseDao
import com.example.beethozart.viewmodels.OnlineHomeViewModel


class OnlineHomeViewModelFactory(
    private val dataSource: SongDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnlineHomeViewModel::class.java)) {
            return OnlineHomeViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}