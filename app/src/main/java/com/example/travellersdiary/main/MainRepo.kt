package com.example.travellersdiary.main

import com.example.travellersdiary.RealmHelper
import com.example.travellersdiary.models.RealmPlace
import io.reactivex.Observable

class MainRepo: MainContract.Repository {

    override fun getPlacesFromCache(): Observable<List<RealmPlace>>? {
        return RealmHelper.getInstance()
            ?.where(RealmPlace::class.java)
            ?.findAllAsync()
            ?.asChangesetObservable()
            ?.map { it.collection }
    }

}
