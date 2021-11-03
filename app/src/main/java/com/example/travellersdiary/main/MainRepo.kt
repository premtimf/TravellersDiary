package com.example.travellersdiary.main

import android.util.Log
import com.example.travellersdiary.FirebaseHelper
import com.example.travellersdiary.RealmHelper
import com.example.travellersdiary.models.RealmPlace
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import io.reactivex.Observable
import io.realm.exceptions.RealmPrimaryKeyConstraintException

class MainRepo: MainContract.Repository {

    override fun getPlacesFromCache(): Observable<List<RealmPlace>>? {
        return RealmHelper.getInstance()
            ?.where(RealmPlace::class.java)
            ?.findAllAsync()
            ?.asChangesetObservable()
            ?.map { it.collection }
    }

    override fun getUsersPlacesRef(uid: String): DatabaseReference {
        return FirebaseHelper.getUserPlacesRef(uid)
    }

    override fun getAuthenticatedUser(): FirebaseUser? {
        return FirebaseHelper.getAuth().currentUser
    }

    override fun savePlaceInCache(realmPlace: RealmPlace) {
        RealmHelper.getInstance()?.executeTransactionAsync { realm ->
            try {
                Log.d("RealmHelper", "Thread in realm: ${Thread.currentThread().name}")
                realm.insertOrUpdate(realmPlace)
            } catch (e: RealmPrimaryKeyConstraintException) {
                e.printStackTrace()
            }
        }
    }

    override fun clearRealm() {
        RealmHelper.deleteRealm()
    }


}
