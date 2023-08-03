package com.psijuego

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.psijuego.core.utils.CoreModule
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PsiJuegoApp: Application() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = Firebase.analytics
        CoreModule.init(this)
    }
}