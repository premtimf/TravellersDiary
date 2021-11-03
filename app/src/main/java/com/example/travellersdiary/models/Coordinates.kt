package com.example.travellersdiary.models

import io.realm.RealmObject

open class PlaceCoordinates: RealmObject() {

    var latitude: Double = 0.0

    var longitude: Double = 0.0
}
