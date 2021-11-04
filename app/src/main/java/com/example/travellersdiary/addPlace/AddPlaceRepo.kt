package com.example.travellersdiary.addPlace

import com.example.travellersdiary.FirebaseHelper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddPlaceRepo : AddPlaceContract.Repository {

    override fun getUploadImageRef(fileName: String): StorageReference {
        return FirebaseStorage.getInstance().reference.child("images/$fileName")
    }

    override fun getUserId(): String? {
        return FirebaseHelper.getAuth().uid
    }

    override fun getPlacesRef(authenticatedUserId: String): DatabaseReference {
        return FirebaseHelper.getUserPlacesRef(authenticatedUserId)
    }

}
