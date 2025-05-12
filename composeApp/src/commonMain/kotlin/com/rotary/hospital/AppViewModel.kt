package com.rotary.hospital

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    var currentPatient = mutableStateOf<Patient?>(null)
}