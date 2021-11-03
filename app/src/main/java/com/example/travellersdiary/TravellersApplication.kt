package com.example.travellersdiary

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import io.realm.Realm
import com.google.firebase.FirebaseOptions


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