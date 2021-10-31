package com.example.travellersdiary.models

import io.realm.RealmObject

open class RealmPlace: RealmObject() {

    var name = ""

    var position: PlaceCoordinates? = null
}