package org.setu.bookshoptracker.main

import android.app.Application
import org.setu.bookshoptracker.AppData
import timber.log.Timber

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Bookshop Tracker MainApp initialized")
        AppData.initialize(this)
    }
}