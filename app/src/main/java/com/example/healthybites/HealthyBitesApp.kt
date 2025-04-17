package com.example.healthybites

import android.app.Application
import com.google.firebase.FirebaseApp

class HealthyBitesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}