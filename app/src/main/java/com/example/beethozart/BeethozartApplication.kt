package com.example.beethozart

import android.app.Application
import timber.log.Timber

class BeethozartApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}