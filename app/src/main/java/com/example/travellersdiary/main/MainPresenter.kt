package com.example.travellersdiary.main

import android.util.Log
import com.example.travellersdiary.models.RealmPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val view: MainContract.View,
    private val repo: MainContract.Repository
) : MainContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun checkIfUserIsSignedIn() {
        val authenticatedUser = repo.getAuthenticatedUser()
        if (authenticatedUser == null) {
            view.launchSignInActivity()
        }
    }

    override fun loadPlaces() {
        val disposable = repo.getPlacesFromCache()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ placesReturned ->
                if (placesReturned.isNullOrEmpty()) {
                    view.showNoPlacesMessage("No favourite places yet! Long click to add a favourite place")
                } else {
                    view.showPlaces(placesReturned)
                    placesReturned.forEach {
                        Log.d(TAG, it.toString())
                    }
                    view.removeNoPlacesMessage()
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
                        Log.d(TAG, "1 place searched")
                        val realmPlace: RealmPlace? = place.getValue(RealmPlace::class.java)
                        if (realmPlace != null) {
                            repo.savePlaceInCache(realmPlace)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    view.showMessage("Failed to load places")
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
                view.showMessage("Failed to update places from database")
            }
        })
    }

    override fun clearRealm() {
        repo.clearRealm()
    }

    override fun makeMarkerOptions(place: RealmPlace): MarkerOptions? {
        val placesCoordinates = place.position
        if (placesCoordinates != null) {
            return MarkerOptions()
                .position(LatLng(placesCoordinates.latitude, placesCoordinates.longitude))
                .title(place.name)
        }
        return null
    }

    override fun stop() {
        compositeDisposable.clear()
    }

    companion object {
        private const val TAG = "MainPresenter"
    }
}
