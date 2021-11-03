package com.example.travellersdiary

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    fun getDb(): FirebaseDatabase {
        return Firebase.database
    }

    fun getUserPlacesRef(userId: String): DatabaseReference {
        return getDb().reference.child("users").child(userId).child("places")
    }

    fun getAuth(): FirebaseAuth {
        return Firebase.auth
    }
}