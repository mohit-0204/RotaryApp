package com.rotary.hospital

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rotary.hospital.core.data.preferences.DATA_STORE_FILE_NAME
import com.rotary.hospital.core.data.preferences.createDataStore

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }

}