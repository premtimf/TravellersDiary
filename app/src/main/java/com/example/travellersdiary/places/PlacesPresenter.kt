package com.example.travellersdiary.places

import android.util.Log
import com.example.travellersdiary.R
import com.example.travellersdiary.main.MainContract
import com.example.travellersdiary.models.RealmPlace
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class PlacesPresenter(val view: PlacesContract.View, val repo: MainContract.Repository) : PlacesContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun loadPlaces() {
        val disposable = repo.getPlacesFromCache()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ placesReturned ->
                if (!placesReturned.isNullOrEmpty()) {
                    view.showPlaces(placesReturned)
                }
            }, { throwable ->
                Log.d(TAG, "${throwable.printStackTrace()}")
            }
            )
        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }

    override fun updatePlacesFromBackend() {
        val authenticatedUserId = repo.getAuthenticatedUser()?.uid
        if (authenticatedUserId != null) {
            repo.getUsersPlacesRef(authenticatedUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (place in snapshot.children) {
                        val realmPlace: RealmPlace? = place.getValue(RealmPlace::class.java)
                        if (realmPlace != null) {
                            repo.savePlaceInCache(realmPlace)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    view.showMessage(R.string.failed_to_load_places)
                }
            })
            addSnapShotOnChangeListener(authenticatedUserId)
        }
    }

    private fun addSnapShotOnChangeListener(authenticatedUserId: String) {
        repo.getUsersPlacesRef(authenticatedUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (place in snapshot.children) {
                    val realmPlace: RealmPlace? = place.getValue(RealmPlace::class.java)
                    if (realmPlace != null) {
                        repo.savePlaceInCache(realmPlace)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                view.showMessage(R.string.failed_to_update_places)
            }
        })
    }

    override fun stop() {
        compositeDisposable.clear()
    }

    companion object {
        private const val TAG = "PlacesPresenter"
    }

}
