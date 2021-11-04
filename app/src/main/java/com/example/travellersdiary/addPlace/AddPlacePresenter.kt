package com.example.travellersdiary.addPlace

import android.net.Uri
import com.example.travellersdiary.models.Place
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddPlacePresenter(private val view: AddPlaceContract.View, private val addPlaceRepo: AddPlaceRepo) : AddPlaceContract.Presenter {

    private var placePosition: LatLng? = null

    override fun createFile(storageDir: File?): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    override fun uploadImage(currentPhotoUri: Uri, fileName: String) {
        val imageStorage = addPlaceRepo.getUploadImageRef(fileName)
        val uploadTask = imageStorage.putFile(currentPhotoUri)
        uploadTask.addOnSuccessListener {
            view.showMessage("Image Uploadad")
        }.addOnFailureListener {
            view.showMessage("Failed to upload image")
        }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageStorage.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                view.setUrlText(downloadUri.toString())
            } else {
                view.showMessage("Failed to get link")
            }
        }
    }

    override fun initializePlace(latitude: Double, longitude: Double) {
        placePosition = LatLng(latitude, longitude)
    }

    override fun savePlace() {
        val authenticatedUserId = addPlaceRepo.getUserId()
        if (authenticatedUserId != null) {
            val placesRef = addPlaceRepo.getPlacesRef(authenticatedUserId)
            val placeRef = placesRef.push()
            val key = placeRef.key
            if (key != null) {
                val place = Place(key, view.placeName, placePosition, view.photoLink)
                placeRef.setValue(place).addOnSuccessListener {
                    view.showMessage("Place inserted successfully")
                }.addOnFailureListener {
                    view.showMessage("Failed to save place")
                }
            }
        }
    }

}
