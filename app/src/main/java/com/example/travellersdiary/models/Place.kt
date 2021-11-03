package com.example.travellersdiary.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Place(val uid: String, val name: String, val description: String, val position: LatLng, val picture: String) {
    @Exclude
    fun toMap(): Map<String, Any> {

        return mapOf(
            "uid" to uid,
            "name" to name,
            "description" to description,
            "position" to position,
            "picture" to picture
        )

    }
}
