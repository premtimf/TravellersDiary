package com.example.travellersdiary.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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

        val placePic = view.findViewById<ImageView>(R.id.placeImageView)
        val requestOptions: RequestOptions = RequestOptions()
            .transform(CircleCrop())
        Glide.with(placePic.context)
            .load(place.picture)
            .apply(requestOptions)
            .override(100, 100)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    placePic.visibility = View.GONE
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    placePic.setImageDrawable(resource)
                    placePic.visibility = View.VISIBLE
                    return true
                }
            })
            .into(placePic)

        return view
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }
}