package com.example.travellersdiary.addPlace

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import java.io.File

class AddPlaceContract {

    interface View {

        val placeName: String

        val photoLink: String

        fun setUrlText(url: String)

        fun showMessage(message: Int)

    }

    interface Presenter {

        fun createFile(storageDir: File?): File?

        fun uploadImage(currentPhotoUri: Uri, fileName: String)

        fun initializePlace(latitude: Double, longitude: Double)

        fun savePlace()

    }

    interface Repository {

        fun getUploadImageRef(fileName: String): Any

        fun getUserId(): String?

        fun getPlacesRef(authenticatedUserId: String): DatabaseReference

    }
}
