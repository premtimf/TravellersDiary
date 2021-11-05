package com.example.travellersdiary.models

import com.google.android.gms.maps.model.LatLng

data class Place(
    val id: String,
    val name: String,
    val position: LatLng?,
    val picture: String)
