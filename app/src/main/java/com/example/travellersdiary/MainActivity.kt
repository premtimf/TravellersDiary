package com.example.travellersdiary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travellersdiary.databinding.ActivityMainBinding
import com.example.travellersdiary.main.MainContract
import com.example.travellersdiary.main.MainPresenter
import com.example.travellersdiary.main.MainRepo
import com.example.travellersdiary.models.RealmPlace
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener,
    MainContract.View {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name)

        presenter = MainPresenter(this, MainRepo())

        auth = Firebase.auth

        checkIfUserIsSignedIn()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkIfUserIsSignedIn() {
        // Check if user is signed in.
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }

    public override fun onStart() {
        super.onStart()
        checkIfUserIsSignedIn()
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            if (it.isSuccessful) {
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        Log.d("Premt", "Map is ready")
        presenter.loadPlaces()

    }

    override fun onMapLongClick(p0: LatLng) {
        val place = Place("DefaultName", p0)
        Log.d("MainActivity", "LongClicked")
        mMap.addMarker(
            MarkerOptions()
                .title(place.name)
                .position(place.position)
        )
    }

    override fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Okay") {}
            .show()
    }

    override fun showPlaces(favouritePlaces: List<RealmPlace>) {
        // Add a marker in Prishtina and move the camera
        val prishtina = LatLng(42.66, 21.16)
        mMap.addMarker(MarkerOptions().position(prishtina).title("Prishtina"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(prishtina))
    }
}