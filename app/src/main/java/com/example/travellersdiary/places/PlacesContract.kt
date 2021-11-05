package com.example.travellersdiary.places

import com.example.travellersdiary.models.RealmPlace

class PlacesContract {

    interface View {

        fun showPlaces(placesReturned: List<RealmPlace>)

        fun showMessage(message: Int)

    }

    interface Presenter {

        fun loadPlaces()

        fun updatePlacesFromBackend()

        fun stop()

    }

    interface Repository {

    }

}
