package com.example.travellersdiary

import android.app.Application
import android.util.Log
import io.realm.Realm

class TravellersApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        RealmHelper.getInstance()
        Log.d("TravellersApp", "Realm created")
    }
}