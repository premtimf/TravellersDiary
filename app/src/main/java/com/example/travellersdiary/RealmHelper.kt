package com.example.travellersdiary

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.exceptions.RealmMigrationNeededException
import org.jetbrains.annotations.NotNull

object RealmHelper {

    private const val DB_NAME = "travellers.db"
    private var realmInstance: Realm? = null

    private fun getRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder()
            .name(DB_NAME)
            .schemaVersion(1)
            .build()
    }

    @Synchronized
    @NotNull
    fun getInstance(): Realm? {
        if (realmInstance == null) {
            try {
                realmInstance = Realm.getInstance(getRealmConfiguration())
            } catch (e: RealmMigrationNeededException) {
                Realm.deleteRealm(getRealmConfiguration())
            }
        }
        return realmInstance
    }
}