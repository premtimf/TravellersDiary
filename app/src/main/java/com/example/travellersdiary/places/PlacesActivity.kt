package com.example.travellersdiary.places

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travellersdiary.R
import com.example.travellersdiary.databinding.ActivityPlacesBinding
import com.example.travellersdiary.main.MainActivity
import com.example.travellersdiary.main.MainRepo
import com.example.travellersdiary.models.RealmPlace
import com.google.android.material.snackbar.Snackbar

class PlacesActivity : AppCompatActivity(), PlacesContract.View, PlaceClickListener {

    private lateinit var binding: ActivityPlacesBinding
    private lateinit var presenter: PlacesContract.Presenter
    private lateinit var adapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = PlacesPresenter(this, MainRepo())
        adapter = PlacesAdapter(this)

        setupRecyclerView()

        loadContent()
    }

    private fun setupRecyclerView() {
        binding.placesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.placesRecyclerView.adapter = adapter
    }

    private fun loadContent() {
        presenter.loadPlaces()
        presenter.updatePlacesFromBackend()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun showPlaces(placesReturned: List<RealmPlace>) {
        adapter.setPlaces(placesReturned)
    }

    override fun showMessage(message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAction(R.string.ok) {}
            .show()
    }

    override fun onPlaceClicked(realmPlace: RealmPlace) {
        startActivity(MainActivity.newIntent(this, realmPlace.position?.latitude, realmPlace.position?.longitude))
    }
}