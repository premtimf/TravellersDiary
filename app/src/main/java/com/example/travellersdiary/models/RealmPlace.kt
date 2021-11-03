package com.example.travellersdiary.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmPlace: RealmObject() {

    @PrimaryKey
    var id = ""

    var name = ""

    var position: PlaceCoordinates? = null

    override fun toString(): String {
        return "RealmPlace(id='$id', name='$name', position=$position)"
    }


}