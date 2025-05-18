package com.rotary.hospital

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath:()-> String): DataStore<Preferences>{
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {producePath().toPath()}
    )
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"