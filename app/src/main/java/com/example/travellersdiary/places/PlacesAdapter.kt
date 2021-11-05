package com.example.travellersdiary.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travellersdiary.R
import com.example.travellersdiary.models.RealmPlace

class PlacesAdapter(private val placeClickListener: PlaceClickListener) : RecyclerView.Adapter<PlacesAdapter.PlacesBaseVH>() {

    private var places = emptyList<RealmPlace>()

    abstract class PlacesBaseVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(item: RealmPlace, position: Int)
    }

    inner class PlaceViewHolder(itemView: View) : PlacesBaseVH(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bindView(item: RealmPlace, position: Int) {
            itemView.findViewById<TextView>(R.id.placeNameText).text = item.name
            val placePic = itemView.findViewById<ImageView>(R.id.placePicImageView)
            Glide.with(placePic.context)
                .load(item.picture)
                .into(placePic)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                placeClickListener.onPlaceClicked(places[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesBaseVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesBaseVH, position: Int) {
        holder.bindView(places[position], position)
    }

    override fun getItemCount() = places.size
    fun setPlaces(placesReturned: List<RealmPlace>) {
        places = placesReturned
        notifyItemRangeChanged(0, placesReturned.size)
    }

}

interface PlaceClickListener {
    fun onPlaceClicked(realmPlace: RealmPlace)
}
