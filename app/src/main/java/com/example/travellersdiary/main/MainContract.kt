package com.example.travellersdiary.main

import com.example.travellersdiary.models.RealmPlace
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import io.reactivex.Observable

class MainContract {

    interface View {
        fun showMessage(message: String)

        fun showPlaces(favouritePlaces: List<RealmPlace>)

        fun showNoPlacesMessage(noPlaces: String)

        fun removeNoPlacesMessage()

        fun launchSignInActivity()

    }

    interface Presenter {

        fun checkIfUserIsSignedIn()

        fun loadPlaces()

        fun updatePlacesFromBackend()

        fun clearRealm()

        fun makeMarkerOptions(place: RealmPlace): MarkerOptions?

        fun stop()
    }

    interface Repository {

        fun getAuthenticatedUser(): FirebaseUser?

        fun getPlacesFromCache() : Observable<List<RealmPlace>>?

        fun getUsersPlacesRef(uid: String): DatabaseReference

        fun savePlaceInCache(realmPlace: RealmPlace)

        fun clearRealm()

    }

}
