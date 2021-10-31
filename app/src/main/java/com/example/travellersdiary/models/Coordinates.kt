package com.example.travellersdiary.models

import io.realm.RealmObject

open class PlaceCoordinates: RealmObject() {

    var lat: Double? = null

    var lon: Double? = null
}
