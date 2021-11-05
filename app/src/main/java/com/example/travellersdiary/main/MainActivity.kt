package com.example.travellersdiary.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travellersdiary.R
import com.example.travellersdiary.SignInActivity
import com.example.travellersdiary.addPlace.AddPlaceActivity
import com.example.travellersdiary.databinding.ActivityMainBinding
import com.example.travellersdiary.models.RealmPlace
import com.example.travellersdiary.places.PlacesActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener,
    MainContract.View {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

    private lateinit var presenter: MainContract.Presenter

    private lateinit var noPlacesSnackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name)

        presenter = MainPresenter(this, MainRepo())

        presenter.checkIfUserIsSignedIn()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    public override fun onStart() {
        super.onStart()
        presenter.checkIfUserIsSignedIn()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                signOut()
                true
            }
            R.id.places_list -> {
                startActivity(Intent(this, PlacesActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun launchSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
        return
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            if (it.isSuccessful) {
                presenter.clearRealm()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this,
                    "There was an error signing out",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        mMap.setInfoWindowAdapter(MarkerInfoAdapter(this))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.656141630821416, 21.151792722748393)))
        loadContent()
    }

    override fun onResume() {
        super.onResume()
        loadContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    private fun loadContent() {
        presenter.loadPlaces()
        presenter.updatePlacesFromBackend()
    }

    override fun onMapLongClick(p0: LatLng) {
        val intent = AddPlaceActivity.newIntent(this, p0.latitude, p0.longitude)
        startActivity(intent)
        Log.d(TAG, "LongClicked")
    }

    override fun showMessage(message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAction(R.string.ok) {}
            .show()
    }

    override fun showPlaces(favouritePlaces: List<RealmPlace>) {
        favouritePlaces.forEach { place ->
            val markerOptions = presenter.makeMarkerOptions(place)
            if (markerOptions != null) {
                val marker = mMap.addMarker(markerOptions)
                marker?.tag = place
            }
        }
    }

    override fun showNoPlacesMessage(noPlaces: Int) {
        noPlacesSnackbar = Snackbar.make(binding.root, noPlaces, Snackbar.LENGTH_INDEFINITE)
        noPlacesSnackbar.setAction(R.string.ok) {}
            .show()
    }

    override fun removeNoPlacesMessage() {
        noPlacesSnackbar.dismiss()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent != null && intent.hasExtra(LATITUDE)) {
            val latitude = intent.getDoubleExtra(LATITUDE, 0.0)
            val longitude = intent.getDoubleExtra(LONGITUDE, 0.0)
            moveCamera(latitude, longitude)
        }
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        val position = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 6.0f))
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"

        fun newIntent(context: Context, latitude: Double?, longitude: Double?): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(LATITUDE, latitude)
                putExtra(LONGITUDE, longitude)
            }
        }
    }
}