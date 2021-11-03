package com.example.travellersdiary.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.travellersdiary.R
import com.example.travellersdiary.models.RealmPlace
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(p0: Marker): View? {
        val place = p0.tag as RealmPlace

        val view = LayoutInflater.from(context).inflate(
            R.layout.info_window_content, null
        )

        view.findViewById<TextView>(
            R.id.placeTitleTextView
        ).text = place.name

        return view
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }
}