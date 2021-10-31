package com.example.travellersdiary.main

import com.example.travellersdiary.models.RealmPlace
import io.reactivex.Observable

class MainContract {

    interface View {
        fun showMessage(message: String)

        fun showPlaces(favouritePlaces: List<RealmPlace>)

    }

    interface Presenter {
        fun loadPlaces()
    }

    interface Repository {

        fun getPlacesFromCache() : Observable<List<RealmPlace>>?

    }

}
