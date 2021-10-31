package com.example.travellersdiary.main

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val view: MainContract.View,
    private val repo: MainContract.Repository
) : MainContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun loadPlaces() {
        val disposable = repo.getPlacesFromCache()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ placesReturned ->
                if (placesReturned.isNullOrEmpty()) {
                    view.showMessage("No favourite places yet! Long click to add a favourite place")
                } else {
                    view.showPlaces(placesReturned)
                }

            }, { throwable ->
                Log.d(TAG, "${throwable.printStackTrace()}")
            }
            )
        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }

    companion object {
        private const val TAG = "MainPresenter"
    }
}
