package com.parth.pestotest.core.application

import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance()
    }
}