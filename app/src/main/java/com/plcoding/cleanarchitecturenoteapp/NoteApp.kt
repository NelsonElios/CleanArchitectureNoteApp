package com.plcoding.cleanarchitecturenoteapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp



// This application must be added to the AndroidManifest
@HiltAndroidApp
class NoteApp: Application() {
}