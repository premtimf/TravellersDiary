package com.example.travellersdiary.addPlace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.travellersdiary.BuildConfig
import com.example.travellersdiary.databinding.ActivityAddPlaceBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class AddPlaceActivity : AppCompatActivity(), AddPlaceContract.View {

    private lateinit var binding: ActivityAddPlaceBinding
    private lateinit var presenter: AddPlaceContract.Presenter

    private lateinit var currentPhotoUri: Uri
    private var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = AddPlacePresenter(this, AddPlaceRepo())
        setClickListeners()
        processIntent()
    }

    private fun processIntent() {
        if (intent != null) {
            if (intent.hasExtra(LATITUDE) && intent.hasExtra(LONGITUDE)) {
                val latitude = intent.getDoubleExtra(LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(LONGITUDE, 0.0)
                presenter.initializePlace(latitude, longitude)
            }
        }
    }

    private fun setClickListeners() {
        binding.addPhotoButton.setOnClickListener {
            tryToTakePhoto()
        }
        binding.savePlaceButton.setOnClickListener {
            presenter.savePlace()
        }
    }

    private fun tryToTakePhoto() {
        val readPermissionsGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val cameraPermissionsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val permissionsGranted = (readPermissionsGranted && cameraPermissionsGranted)
        when {
            permissionsGranted -> takePhoto()
            shouldRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA) ->
                showMessage("Permissions to add photo needed")
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    PERMISSIONS_CODE
                )
            }
        }
    }

    override fun showMessage(message: String) {
        Snackbar.make(binding.rootConstraintLayout, message, Snackbar.LENGTH_SHORT)
            .setAction("Okay") {}
            .show()
    }

    private fun shouldRequestPermissionRationale(vararg permissions: String): Boolean {
        var shouldShowRequestPermissionRationale = false
        permissions.forEach { permission ->
            shouldShowRequestPermissionRationale =
                shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }
        return shouldShowRequestPermissionRationale
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also { file ->
                    fileName = file.name
                    currentPhotoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".android.fileprovider", file)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                    startActivityForResult(takePictureIntent, TAKE_PHOTO)
                }
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return presenter.createFile(storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, currentPhotoUri.toString())
            presenter.uploadImage(currentPhotoUri, fileName)
        }
    }

    companion object {

        private const val TAG = "AddPlaceActivity"

        private const val PERMISSIONS_CODE = 4321
        private const val TAKE_PHOTO = 1234
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"

        fun newIntent(context: Context, lat: Double?, lon: Double): Intent {
            return Intent(context, AddPlaceActivity::class.java).apply {
                putExtra(LATITUDE, lat)
                putExtra(LONGITUDE, lon)
            }
        }
    }

    override val placeName: String
        get() = binding.placeNameText.text.toString()

    override val photoLink: String
        get() = binding.photoLinkText.text.toString()

    override fun setUrlText(url: String) {
        binding.photoLinkText.setText(url)
    }
}